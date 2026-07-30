package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.entity.Note;

public interface SemanticRetrievalService {

    List<Note> retrieveRelevantNotes(String query);

}