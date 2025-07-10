// 文件路径: com/hwadee/IOTS_SCS/controller/NoteController.java
package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.entity.DTO.CreateNoteDTO;
import com.hwadee.IOTS_SCS.entity.DTO.UpdateNoteDTO;
import com.hwadee.IOTS_SCS.entity.POJO.Note;
import com.hwadee.IOTS_SCS.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    /**
     * 获取学习笔记列表
     */
    @GetMapping
    public ResponseEntity<List<Note>> getNotes(
            @RequestParam(required = false) String course_id,
            @RequestParam(required = false) String lesson_id) {
        return ResponseEntity.ok(noteService.getNotes(course_id, lesson_id));
    }

    /**
     * 创建学习笔记
     */
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody CreateNoteDTO createNoteDTO) {
        return ResponseEntity.ok(noteService.createNote(createNoteDTO));
    }

    /**
     * 获取笔记详情
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteDetail(@PathVariable String noteId) {
        return ResponseEntity.ok(noteService.getNoteById(noteId));
    }

    /**
     * 更新学习笔记
     */
    @PutMapping("/{noteId}")
    public ResponseEntity<Note> updateNote(
            @PathVariable String noteId,
            @RequestBody UpdateNoteDTO updateNoteDTO) {
        return ResponseEntity.ok(noteService.updateNote(noteId, updateNoteDTO));
    }

    /**
     * 删除学习笔记
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable String noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.ok().build();
    }
}