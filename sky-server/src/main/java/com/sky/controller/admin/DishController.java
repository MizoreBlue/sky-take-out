package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        dishService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改菜品
     * body 参数
     * @param dishDTO
     * @return
     */
    public Result updateDish(@RequestBody DishDTO dishDTO){
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id path 参数
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO>  getDishById(@PathVariable Long id){
        log.info("根据菜品Id查询菜品:{}",id);
        DishVO dishVO =  dishService.getByDishId(id);
        return Result.success(dishVO);
    }
}
