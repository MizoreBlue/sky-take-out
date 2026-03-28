package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.service.WorkSpaceService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private DishMapper dishMapper;


    /**
     * 获取今日营运数据
     * @return 视图对象
     */
    public BusinessDataVO getBusinessDate() {
//        设置当天边界时间
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);

//         构建查询参数
        HashMap<String,Object> params = new HashMap<>();
        params.put("beginTime",beginTime);
        params.put("endTime",endTime);

//        获取各项数据
        Double turnover = Optional.ofNullable(orderMapper.getValidTurnover(beginTime, endTime)).orElse(0.0);
        Integer newUsers = Optional.ofNullable(userMapper.countByMap(params)).orElse(0);
        Integer totalOrders = Optional.ofNullable(orderMapper.sumOrdersByMap(params)).orElse(0);

        params.put("status", Orders.COMPLETED);
        Integer validOrders = Optional.ofNullable(orderMapper.sumOrdersByMap(params)).orElse(0);

//        计算客单价和完成率
        Double unitPrice = validOrders == 0 ? 0.0 : turnover / validOrders;
        Double orderCompleteRate = totalOrders == 0 ? 0.0 : (double) validOrders / totalOrders;

//       封装数据
        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .turnover(turnover)
                .validOrderCount(validOrders)
                .unitPrice(unitPrice)
                .orderCompletionRate(orderCompleteRate)
                .build();
    }


    /**
     * 查询套餐总览对象
     * @return 视图对象
     */
    public SetmealOverViewVO getSetmealOverView() {
     return setMealMapper.getSetMealsOverView();
    }


    /**
     * 查询菜品总览
     * @return 视图对象
     */
    public DishOverViewVO getDishOverView() {
        return dishMapper.getDishOverView();
    }


    /**
     * 查询订单总览
     * @return 视图对象
     */
    public OrderOverViewVO getOrderOverView() {
//        设置查询时间
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);

        return orderMapper.getOrderOverViewDate(beginTime,endTime);
    }
}
