package com.qiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiao.common.CustomException;
import com.qiao.dto.SetmealDto;
import com.qiao.entity.Setmeal;
import com.qiao.entity.SetmealDish;
import com.qiao.mapper.SetmealMapper;
import com.qiao.service.SetmealDishService;
import com.qiao.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveMeal(SetmealDto setmealDto) {
        this.save(setmealDto);

        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id.toString()));

        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id.toString());
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId().toString());
        setmealDishService.remove(lqw);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDto.getId().toString()));

        setmealDishService.saveBatch(setmealDishes);


    }

    @Override
    public void deleteWithDish(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        if(this.count(queryWrapper) > 0){
            throw new CustomException("the combo is on sale, take it off the shelf first");
        }

        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw);
    }


}
