package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO body
     * @return Vo
     */
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);


    /**
     * 订单支付成功
     * @param orderNumber string
     */
    void paySuccess(String orderNumber);


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;


    /**
     * 催单
     * @param id 订单 id
     */
    void reminder(Long id);


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 再来一单
     * @param id orderId
     */
    void repetition(Long id);


    /**
     * 取消订单
     * @param id orderId
     */
    void orderCancel(Long id);


    /**
     * 订单退款
     * @param id orderId
     */
    void refund(Long id);


    /**
     * 查看订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);
}
