package com.sky.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 返回营业额数据统计
     * @param begin query yyyy-MM-dd
     * @param end query yyyy-MM-dd
     * @return TurnoverReportVO
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
//        计算 dateList 存放begin到end的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

//        每天的营业额
        List<Double> turanoverList = new ArrayList<>();
//        计算边界值
        dateList.add(begin);
        while (begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


//        TODO 待优化 将多次查询改为单次查询
        for (LocalDate date : dateList) {
//        查询日期对应的营业额。状态已完成的订单金额合计，在限定时间范围内
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            select sum(amount) from order where order_time > beginTime and order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover =  orderMapper.sumByMap(map);
//            判断是否为空
            turnover = turnover == null ? 0.0 : turnover;
            turanoverList.add(turnover);
        }


        String date = StringUtils.join(dateList, ",");
        String turnover = StringUtils.join(turanoverList, ",");

        return TurnoverReportVO.builder()
                .dateList(date)
                .turnoverList(turnover)
                .build();
    }


    /**
     * 用户数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 视图对象
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
//        获取日期集合 包含起止时间
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }

//        查询每一天的 总的用户数量 和新增的用户数量
        List<Integer> newUserList = new ArrayList<>(); //每天新增的用户数量
        List<Integer> totalUserList = new ArrayList<>();//每天总的用户数量
        HashMap map = new HashMap();//用于配置参数，避免重复新建对象

//        每一天新增的用户数量
//        select * from user where create_time > ? and create_time < ?
//        统计当前总的用户数量  小于某一天的总的数据
//        select * from user where create_time < ?
        for (LocalDate date : dataList) {
//            计算当天时间的最大值和最小值
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

//            TODO 待优化 循环多次查询改为单次查询
//           先设置终止时间，查询到当天为止的用户总数
            map.put("endTime", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);

//            设置起始时间，查询当天的用户注册数量
            map.put("beginTime", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
            map.put("beginTime", null);//用于下次循环用户总数查询
        }

//        封装结果数据
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }
}
