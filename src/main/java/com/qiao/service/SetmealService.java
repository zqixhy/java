package com.qiao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiao.dto.SetmealDto;
import com.qiao.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveMeal(SetmealDto setmealDto);

    SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);

}
