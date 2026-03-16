package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.ShoppingCartService;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setmealMapper;


    /**
     * 添加购物车
     *
     * @param shoppingCartDTO body
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {

//        判断该菜品是否已经存在购物车
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

//        若存在则将已经再购物车中则直接将数量+1
        if (list != null && !list.isEmpty()) {
//            要么查不到，要么就就一条数据
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
//            更新购物车
            shoppingCartMapper.updateNumberById(cart);

        } else {
//        若不存在则将插入一条购物车数据
//            判断本次添加到购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
//                添加的是菜品 查询菜品用于封装购物车数据
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else {
//                添加的是套餐 查询套餐用于封装购物车
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
//            无论是菜品还是套餐，都需要执行加1操作和更新时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
        }


    }


    /**
     * 查看购物车
     * @return List
     */
    public List<ShoppingCart> getList() {
//        根据用户 id 获取购物车数据

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.list(cart);
    }


    /**
     * 清空购物车
     */
    public void clean() {
        shoppingCartMapper.cleanByUserId(BaseContext.getCurrentId());
    }
}