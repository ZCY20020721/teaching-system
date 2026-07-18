package com.teaching.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.system.entity.User;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE role='student' ORDER BY username")
    List<User> findAllStudents();
    @Select("SELECT * FROM users WHERE role='teacher' ORDER BY username")
    List<User> findAllTeachers();
    @Select("SELECT * FROM users WHERE role='teacher' LIMIT 1")
    User findFirstTeacher();
}
