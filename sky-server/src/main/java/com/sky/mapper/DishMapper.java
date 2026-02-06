package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    @Select("select COUNT(category_id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
