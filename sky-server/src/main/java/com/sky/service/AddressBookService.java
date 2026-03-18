package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.User;
import io.swagger.models.auth.In;

import java.util.List;

public interface AddressBookService {


    /**
     * 获取默认地址
     * @return AddressBook
     */
    AddressBook getDefaultAddress();

    /**
     * 获取用户所有地址信息
     * @return list
     */
    List<AddressBook> getAllAddress();


    /**
     * 新增地址
     * @param addressBook entity
     */
    void addAddress(AddressBook addressBook);


    /**
     * 根据地址id查询地址信息
     * @param id long
     * @return AddressBook
     */
    AddressBook getAddressAddressId(Long id);


    /**
     * 根据地址id修改地址信息
     * @param addressBook pojo
     */
    void updateAddressBook(AddressBook addressBook);


    /**
     * 删除地址信息
     */
    void deleteAddressBook(Long id);


    /**
     * 根据地址id设置用户默认地址
     * @param id address
     */
    void setDefaultAddress(Long id);
}
