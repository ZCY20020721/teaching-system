package com.teaching.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("materials")
public class Material {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private String filename;
    private String originalName;
    private Integer chunks;
    private LocalDateTime createdAt;
}
