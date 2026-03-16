package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO body
     */
    void add(ShoppingCartDTO shoppingCartDTO);
}
