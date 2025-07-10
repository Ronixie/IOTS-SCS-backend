package com.hwadee.IOTS_SCS.service;

import com.hwadee.IOTS_SCS.entity.DTO.CreateNoteDTO;
import com.hwadee.IOTS_SCS.entity.DTO.UpdateNoteDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Note;
import java.util.List;

public interface NoteService {
    List<Note> getNotes(String courseId, String lessonId);
    Note createNote(CreateNoteDTO createNoteDTO);
    Note getNoteById(String noteId);
    Note updateNote(String noteId, UpdateNoteDTO updateNoteDTO);
    void deleteNote(String noteId);
}