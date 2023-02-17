package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiao.common.R;
import com.qiao.entity.Category;
import com.qiao.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize){
        log.info("page={},pageSize={}",page,pageSize);
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("add new category:{}",category.toString());

/*        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        category.setCreateUser(empId);
        category.setUpdateUser(empId);*/
        categoryService.save(category);

        return R.success("add success");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info(category.toString());

/*        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/

        categoryService.updateById(category);
        return R.success("update success");
    }

    @GetMapping("{id}")
    public R<Category> getById(@PathVariable Long id){
        log.info("get category by id {}",id);

        Category category = categoryService.getById(id);
        if(category != null){
            return R.success(category);
        }
        return R.error("failed to get category info");
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("delete by id:{}",ids);

        categoryService.remove(ids);

        return R.success("delete success");
    }

    @GetMapping("/list")
    public R<List<Category>> getList(Category category){
        log.info("get category list");
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        lqw.eq(category.getType() != null,Category::getType,category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lqw);
        return R.success(list);


    }






}
