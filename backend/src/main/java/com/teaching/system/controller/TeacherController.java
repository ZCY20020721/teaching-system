package com.teaching.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.system.entity.ExamRecord;
import com.teaching.system.entity.Exercise;
import com.teaching.system.entity.User;
import com.teaching.system.mapper.ExamRecordMapper;
import com.teaching.system.mapper.ExerciseMapper;
import com.teaching.system.mapper.MessageMapper;
import com.teaching.system.mapper.UserMapper;
import com.teaching.system.service.AIService;
import com.teaching.system.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final ExerciseMapper exerciseMapper;
    private final ExamRecordMapper examRecordMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final AIService aiService;

    @PostMapping("/exercises/generate")
    public R generateQuestion() {
        String context = aiService.search("提取核心概念和关键知识点", 5);
        Map<String, Object> result = aiService.generateQuestion(context);
        if (result.containsKey("error")) return R.error("AI出题失败");
        return R.ok(result);
    }

    @PostMapping("/exercises")
    public R publishExercise(@RequestParam Long teacherId, @RequestBody Exercise exercise) {
        exercise.setTeacherId(teacherId);
        exerciseMapper.insert(exercise);
        return R.ok(exercise);
    }

    @GetMapping("/exercises")
    public R listExercises() {
        return R.ok(exerciseMapper.selectList(
                new LambdaQueryWrapper<Exercise>().orderByDesc(Exercise::getCreatedAt)));
    }

    @DeleteMapping("/exercises/{id}")
    public R deleteExercise(@PathVariable Long id) {
        exerciseMapper.deleteById(id);
        return R.ok();
    }

    @GetMapping("/scores/students")
    public R getAllRecords() {
        return R.ok(examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().orderByDesc(ExamRecord::getCreatedAt)));
    }

    @GetMapping("/scores/statistics")
    public R getErrorStatistics() {
        return R.ok(examRecordMapper.getErrorStatistics());
    }

    @GetMapping("/chat/contacts")
    public R getStudentContacts() {
        return R.ok(userMapper.findAllStudents());
    }

    @GetMapping("/chat/messages/{studentId}")
    public R getChatMessages(@PathVariable Long studentId,
                             @RequestParam Long teacherId) {
        return R.ok(messageMapper.findConversation(teacherId, studentId));
    }
}
