package com.pkos.backend.mapper;

import com.pkos.backend.dto.request.CreateNotebookRequest;
import com.pkos.backend.dto.request.UpdateNotebookRequest;
import com.pkos.backend.dto.response.NotebookResponse;
import com.pkos.backend.entity.Notebook;
import org.springframework.stereotype.Component;

@Component
public class NotebookMapper {

    public Notebook toEntity(CreateNotebookRequest request) {

        Notebook notebook = new Notebook();
        notebook.setName(request.getName());

        return notebook;
    }

    public void updateEntity(Notebook notebook, UpdateNotebookRequest request) {
        notebook.setName(request.getName());
    }

    public NotebookResponse toResponse(Notebook notebook) {

        return NotebookResponse.builder()
                .id(notebook.getId())
                .name(notebook.getName())
                .createdAt(notebook.getCreatedAt())
                .updatedAt(notebook.getUpdatedAt())
                .build();
    }
}