package com.sky.controller.user;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetMealController")
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/user/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setmealService;


    /**
     * body参数
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类套餐")
    public Result modifySetMeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改分类套餐:{}",setmealDTO);
        setmealService.modifySetMeal(setmealDTO);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getBySetMealId(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        SetmealVO setmealVO =  setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * query 路径查询参数
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult) ;
    }

    /**
     * path参数 status
     * query参数 id
     * 停售起售套餐
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result setSetMealStatus(@PathVariable Integer status ,Long id){
        log.info("停售起售套餐:type={},id={}",status,id);
        setmealService.setStatus(status,id);
        return Result.success();
    }


    /**
     * 批量删除套餐
     * query参数
     * @param ids
     * 使用注解将参数解析为集合
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteSetMealByIds(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}",ids);
        setmealService.deleteBatchByIds(ids);
        return Result.success();
    }

    /**
     * 新曾套餐
     * 参数类型为Body
     * @param setmealDTO
     * @return
     */
    public Result insertSetMeal(@RequestBody SetmealDTO setmealDTO){
        return null;
    }


    /**
     * 新增套餐
     * Body参数
     * @param setmealVO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insertSetMeal(@RequestBody SetmealVO setmealVO){
        log.info("新增套餐:{}",setmealVO);
        setmealService.insert(setmealVO);
        return Result.success();
    }

    /**
     * 根据分类获取套餐集合
     * @param categoryId query
     * @return list
     */
    @GetMapping("/list")
    @ApiOperation("根据分分类id获取套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")//key:setmealCache::categoryId
    public Result<List<Setmeal>> getSetMealsByCategoryId(Long categoryId){
        log.info("getSetMealsByCategoryId:{}",categoryId);
        List<Setmeal> setmeals = setmealService.getByCategoryId(categoryId);
        return Result.success(setmeals);
    }
}
