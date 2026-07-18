package com.teaching.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.system.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT m.*, u.username as sender_name FROM messages m JOIN users u ON m.sender_id=u.id WHERE (m.sender_id=#{a} AND m.receiver_id=#{b}) OR (m.sender_id=#{b} AND m.receiver_id=#{a}) ORDER BY m.created_at ASC")
    List<Message> findConversation(@Param("a") Long a, @Param("b") Long b);

    @Select("SELECT m.*, u.username as sender_name FROM messages m JOIN users u ON m.sender_id=u.id WHERE (m.sender_id=#{a} AND m.receiver_id=#{b}) OR (m.sender_id=#{b} AND m.receiver_id=#{a}) ORDER BY m.created_at DESC LIMIT 1")
    Message findLastMessage(@Param("a") Long a, @Param("b") Long b);
}
