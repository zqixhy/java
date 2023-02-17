package com.qiao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiao.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
