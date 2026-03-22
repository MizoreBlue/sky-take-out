package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 修改分类套餐
     * @param setmealDTO
     */
    void modifySetMeal(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 停售 起售套餐
     * @param status
     */
    void setStatus(Integer status, Long id);

    /**
     * 批量删除套擦
     * @param ids
     */
    void deleteBatchByIds(List<Long> ids);

    /**
     * 插入套餐
     * @param setmealVO
     */
    void insert(SetmealVO setmealVO);

    /**
     * 根据分类id获取套餐集合
     * @param categoryId
     * @return
     */
  List<Setmeal> getByCategoryId(Long categoryId);


    /**
     * 根据套餐id获得菜品数据
     * @param id setmeal id
     * @return list
     */
    List<DishItemVO> getDishesBySetmealId(Long id);
}
