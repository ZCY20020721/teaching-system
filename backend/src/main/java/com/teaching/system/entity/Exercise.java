package com.teaching.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exercises")
public class Exercise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private String question;
    private String standardAnswerPoints;
    private Double totalMaxScore;
    private LocalDateTime createdAt;
}
