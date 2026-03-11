package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 插入套餐
     * @param setmealVO
     */
    public void insert(SetmealVO setmealVO) {

        Setmeal setmeal = new Setmeal();

//        将套餐数据插入setmeal表
        BeanUtils.copyProperties(setmealVO, setmeal);
        setMealMapper.insert(setmeal);

//        将和套餐关联的数据插入setmeal_dish表
//        将插入生成的id回显
        Long setmealId = setmeal.getId();
        List<SetmealDish> dishes = setmealVO.getSetmealDishes();
//        设置setmeal_dish关联的套餐id
        Iterator<SetmealDish> iterator = dishes.iterator();
        while (iterator.hasNext()) {
            SetmealDish dish = iterator.next();
            dish.setSetmealId(setmealId);
        }
        setmealDishMapper.insertBatch(dishes);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    public void modifySetMeal(SetmealDTO setmealDTO) {
//        修改套餐本身的数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setMealMapper.modifySetMeal(setmeal);

//        将菜品信息存储到菜品套餐关系表
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        dishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
        });

//        批量修改setmeal_dish表中的数据
//        采用先删除，后插入的思想 删除原有数据 再插入新的数据实现数据更新功能
        setmealDishMapper.deleteBySetMealId(setmealDTO.getId());
        setmealDishMapper.insertBatch(dishes);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
//        分别查询套餐数据和关联的菜品数据
        Setmeal setmeal = setMealMapper.getById(id);
//        批量拆套餐相关的菜品数据
        List<SetmealDish> dishes = setmealDishMapper.getById(id);

//        封装成Vo数据 返回至前端
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(dishes);
        return setmealVO;
    }

    /**
     * 停售 起售套餐
     *
     * @param status
     */
    public void setStatus(Integer status, Long id) {
        setMealMapper.setStatus(status, id);
    }

    /**
     * 批量删除套餐
     * @param setMealIds
     */
    @Transactional
    public void deleteBatchByIds(List<Long> setMealIds) {
//        删除套餐表数据
        setMealMapper.deleteBatchByIds(setMealIds);
//        删除setmeal_dish表数据
        setmealDishMapper.deleteBySetMealIds(setMealIds);
    }

    /**
     * 菜品分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // select * form setmeal limit 0,10 从0开始查询10条数据
//        开始分页查询
//        aop面向切面的编程，将sql语句拦截再加强
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total, records);
    }
}
