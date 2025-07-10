package com.hwadee.IOTS_SCS.mapper;

import com.hwadee.IOTS_SCS.entity.POJO.Note;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NoteMapper {
    List<Note> selectNotes(String courseId, String lessonId);
    Note selectById(String id);
    int insert(Note note);
    int update(Note note);
    int deleteById(String id);
}