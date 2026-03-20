package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

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
}
