package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
public class ShopCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCartDTO body
     * @return Result
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车:{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return  Result.success();
    }

    /**
     * 查看购物车
     * @return list
     */
    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> listShoppingCart() {
        log.info("查看购物车:{}", BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartService.getList();
        return Result.success(shoppingCarts);
    }


    /**
     * 清空购物车
     * @return code
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result deleteShoppingCart() {
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }


    /**
     * 删除购物车中的一个商品
     * @param shoppingCartDTO requestBody
     * @return result
     */
    @PostMapping("/sub")
    @ApiOperation("删除购物车中的一个商品")
    public Result subtractShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("删除购物车中的一个商品:{}", shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);
        return  Result.success();
    }
}
