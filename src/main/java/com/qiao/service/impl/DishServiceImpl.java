package com.qiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiao.dto.DishDto;
import com.qiao.entity.Dish;
import com.qiao.entity.DishFlavor;
import com.qiao.mapper.DishMapper;
import com.qiao.service.DishFlavorService;
import com.qiao.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishId));

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(lqw);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDto.getId()));

        dishFlavorService.saveBatch(flavors);


    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lqw);
        dishDto.setFlavors(flavors);

        return dishDto;
    }


    @Override
    public void deleteByIdWithFlavor(Long id) {
        this.removeById(id);

        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(lqw);

    }



}
