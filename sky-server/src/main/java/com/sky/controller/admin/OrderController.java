package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 取消订单
     * @param ordersCancelDTO header body
     * @return msg
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("管理端取消订单：{}", ordersCancelDTO);
        orderService.adminOrderCancel(ordersCancelDTO);
        return Result.success();
    }


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单分页查询：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.getOrderPage(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 各个状态的订单数量统计
     * @return statistics
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getOrderStatistics(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getOrderStatistics();
        return Result.success(orderStatisticsVO);
    }


    /**
     * 完成订单2
     * @param id 订单id
     * @return msg
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result orderComplete(@PathVariable Long id){
        log.info("完成订单:{}",id);
        orderService.orderComplete(id);
        return Result.success();
    }


    /**
     * 拒单
     * @param ordersRejectionDTO 数据传输对象
     * @return msg
     */
    @PutMapping("rejection")
    @ApiOperation("拒单")
    public Result orderReject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单:{}",ordersRejectionDTO);
        orderService.orderReject(ordersRejectionDTO);
        return Result.success();
    }


    /**
     * 接单
     * @param ordersConfirmDTO orderId
     * @return msg
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单：{}",ordersConfirmDTO);
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }


    /**
     * 查看订单详情
     * @param id orderId
     * @return OrderVo
     */
    @GetMapping("details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("查看订单详情:{}",id);
        OrderVO orderDetail = orderService.getOrderDetail(id);
        return Result.success(orderDetail);
    }


    /**
     * 订单派送
     * @param id OrderId
     * @return msg
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("订单派送")
    public Result orderDelivery(@PathVariable Long id){
        log.info("订单派送:{}",id);
        orderService.orderDelivery(id);
        return Result.success();
    }
}
