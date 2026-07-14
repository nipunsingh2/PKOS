package com.pkos.backend.service;

import com.pkos.backend.dto.response.FileResponse;
import com.pkos.backend.entity.FileMetadata;
import com.pkos.backend.entity.User;
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

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;

    private final CurrentUserService currentUserService;

    private final AuditService auditService;

    private static final String UPLOAD_DIR = "uploads";

    public FileResponse uploadFile(
        MultipartFile file) throws IOException {

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

}