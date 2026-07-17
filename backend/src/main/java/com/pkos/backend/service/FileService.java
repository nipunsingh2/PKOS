package com.pkos.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pkos.backend.dto.response.FileResponse;
import com.pkos.backend.entity.FileMetadata;
import com.pkos.backend.entity.SupportedFileType;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.pkos.backend.service.content.FileContentService;



@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;

    private final CurrentUserService currentUserService;

    private final AuditService auditService;

    private static final String UPLOAD_DIR = "uploads";

    private final FileContentService fileContentService;

    private static final Logger logger =
        LoggerFactory.getLogger(FileService.class);

    public FileResponse uploadFile(
        MultipartFile file) throws IOException {
    validateFile(file);
    User currentUser = currentUserService.getCurrentUser();

    Path uploadPath = Paths.get(UPLOAD_DIR);

    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
    }

    String originalFileName = file.getOriginalFilename();

    String extension = "";

    if (originalFileName != null && originalFileName.contains(".")) {
        extension = originalFileName.substring(
                originalFileName.lastIndexOf("."));
    }

    String storedFileName = UUID.randomUUID() + extension;

    Path filePath = uploadPath.resolve(storedFileName);

    // Save the physical file
    file.transferTo(filePath);

    // Create metadata
    FileMetadata fileMetadata = FileMetadata.builder()
            .fileName(originalFileName)
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .storagePath(filePath.toString())
            .user(currentUser)
            .build();

    FileMetadata savedFile = fileMetadataRepository.save(fileMetadata);
    fileContentService.process(savedFile);

    auditService.logEvent(
            "Uploaded File",
            currentUser.getEmail()
    );

    return FileResponse.builder()
            .id(savedFile.getId())
            .fileName(savedFile.getFileName())
            .contentType(savedFile.getContentType())
            .fileSize(savedFile.getFileSize())
            .uploadedAt(savedFile.getUploadedAt())
            .downloadUrl("/api/files/" + savedFile.getId())
            .build();
    }
    
        @Transactional(readOnly = true)
        public Resource downloadFile(Long id)
        throws MalformedURLException {
                User currentUser = currentUserService.getCurrentUser();
                FileMetadata fileMetadata = fileMetadataRepository
            .findByIdAndUser(id, currentUser)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "File not found"));

    Path filePath = Paths.get(fileMetadata.getStoragePath());

    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists() || !resource.isReadable()) {
        throw new ResourceNotFoundException("File not found.");
    }

    return resource;
}
    @Transactional
    public void deleteFile(Long id) throws IOException {

        User currentUser = currentUserService.getCurrentUser();

        FileMetadata fileMetadata = fileMetadataRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("File not found."));

        Path filePath = Paths.get(fileMetadata.getStoragePath());

        if (!Files.deleteIfExists(filePath)) {
            logger.warn(
                    "Physical file already missing: {}",
                    fileMetadata.getStoragePath()
            );
        }

        fileMetadataRepository.delete(fileMetadata);

        auditService.logEvent(
                "Deleted File",
                currentUser.getEmail()
        );

        logger.info(
                "File deleted successfully. File ID: {}, User: {}",
                fileMetadata.getId(),
                currentUser.getEmail()
        );
    }




    @Transactional(readOnly = true)
    public Page<FileResponse> getFiles(
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = currentUserService.getCurrentUser();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return fileMetadataRepository
                .findByUser(currentUser, pageable)
                .map(file -> FileResponse.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .contentType(file.getContentType())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .downloadUrl("/api/files/" + file.getId())
                        .build());
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File cannot be empty.");
        }

        String contentType = file.getContentType();

        if (SupportedFileType.from(contentType) == SupportedFileType.UNKNOWN) {
                throw new IllegalArgumentException("Unsupported file type.");
        }
    }


}