package com.qiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiao.common.CustomException;
import com.qiao.entity.Category;
import com.qiao.entity.Dish;
import com.qiao.entity.Setmeal;
import com.qiao.mapper.CategoryMapper;
import com.qiao.service.CategoryService;
import com.qiao.service.DishService;
import com.qiao.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        if(dishService.count(dishLambdaQueryWrapper) > 0){
            throw new CustomException("当下分类下关联了菜品，删除失败");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        if(setmealService.count(setmealLambdaQueryWrapper) > 0){
            throw new CustomException("当下分类下关联了套装，删除失败");
        }

        super.removeById(id);

    }
}
