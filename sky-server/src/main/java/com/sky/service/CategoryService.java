package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    /**
     * 修改分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);


    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    void startOrStopCategory(Integer status, long id);
}
