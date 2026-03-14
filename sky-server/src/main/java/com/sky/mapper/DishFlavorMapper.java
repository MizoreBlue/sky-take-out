package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface DishFlavorMapper {

    /**
     * 添加菜品口味
     * @param flavor
     */
    void addFlavor(DishFlavor flavor);

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据DishId删除口味表
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品主键集合删除口味数据
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询口味数据
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getListByDishId(Long id);

    /**
     * 根据菜品id更新口味数据
     * @param flavors
     */
    void updateByDishId(List<DishFlavor> flavors);

    /**
     * 根据菜品id批量查询菜品口味
     * @param dishIds
     * @return
     */
    List<DishFlavor> getListByDishIds(List<Long> dishIds);
}
