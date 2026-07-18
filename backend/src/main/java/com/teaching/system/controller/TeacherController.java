package com.teaching.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
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

    @PostMapping("/materials/upload")
    public R uploadMaterial(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return R.error("文件为空");
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/pdfs";
            new File(uploadDir).mkdirs();
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            String result = aiService.loadPdf(filePath);
            return R.ok(Map.of("chunks", result, "filename", file.getOriginalFilename()));
        } catch (Exception e) {
            return R.error("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/exercises/generate")
    public R generateQuestion() {
        String context = aiService.search("key concepts", 5);
        Map<String, Object> result = aiService.generateQuestion(context);
        if (result.containsKey("error")) return R.error("AI出题失败");
        return R.ok(result);
    }

    @PostMapping("/exercises")
    public R publishExercise(@RequestBody Map<String, Object> body, Authentication auth) {
        Long teacherId = (Long) auth.getPrincipal();
        Exercise exercise = new Exercise();
        exercise.setTeacherId(teacherId);
        exercise.setQuestion((String) body.get("question"));
        exercise.setTotalMaxScore(parseDouble(body.get("total_max_score"), 15.0));
        try {
            exercise.setStandardAnswerPoints(new ObjectMapper().writeValueAsString(body.get("standard_answer_points")));
        } catch (Exception e) { exercise.setStandardAnswerPoints("[]"); }
        exerciseMapper.insert(exercise);
        return R.ok(exercise);
    }

    @GetMapping("/exercises")
    public R listExercises() {
        return R.ok(exerciseMapper.selectList(
                new LambdaQueryWrapper<Exercise>().orderByDesc(Exercise::getCreatedAt)));
    }

    @DeleteMapping("/exercises/{id}")
    public R deleteExercise(@PathVariable Long id) { exerciseMapper.deleteById(id); return R.ok(); }

    @GetMapping("/scores/students")
    public R getAllRecords() {
        return R.ok(examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().orderByDesc(ExamRecord::getCreatedAt)));
    }

    @GetMapping("/scores/statistics")
    public R getErrorStatistics() { return R.ok(examRecordMapper.getErrorStatistics()); }

    private Double parseDouble(Object v, Double def) {
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) try { return Double.valueOf(s); } catch (Exception ignored) {}
        return def;
    }
}
