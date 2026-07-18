package com.teaching.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teaching.system.entity.*;
import com.teaching.system.mapper.*;
import com.teaching.system.service.AIService;
import com.teaching.system.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final ExerciseMapper exerciseMapper;
    private final ExamRecordMapper examRecordMapper;
    private final MaterialMapper materialMapper;
    private final AIService aiService;

    @PostMapping("/materials/upload")
    public R uploadMaterial(@RequestParam("file") MultipartFile file, Authentication auth) {
        if (file.isEmpty()) return R.error("文件为空");
        try {
            Long teacherId = (Long) auth.getPrincipal();
            byte[] pdfBytes = file.getBytes();
            String chunksStr = aiService.loadPdf(pdfBytes, file.getOriginalFilename());
            int chunks = Integer.parseInt(chunksStr);
            Material mat = new Material();
            mat.setTeacherId(teacherId);
            mat.setFilename(file.getOriginalFilename());
            mat.setOriginalName(file.getOriginalFilename());
            mat.setChunks(chunks);
            materialMapper.insert(mat);
            return R.ok(Map.of("chunks", chunks, "filename", file.getOriginalFilename(), "id", mat.getId()));
        } catch (Exception e) {
            return R.error("upload fail: " + e.getMessage());
        }
    }

    @GetMapping("/materials")
    public R listMaterials(Authentication auth) {
        Long teacherId = (Long) auth.getPrincipal();
        return R.ok(materialMapper.selectList(
                new LambdaQueryWrapper<Material>().eq(Material::getTeacherId, teacherId)
                        .orderByDesc(Material::getCreatedAt)));
    }

    @PostMapping("/exercises/generate")
    public R generateQuestion(@RequestBody(required = false) Map<String, Object> body) {
        String searchQuery = "key concepts";
        String customReq = null;
        if (body != null) {
            customReq = (String) body.getOrDefault("requirement", null);
        }
        String context = aiService.search(searchQuery, 8);
        Map<String, Object> result = aiService.generateQuestion(context, customReq);
        if (result.containsKey("error")) return R.error("AI gen failed");
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
