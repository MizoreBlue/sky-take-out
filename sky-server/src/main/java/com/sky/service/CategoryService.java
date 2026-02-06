package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

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

    /**
     * 新增分类
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param categoryDTO
     */
    void deleteById(CategoryDTO categoryDTO);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> getCategoryByType(Integer type);
}
