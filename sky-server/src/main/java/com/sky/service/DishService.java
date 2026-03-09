package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * 菜品管理
 */
public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void addDishWithFlavors(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据菜品id查询菜品
     * @param id
     * @return
     */
    DishVO getByDishId(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateDish(DishDTO dishDTO);


    /**
     * 根据菜品分类查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> getByCategoryId(Long categoryId);
}
