package com.sky.controller.user;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "C端-订单接口")
@RestController("userOrderController")
@RequestMapping("/user/order")
public class OrderController {


    @Autowired
    private OrderService orderService;


    /**
     * 用户下单
     * @param ordersSubmitDTO body
     * @return Vo
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> orderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单:{}", ordersSubmitDTO);
        OrderSubmitVO  orderSubmitVO = orderService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO dto
     * @return msg
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
//        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
//        log.info("生成预支付交易单：{}", orderPaymentVO);
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        return Result.success();
    }


    /**
     * 催单
     * @param id 订单id
     * @return result
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id){
        log.info("催单:{}",id);
        orderService.reminder(id);
        return Result.success();
    }


    /**
     * 历史订单查询
     * @param ordersPageQueryDTO query
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("获取历史订单")
    public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("获取历史订单:{}",ordersPageQueryDTO);
         PageResult result = orderService.pageQuery(ordersPageQueryDTO);
         return Result.success(result);
    }


    /**
     * 再来一单
     * @param id path
     * @return result
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单:{}",id);
        orderService.repetition(id);
        return Result.success();
    }


    /**
     * 取消订单
     * @param id orderId
     * @return result
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result OrderCancel(@PathVariable Long id) throws Exception {
        log.info("取消订单:{}",id);
        orderService.orderCancel(id);
        return Result.success();
    }


    /**
     * 查看订单详情
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查看订单详情:{}",id);
       OrderVO orderVO =  orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }
}
