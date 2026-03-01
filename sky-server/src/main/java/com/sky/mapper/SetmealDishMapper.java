package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据dishid查询套餐
     * @param dishIds
     * @return 查询到的套擦id集合
     */
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);
}
