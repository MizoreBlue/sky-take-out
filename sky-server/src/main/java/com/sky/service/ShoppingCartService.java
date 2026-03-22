package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO body
     */
    void add(ShoppingCartDTO shoppingCartDTO);


    /**
     * 查看购物车
     * @return List
     */
    List<ShoppingCart> getList();

    /**
     * 清空购物车
     */
    void clean();


    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO body
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
