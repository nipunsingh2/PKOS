package com.pkos.backend.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiChatResponse {

    private Long conversationId;

    private String answer;

    private List<SourceNoteResponse> sources;

}