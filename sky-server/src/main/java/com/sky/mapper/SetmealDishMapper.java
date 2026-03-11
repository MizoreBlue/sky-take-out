package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据dishid查询套餐
     * @param dishIds
     * @return 查询到的套擦id集合
     */
    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    /**
     * 修改套餐，菜品关系表
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetMealId(Long id);

    /**
     * 一次性查询所有套餐菜品数据
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getById(Long id);

    /**
     * 根据套餐id批量删除行数据
     * @param setMealIds
     */
    void deleteBySetMealIds(List<Long> setMealIds);
}
