package com.sky.service;

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
}
