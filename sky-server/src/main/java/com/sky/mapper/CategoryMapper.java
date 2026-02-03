package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类
     * @param category
     */
    void update(Category category);

    /**
     * 新增分类
     * @param category
     */
    void insert(Category category);

    /**
     * 根据id删除分类
     * @param categoryId
     */
    @Delete("delete from category where id = #{categoryId}")
    void delete(Long categoryId);
}
