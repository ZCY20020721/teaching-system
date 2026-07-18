package com.teaching.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.system.entity.ExamRecord;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    @Select("SELECT tag_name as tag, error_count as count FROM knowledge_tags ORDER BY error_count DESC")
    List<Map<String, Object>> getErrorStatistics();
}
