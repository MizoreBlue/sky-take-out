package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@Api(tags = "菜品管理相关接口")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新曾菜品
     * 前端参数格式为json
     * 参数类型为Body
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.addDishWithFlavors(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * Query 查询参数
     * @return
     * @param dishPageQueryDTO
     */
    @GetMapping("/page")
    @ApiOperation( "菜品分页查询")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询参数:{}",dishPageQueryDTO);
//        调用service层
        PageResult pageResult = dishService.PageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * Query 查询参数
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("单个或批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("菜品批量删除");

//        清除缓存
        clearCache("dish_");

        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改菜品
     * body 参数
     * json对象反序列化为Java
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);

//        清除缓存
        clearCache("dish_"+dishDTO.getCategoryId());

        dishService.updateDish(dishDTO);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id path 参数
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据菜品Id查询菜品")
    public Result<DishVO>  getDishById(@PathVariable Long id){
        log.info("根据菜品Id查询菜品:{}",id);
        DishVO dishVO =  dishService.getByDishId(id);
        return Result.success(dishVO);
    }


    /**
     * query参数
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishListByCategoryId(Long categoryId){
        log.info("Admin:根据分类id查询菜品:{}",categoryId);
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     *
     * @param status path
     * @param id query
     * @return
     */
    @PostMapping("/status/{status}")
    public Result setDishStatus(@PathVariable Integer status,long id){
        log.info("起售禁售菜品:{},{}",status,id);

//      清除缓存
        clearCache("dish_*");

        dishService.setDishStatus(status,id);
        return Result.success();
    }


    /**
     * 清理redis缓存
     * @param pattern
     */
    private void clearCache(String pattern){
//       根据正则获取keys
        Set  keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
