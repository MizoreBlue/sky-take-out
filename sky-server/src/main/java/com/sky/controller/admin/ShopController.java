package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺操作相关接口")
public class ShopController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 设置店铺是否营业
     * path 参数
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为:{}", status== 1 ? "营业中" : "打样中");

//        将状态值存入Redis中
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 管理端获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getShopStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺营业状态为:{}", status == 1 ? "营业中" : "打样中");
        return Result.success(status);
    }
}
