package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
@Slf4j
@Api(tags = "工作台接口")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;


    /**
     * 获取今日运营数据
     * @return 视图对象
     */
    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> getBusinessDate() {
        log.info("获取今日运营数据");
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO = workSpaceService.getBusinessDate(beginTime, endTime);
        return Result.success(businessDataVO);
    }


    /**
     * 查询套餐总览
     * @return 视图对象
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> getSetmealOverView() {
        log.info("查询套餐总览数据");
        SetmealOverViewVO setmealOverView = workSpaceService.getSetmealOverView();
        return Result.success(setmealOverView);
    }


    /**
     * 查询菜品总览
     * @return 视图对象
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> getDishOverView() {
        log.info("查询菜品总览数据");
        DishOverViewVO dishOverViewVO = workSpaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }


    /**
     * 查询订单总览数据
     * @return 视图对象
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单总览")
    public Result<OrderOverViewVO> getOrderOverView() {
        log.info("查询订单总览数据");
        OrderOverViewVO orderOverViewVO =  workSpaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }
}
