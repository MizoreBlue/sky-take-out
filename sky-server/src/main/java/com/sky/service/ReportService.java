package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {


    /**
     * 返回营业额数据统计
     * @param begin query yyyy-MM-dd
     * @param end query yyyy-MM-dd
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);


    /**
     * 返回用户数据统计在指定时间内的
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);


    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 视图对象
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);


    /**
     * 查询销量排名top10
     * @param begin 开始时间
     * @param end 结束时间
     * @return 视图对象
     */
    SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end);
}
