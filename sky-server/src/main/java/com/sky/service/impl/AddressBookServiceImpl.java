package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 获取用户默认地址
     *
     * @return AddressBook
     */
    public AddressBook getDefaultAddress() {
        return addressBookMapper.getDefaultAddressByUserId(BaseContext.getCurrentId());
    }


    /**
     * 获取用户所有地址信息
     *
     * @return list
     */
    public List<AddressBook> getAllAddress() {
        return addressBookMapper.getAllAddressByUserId(BaseContext.getCurrentId());
    }


    /**
     * 根据用户 id 添加一条地址信息
     *
     * @param addressBook entity
     */
    public void addAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
//        默认为非默认地址
        addressBook.setIsDefault(0);
        addressBookMapper.addAddressBook(addressBook);
    }


    /**
     * 根据地址id查询地址信息
     *
     * @param id long
     * @return
     */
    public AddressBook getAddressAddressId(Long id) {
        return addressBookMapper.getAddressByAddressId(id);
    }


    /**
     * 根据地址id修改地址信息
     *
     * @param addressBook pojo
     */
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAddressBook(addressBook);
    }


    /**
     * 根据地址id删除地址信息
     */
    public void deleteAddressBook(Long id) {
        addressBookMapper.deleteByAddressId(id);
    }


    /**
     * 设置用户默认地址
     *
     * @param id address
     */
    @Transactional
    public void setDefaultAddress(Long id) {
//       查询是否已有其他默认地址
        AddressBook addressBook = addressBookMapper.getAddressByDefaultStatus();
        if (addressBook != null) {
//            若是有先设置为非默认地址
            addressBook.setIsDefault(0);
            addressBookMapper.updateAddressBook(addressBook);
        }
//            如果没就直接设置当前地址为默认地址
        AddressBook build = AddressBook.builder()
                .id(id)
                .isDefault(1)
                .build();
        addressBookMapper.updateAddressBook(build);
    }
}
