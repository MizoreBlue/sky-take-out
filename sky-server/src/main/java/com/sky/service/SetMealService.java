package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
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
}
