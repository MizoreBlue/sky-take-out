package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.DishMapper;
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

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
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
//        category.setUpdateTime(LocalDateTime.now());
//        修改用户id
//        category.setUpdateUser(BaseContext.getCurrentId());
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
                //.updateTime(LocalDateTime.now())
                //.updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    public void addCategory(CategoryDTO categoryDTO) {
//        insert into category name() value()
//        复制拷贝数据，将数据传输对象转换为对于的实体类对象
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

//        添加其他属性 状态为默认禁用
        category.setStatus(0);
//        创建时间和创建用户信息
//        category.setCreateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

//        调用mapper层
        categoryMapper.insert(category);
    }

    /**
     * 根据id删除分类
     * @param categoryDTO
     */
    public void deleteById(CategoryDTO categoryDTO) {
        //        当前分类有菜品关联时抛出异常
        Long categoryId = categoryDTO.getId();
        // 查询当前分类关联的菜品数量
        Integer count = dishMapper.countByCategoryId(categoryId);

        if(count>0){
//            若当前分类有菜品关联则跑出异常，不予删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

//        查询当前分类是否关联了套餐
        count = setmealMapper.countCategoryById(categoryId);
        if(count>0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.delete(categoryId);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    public List<Category> getCategoryByType(Integer type) {
        return categoryMapper.select(type);
    }


}
