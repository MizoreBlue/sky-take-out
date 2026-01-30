package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
//        select * from category limit 0,10 从0开始查询10条数据
//        面向aop的编程，将查询语句拦截再编辑？
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

//        DAO层
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
//        将数据传输对象转换为实体对象 使用工具类拷贝已有属性值
        BeanUtils.copyProperties(categoryDTO,category);
//        设置其他属性 ，修改时间
        category.setUpdateTime(LocalDateTime.now());
//        修改用户id
        category.setUpdateUser(BaseContext.getCurrentId());
//        调用mapper层
        categoryMapper.update(category);
    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    public void startOrStopCategory(Integer status, long id) {
//        update category set status = ? where id = id
        Category category = Category.builder()
                .status(status)
                //设置被修改的分类 id
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }
}
