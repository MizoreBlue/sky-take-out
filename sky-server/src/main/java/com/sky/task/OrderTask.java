package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 定时任务类 定时处理订单状态
 */
@Component //自定义的定时任务类
@Slf4j
public class OrderTask {


    @Autowired
    private OrderMapper orderMapper;


    /**
     * 处理订单超时的方法
     */
    @Scheduled(cron = "0 * * * * ?")
    public void executeTask() {
        log.info("定时处理超时订单:{}",new Date());

//        当前时间减十五分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
//        定时执行查询语句
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
            }
            //        批量更新订单数据
            orderMapper.updates(ordersList);
        }
    }


    /**
     * 处理派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨一点触发一次
    public void processDelivery(){
        log.info("定时处理派送中的订单：{}",LocalDateTime.now());
//        处理上一个工作日的订单
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);//00:00时间
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.COMPLETED);
            }
            //        批量更新订单数据
            orderMapper.updates(ordersList);
        }
    }
}
