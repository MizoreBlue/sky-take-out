package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkSpaceService workSpaceService;


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

        Map map = new HashMap();

//        TODO 待优化 将多次查询改为单次查询
        for (LocalDate date : dateList) {
//        查询日期对应的营业额。状态已完成的订单金额合计，在限定时间范围内
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
//            select sum(amount) from order where order_time > beginTime and order_time < endTime and status = 5
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


    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 视图对象
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

//        获取日期集合 包含起止日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        while (begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dataList.add(begin);
        }


        Integer totalOrdesCount = 0;
        Integer validOrderCount = 0;
        HashMap map = new HashMap(); //用于存储数据库查询参数
        List CurrentDayTotallist = new ArrayList();
        List CurrentDayVaildlist = new ArrayList();

//        根据日期查询当天的数据
//        TODO 多次循环待优化为单次循环查询数据库
        for (LocalDate date : dataList) {
//            查询当天订单有效总数 select count(id) from orders where order_time > beginTime and order_time < endTime and status = 5;
//            当天时间的最大值和最小值
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

//            查询当前的订单总数 select count(id) from orders where order_time > beginTime and order_time < endTime;
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer totalOrders = orderMapper.sumOrdersByMap(map); //当天订单总数
            map.put("status", Orders.COMPLETED);
            Integer validOrders = orderMapper.sumOrdersByMap(map); //当天有效订单总数
            map.put("status", null);//为下一次订单数据查询置空

//           当日数据集合
//           若查询到的数据为空
            totalOrders = totalOrders == null ? 0 : totalOrders;
            validOrders = validOrders == null ? 0 : validOrders;
            CurrentDayTotallist.add(totalOrders);
            CurrentDayVaildlist.add(validOrders);

//          累加当天的订单数据
            totalOrdesCount += totalOrders;
            validOrderCount += validOrders;

        }
//            总的订单完成率
            Double completedRate = (double) (validOrderCount/totalOrdesCount);

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .totalOrderCount(totalOrdesCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(completedRate) //总订单完成率计算
                .validOrderCountList(StringUtils.join(CurrentDayVaildlist,",")) //当日有效订单集合
                .orderCountList(StringUtils.join(CurrentDayTotallist,",")) //当日订单总数集合
                .build();
    }


    /**
     * 销量top10
     * @param begin 开始时间
     * @param end 结束时间
     * @return 视图对象
     */
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {

//        设置起止时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

//        获得热销商品数据
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);


//        获取所有商品名称集合
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
//        处理得到的数据 转换为字符串
        String nameList = StringUtils.join(names, ",");
        String numberList = StringUtils.join(numbers, ",");


//        封装并返回数据
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }



    /**
     * 导出运营数据报表
     * 当天以前的三十天
     * @param response 给前端发送响应数据
     */
    public void exportExcel(HttpServletResponse response) {
//        设置起止时间限制
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

//        查询概况数据
        BusinessDataVO businessDate = workSpaceService.getBusinessDate(
                LocalDateTime.of(dateBegin,LocalTime.MIN),
                LocalDateTime.of(dateEnd,LocalTime.MAX));

//        将查询到的数据写入到excel文件当中
//        获取类路径 从而获得输入流对象像
//        导出报表只导出数据，不需要创建新的文件，读取带有相应格式的模板文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板文件.xlsx");

        try {
//             基于模板文件创建一个新的Excel 文件
            if (in == null) {
                throw new OrderBusinessException("模板文件未找到");
            }
            XSSFWorkbook excel = new XSSFWorkbook(in);;
            XSSFSheet sheet1 = excel.getSheet("sheet1");

//            填充数据--时间
            sheet1.getRow(2).getCell(0).setCellValue("时间: " + dateBegin + "至" + dateBegin);

//            设置概览数据
            XSSFRow row = sheet1.getRow(4);
            row.getCell(1).setCellValue(businessDate.getTurnover());
            row.getCell(3).setCellValue(businessDate.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessDate.getNewUsers());

            row = sheet1.getRow(5);
            row.getCell(1).setCellValue(businessDate.getValidOrderCount());
            row.getCell(3).setCellValue(businessDate.getUnitPrice());

//            设置三十天的明细数据
            for (int i = 0; i < 30; i++) {
//                TODO  待优化 ，将循环改为单次查询
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessData = workSpaceService.getBusinessDate(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
//                获得某一行
                 row = sheet1.createRow(i+8);
                 row.createCell(0).setCellValue(date.toString());
                 row.createCell(1).setCellValue(businessData.getTurnover());
                 row.createCell(2).setCellValue(businessData.getValidOrderCount());
                 row.createCell(3).setCellValue(businessData.getOrderCompletionRate());
                 row.createCell(4).setCellValue(businessData.getUnitPrice());
                 row.createCell(5).setCellValue(businessData.getNewUsers());
            }


//        导出报表 ，通过输入流，将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

//            关闭资源
            excel.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
