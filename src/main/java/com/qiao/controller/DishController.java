package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiao.common.R;
import com.qiao.dto.DishDto;
import com.qiao.entity.Category;
import com.qiao.entity.Dish;
import com.qiao.entity.DishFlavor;
import com.qiao.service.CategoryService;
import com.qiao.service.DishFlavorService;
import com.qiao.service.DishService;
import com.qiao.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize,String name){
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,lqw);

        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> dishDtos = new ArrayList<>();
        records.forEach(dish ->
        {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(dish,dishDto);

            Category category = categoryService.getById(dish.getCategoryId());

            if(category != null){
                dishDto.setCategoryName(category.getName());
            }

            dishDtos.add(dishDto);
        });

        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品：{}",dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        redisTemplate.delete("dish_" + dishDto.getCategoryId() + "_1");

        return R.success("add success");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        redisTemplate.delete("dish_" + dishDto.getCategoryId() +"_1");

        return R.success("update success");
    }

    @GetMapping("/{id}")
    public R<DishDto> getByIdWithDish(@PathVariable Long id){
        log.info("get dishDto by id {}",id);

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if(dishDto != null){
            return R.success(dishDto);
        }
        return R.error("failed to get dishDto info");
    }

    @PostMapping("/status/{status}")
    public R<String> changeDishStatus(Long[] ids, @PathVariable Integer status){

        for (Long id : ids) {
                Dish dish = dishService.getById(id);
                dish.setStatus(status);
                dishService.updateById(dish);
        }

        return R.success("change status success");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids) {
        log.info("delete: {}", ids);

        for (Long id : ids) {
            dishService.deleteByIdWithFlavor(id);
        }

        return R.success("delete success");

    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishList(Dish dish){
        Long categoryId = dish.getCategoryId();
        List<DishDto> dishDtos = new ArrayList<>();

        String key = "dish_" +categoryId +"_" +dish.getStatus();

        List<DishDto> dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList != null){
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId,categoryId).eq(Dish::getStatus,1);
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes = dishService.list(lqw);

        dishes.forEach(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            dishDto.setCategoryName(categoryService.getById(categoryId).getName());

            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,item.getId()).eq(DishFlavor::getIsDeleted,0);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);

            dishDto.setFlavors(dishFlavors);

            dishDtos.add(dishDto);
        });

        redisTemplate.opsForValue().set(key,dishDtos,60, TimeUnit.MINUTES);

        return R.success(dishDtos);

    }


}
