package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 起售、禁售套餐
     * @param status
     * @param id
     */
    @Update("update setmeal set status = #{status} where id =#{id}")
    void setStatus(Integer status,Long id);

    /**
     * 批量删除套餐
     * @param setMealIds
     */
    void deleteBatchByIds(List<Long> setMealIds);
}
