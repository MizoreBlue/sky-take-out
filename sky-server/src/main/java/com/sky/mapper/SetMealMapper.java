package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealMapper {

    @Select("select COUNT(category_id) from setmeal where category_id = #{categoryId}")
    Integer countCategoryById(Long categoryId);

    /**
     * 菜品分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 修改套餐
     * @param setmeal
     */
    void modifySetMeal(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);
}
