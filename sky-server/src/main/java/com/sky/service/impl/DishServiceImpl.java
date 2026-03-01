package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional
    public void addDishWithFlavors(DishDTO dishDTO) {
//        将数据传输对象DTO转换为与数据库交互的对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
//        向菜品表插入1条数据
        dishMapper.addDish(dish);
//        获取菜品传输对象中的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Long dishId = dish.getId();
        if (flavors != null && flavors.size() > 0) {
//            若有口味数据
//        向口味表中插入n条数据 批量插入
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO) {
//        从与前端交互的数据传输对象获取分页查询所需要的数据
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
//        DAO层
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return new PageResult(total,result);
    }

    /**
     * 单个或批量删除菜品
     * 起售中的菜品不能删除
     * 关联了套餐的菜品不能删除
     * 删除菜品后 关联的口味数据也要删除掉
     * 菜品批量删除
     * @param dishIds
     */
//    事务注解保证事物代码的一致性
    @Transactional
    public void deleteBatch(List<Long> dishIds) {
//        判断当前是否能够删除 是否存在起售中的菜品
        dishIds.forEach(dishId -> {
           Dish dish = dishMapper.getById(dishId);
           if (dish.getStatus().equals(StatusConstant.ENABLE)) {
//               菜品处于起售中，不能删除，抛出业务异常
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        });
//        判断当前是否能够删除 菜品是否关联了套餐
        List<Long> setmealIDs =  setmealDishMapper.getSetmealIdsByDishId(dishIds);
        if (setmealIDs != null && setmealIDs.size() > 0) {
//            说明菜品关联了套餐，不允许删除 抛出业务异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        删除菜品中的菜品数据
/*        for (Long id : dishIds) {
            dishMapper.deleteByIds(id);
//        删除菜品关联的菜品数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

//        根据菜品id集合批量删除菜品和菜品口味数据
        dishMapper.deleteByIds(dishIds);

//        批量删除口味数据
        dishFlavorMapper.deleteByDishIds(dishIds);

    }

}
