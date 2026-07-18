package com.teaching.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaching.system.entity.ExamRecord;
import com.teaching.system.entity.Exercise;
import com.teaching.system.mapper.ExamRecordMapper;
import com.teaching.system.mapper.ExerciseMapper;
import com.teaching.system.mapper.MessageMapper;
import com.teaching.system.mapper.UserMapper;
import com.teaching.system.service.AIService;
import com.teaching.system.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final ExerciseMapper exerciseMapper;
    private final ExamRecordMapper examRecordMapper;
    private final UserMapper userMapper;
    private final MessageMapper messageMapper;
    private final AIService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/exercises")
    public R listExercises() {
        return R.ok(exerciseMapper.selectList(
                new LambdaQueryWrapper<Exercise>().orderByDesc(Exercise::getCreatedAt)));
    }

    @PostMapping("/answers/{exerciseId}")
    public R submitAnswer(@PathVariable Long exerciseId,
                          @RequestBody Map<String, String> body) {
        Exercise exercise = exerciseMapper.selectById(exerciseId);
        if (exercise == null) return R.error("习题不存在");

        String studentAnswer = body.get("studentAnswer");
        String context = aiService.search(exercise.getQuestion(), 5);
        Map<String, Object> gradeResult = aiService.gradeAnswer(
                context, exercise.getStandardAnswerPoints(), studentAnswer);

        if (gradeResult.containsKey("error")) return R.error("AI批改失败");

        try {
            ExamRecord record = new ExamRecord();
            record.setExerciseId(exerciseId);
            record.setStudentId(Long.valueOf(body.get("studentId")));
            record.setQuestion(exercise.getQuestion());
            record.setStudentAnswer(studentAnswer);
            record.setStepScores(objectMapper.writeValueAsString(
                    gradeResult.get("step_scores")));
            record.setLogicScore(toDouble(gradeResult.get("logic_score")));
            record.setTotalScore(toDouble(gradeResult.get("total_score")));
            record.setFeedback((String) gradeResult.get("feedback"));
            record.setWeakTags(objectMapper.writeValueAsString(
                    gradeResult.get("weak_tags")));
            examRecordMapper.insert(record);
        } catch (Exception ignored) {}

        return R.ok(gradeResult);
    }

    @GetMapping("/scores")
    public R getMyScores(@RequestParam Long studentId) {
        return R.ok(examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getStudentId, studentId)
                        .orderByDesc(ExamRecord::getCreatedAt)));
    }

    @GetMapping("/errors")
    public R getMyErrors(@RequestParam Long studentId) {
        List<ExamRecord> records = examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getStudentId, studentId));
        Map<String, Integer> tagCounts = new HashMap<>();
        for (ExamRecord r : records) {
            if (r.getWeakTags() != null) {
                try {
                    String[] tags = objectMapper.readValue(r.getWeakTags(), String[].class);
                    for (String tag : tags) {
                        tagCounts.merge(tag, 1, Integer::sum);
                    }
                } catch (Exception ignored) {}
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        tagCounts.forEach((tag, count) -> result.add(Map.of("tag", tag, "count", count)));
        result.sort((a, b) -> ((Integer) b.get("count")).compareTo((Integer) a.get("count")));
        return R.ok(result);
    }

    @GetMapping("/chat/contacts")
    public R getTeacherContacts() {
        return R.ok(userMapper.findAllTeachers());
    }

    @GetMapping("/chat/messages/{teacherId}")
    public R getChatMessages(@PathVariable Long teacherId,
                             @RequestParam Long studentId) {
        return R.ok(messageMapper.findConversation(teacherId, studentId));
    }

    private Double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        if (val instanceof String s) return Double.valueOf(s);
        return 0.0;
    }
}
