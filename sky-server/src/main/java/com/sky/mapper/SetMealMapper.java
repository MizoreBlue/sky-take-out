package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.result.Result;
import com.sky.vo.SetmealOverViewVO;
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

    /**
     * 插入套餐
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    @Select("select * from setmeal where category_id = #{categoryId}")
    List<Setmeal> getSetmealsById(Long categoryId);


    /**
     * 获取套餐总览
     * @return
     */
    @Select("select " +
            "sum(case when status = 1 then 1 else 0 end) as sold," +
            "sum(case when status = 0 then 1 else 0 end) as discontinued " +
            "from setmeal")
    SetmealOverViewVO getSetMealsOverView();
}
