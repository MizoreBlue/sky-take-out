package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
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
}
