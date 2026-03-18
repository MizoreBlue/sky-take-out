package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {


    /**
     * 获取用户默认地址
     * @return AddressBook
     */
    @Select("select * from  address_book where user_id = #{currentId} and is_default = 1")
    AddressBook getDefaultAddressByUserId(Long currentId);


    /**
     * 根据当前登录用户的 id 获取所有地址信息
     * @param currentId long
     * @return list
     */
    @Select("select * from address_book where  user_id = #{currentId}")
    List<AddressBook> getAllAddressByUserId(Long currentId);


    /**
     * 添加一条地址信息
     * @param addressBook pojo
     */
    void addAddressBook(AddressBook addressBook);

    /**
     * 根据地址id 查询数据
     * @param id long
     * @return address book
     */
    @Select("select * from address_book where id = #{id};")
    AddressBook getAddressByAddressId(Long id);


    /**
     * 根据地址id删除地址信息
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteByAddressId(Long id);


    /**
     * 获取默认地址
     * @return addressbook
     */
    @Select("select * from address_book where is_default = 1")
    AddressBook getAddressByDefaultStatus();


    /**
     * 更新地址信息
     * @param addressBook pojo
     */
    void updateAddressBook(AddressBook addressBook);
}
