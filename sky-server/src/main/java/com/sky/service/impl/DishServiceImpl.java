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
import com.sky.mapper.CategoryMapper;
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

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增菜品
     *
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
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult PageQuery(DishPageQueryDTO dishPageQueryDTO) {
//        从与前端交互的数据传输对象获取分页查询所需要的数据
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
//        DAO层
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> result = page.getResult();
        return new PageResult(total, result);
    }

    /**
     * 单个或批量删除菜品
     * 起售中的菜品不能删除
     * 关联了套餐的菜品不能删除
     * 删除菜品后 关联的口味数据也要删除掉
     * 菜品批量删除
     *
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
        List<Long> setmealIDs = setmealDishMapper.getSetmealIdsByDishId(dishIds);
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

    /**
     * 根据菜品Id查询菜品
     *
     * @param id
     * @return
     */
    public DishVO getByDishId(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
//        属性拷贝
        BeanUtils.copyProperties(dish, dishVO);
//        获取缺少的分类名称
        String categoryName = categoryMapper.getNameById(dish.getCategoryId());
        dishVO.setCategoryName(categoryName);

//        获取缺少的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getListByDishId(id);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * 关系到两个表的数据处理，需要添加事物注解
     *
     * @param dishDTO
     */
    @Transactional
    public void updateDish(DishDTO dishDTO) {
//        将对象转为实体类对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
//        service层
        dishMapper.updateDish(dish);

//        初次提交数据时dishId==null 二次提交时有数据
        for (DishFlavor flavor : dishDTO.getFlavors()) {
            flavor.setDishId(dish.getId());
        }

//        保存对应的口味数据
        if (dishDTO.getFlavors() != null && dishDTO.getFlavors().size() > 0) {
//            若菜品无最初的菜品口味数据，则改为插入菜品口味数据
//            先清空对应的口味数据，在进行口味数据添加
            dishFlavorMapper.deleteByDishId(dish.getId());
            dishFlavorMapper.insertBatch(dishDTO.getFlavors());
//            否则为更新菜品数据口味
        } else {
            dishFlavorMapper.deleteByDishId(dish.getId());
        }
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.getByCategoryId(categoryId);
    }

    /**
     * 根据id起售禁售菜品
     *
     * @param status path
     * @param dishId query
     */
    public void setDishStatus(Integer status, Long dishId) {
        Dish build = Dish.builder().status(status).id(dishId).build();
        dishMapper.updateDish(build);
    }

}
