package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    /**
     * 批量更新订单数据
     * @param ordersList
     */
    void updates(List<Orders> ordersList);


    /**
     * 根据订单状态和当前时间-15分钟查询数据
     * @param status 订单状态
     * @param time 当前时间-15分钟
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);


    /**
     * 根据开始和结束日期查询营业额
     * @return Double
     */
    Double sumByMap(Map map);


    /**
     * 计算订单总数
     * @param map
     * @return
     */
    Integer sumOrdersByMap(HashMap map);



    /**
     * 统计指定时间内销量前十的商品
     * @param beginTime 开始时间
     * @param endTime 结束时间
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
