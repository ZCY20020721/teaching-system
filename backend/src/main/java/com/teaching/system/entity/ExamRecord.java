package com.teaching.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exam_records")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long exerciseId;
    private String question;
    private String studentAnswer;
    private String stepScores;
    private Double logicScore;
    private Double totalScore;
    private String feedback;
    private String weakTags;
    private LocalDateTime createdAt;
}
