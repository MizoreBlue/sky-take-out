package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /**
     * 用户提交订单
     * @param orders entity
     */
    void insert(Orders orders);


    /**
     * 根据订单号查订单
     * @param outTradeNo
     * @return
     */
    @Select("select * from orders where number = #{ouTradeNo}")
    Orders getByNumber(String outTradeNo);


    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据订单id查询订单
     * @param id order id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getByOrderId(Long id);
}
