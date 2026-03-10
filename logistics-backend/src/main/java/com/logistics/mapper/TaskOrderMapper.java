package com.logistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.logistics.entity.TaskOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskOrderMapper extends BaseMapper<TaskOrder> {
}
