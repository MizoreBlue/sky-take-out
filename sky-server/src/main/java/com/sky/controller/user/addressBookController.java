package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
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
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址簿接口")
public class addressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 返回用户默认地址
     * @return AddressBook
     */
    @GetMapping("/default")
    @ApiOperation("获取默认地址")
    public Result<AddressBook> getDefaultAddress() {
        log.info("获取默认地址");
        AddressBook addressBook = addressBookService.getDefaultAddress();
        return Result.success(addressBook);
    }


    /**
     * 获取当前用户的所有地址信息
     * @return list
     */
    @GetMapping("/list")
    @ApiOperation("获取用户所有地址信息")
    public Result<List<AddressBook>> getAddressBookByUserId() {
        log.info("获取用户所有地址信息");
        List<AddressBook> addressBooks = addressBookService.getAllAddress();
        return Result.success(addressBooks);
    }


    /**
     * 新增一套地址信息数据
     * @param addressBook pojo
     * @return msg
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result addAddress(@RequestBody AddressBook addressBook) {
        log.info("新增地址数据:{}", addressBook);
        addressBookService.addAddress(addressBook);
        return Result.success();
    }


    /**
     * 根据地址id查询地址信息
     * @param id path
     * @return address book
     */
    @GetMapping("/{id}")
    @ApiOperation("根据地址id查询地址")
    public Result<AddressBook> getAddressBookById(@PathVariable Long id) {
        log.info("根据地址id查询地址信息：{}",id);
        AddressBook addressBook =  addressBookService.getAddressAddressId(id);
        return Result.success(addressBook);
    }


    /**
     * 根据地址id修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据地址id修改地址")
    public Result updateAddress(@RequestBody AddressBook addressBook) {
        log.info("根据地址id修改地址：{}",addressBook);
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }


    /**
     * 根据地址id删除地址信息
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据地址id删除地址")
    public Result deleteAddress(Long id) {
        addressBookService.deleteAddressBook(id);
        return Result.success();
    }


    /**
     * 设置用户默认地址
     * @param addressBook 地址id
     * @return result
     */
    @PutMapping("/default")
    @ApiOperation("设置用户默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置用户默认地址:{}",addressBook);
        addressBookService.setDefaultAddress(addressBook.getId());
        return Result.success();
    }
}
