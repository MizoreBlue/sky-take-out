package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 根据接口文档可知，前端传递过来的三个参数均为Query类型
     * 则可用数据传输对象DTO接收
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页分类查询参数:{}", categoryPageQueryDTO);
//        调用service层接口
       PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
//        返回分页查询结果
        return Result.success(pageResult);
    }

    /**
     * 修改分类
     * 参数类型为Json，需要将java对象序列化
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
//        打印日志信息
        log.info("修改分类:{}", categoryDTO);
//        调用service层
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用禁用分类
     * @param status  路径参数url 需要添加注解
     * @param id  Query参数，无需添加任何注解，MVC框架自动映射
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("启用禁用分类")
    public Result startOrStopCategory(@PathVariable Integer status,long id) {
        log.info("启用禁用分类:{},{}", status,id);
        categoryService.startOrStopCategory(status,id);
        return Result.success();
    }

    /**
     * 新增分类
     * 参数类型为Json
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类:{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return  Result.success();
    }

    /**
     * 根据id删除分类
     * query参数
     * 采用数据传输对象接受
     * @param categoryDTO
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result deleteCategoryById(CategoryDTO categoryDTO) {
        log.info("根据id删除分类:{}", categoryDTO);
//        TODO 关联菜品时无法删除 外键捕获异常
        categoryService.deleteById(categoryDTO);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * Query查询参数
     * 反回一个list集合
     * @param type
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> getCategoryByType(Integer type) {
        log.info("根据类型查询分类:{}", type);
        List<Category> list = categoryService.getCategoryByType(type);
        return Result.success(list);
    }
}
