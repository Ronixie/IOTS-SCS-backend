// 文件路径: com/hwadee/IOTS_SCS/service/impl/NoteServiceImpl.java
package com.hwadee.IOTS_SCS.service.impl;

import com.hwadee.IOTS_SCS.entity.DTO.CreateNoteDTO;
import com.hwadee.IOTS_SCS.entity.DTO.UpdateNoteDTO;
import com.hwadee.IOTS_SCS.mapper.NoteMapper;
import com.hwadee.IOTS_SCS.entity.POJO.Note;
import com.hwadee.IOTS_SCS.service.NoteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public List<Note> getNotes(String courseId, String lessonId) {
        return noteMapper.selectNotes(courseId, lessonId);
    }

    @Override
    public Note createNote(CreateNoteDTO createNoteDTO) {
        Note note = new Note();
        BeanUtils.copyProperties(createNoteDTO, note);
        note.setCreateTime(LocalDateTime.now());
        note.setUpdateTime(LocalDateTime.now());
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Note getNoteById(String noteId) {
        return noteMapper.selectById(noteId);
    }

    @Override
    public Note updateNote(String noteId, UpdateNoteDTO updateNoteDTO) {
        Note existingNote = noteMapper.selectById(noteId);
        if (existingNote == null) {
            throw new RuntimeException("笔记不存在");
        }

        BeanUtils.copyProperties(updateNoteDTO, existingNote);
        existingNote.setUpdateTime(LocalDateTime.now());
        noteMapper.update(existingNote);
        return existingNote;
    }

    @Override
    public void deleteNote(String noteId) {
        noteMapper.deleteById(noteId);
    }
}