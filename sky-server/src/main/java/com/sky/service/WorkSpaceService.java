package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkSpaceService {


    /**
     * 获取今日运营数据
     * @return 视图对象
     */
    BusinessDataVO getBusinessDate();


    /**
     * 查询套餐总览
     * @return 视图对象
     */
    SetmealOverViewVO getSetmealOverView();


    /**
     * 查询菜品总览
     * @return 视图对象
     */
    DishOverViewVO getDishOverView();


    /**
     * 查询订单总览
     * @return 视图对象
     */
    OrderOverViewVO getOrderOverView();
}
