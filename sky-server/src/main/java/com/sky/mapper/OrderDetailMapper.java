package com.sky.mapper;


import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入Order 数据
     * @param orderDetails list
     */
    void insertBatch(List<OrderDetail> orderDetails);


    /**
     * 根据订单id集合批量查询数据
     * @param orderIds list
     * @return list
     */
    List<OrderDetail> getByOrderIds(List<Long> orderIds);
}
