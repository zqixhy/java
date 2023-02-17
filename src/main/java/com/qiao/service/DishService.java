package com.qiao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiao.dto.DishDto;
import com.qiao.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void deleteByIdWithFlavor(Long id);

}
