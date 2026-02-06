package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    @Select("select COUNT(category_id) from setmeal where category_id = #{categoryId}")
    Integer countCategoryById(Long categoryId);
}
