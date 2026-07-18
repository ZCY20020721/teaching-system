package com.teaching.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String filePath;
    private String fileName;
    private String fileType;
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String senderName;
}
