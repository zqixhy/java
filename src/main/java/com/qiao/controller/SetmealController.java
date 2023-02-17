package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiao.common.R;
import com.qiao.dto.SetmealDto;
import com.qiao.entity.Category;
import com.qiao.entity.Setmeal;
import com.qiao.service.CategoryService;
import com.qiao.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<Setmeal>();
        lqw.like(name != null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage,lqw);

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> setmealDtos = new ArrayList<>();

        records.forEach(setmeal ->
        {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);

            Category category = categoryService.getById(setmeal.getCategoryId());

            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }

            setmealDtos.add(setmealDto);
        });

        setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);

    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveMeal(setmealDto);
        return R.success("save success");

    }

    @GetMapping("/{id}")
    public R<SetmealDto> getByIdWithDish(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);

    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("update success");
    }

    @DeleteMapping
    public R<String> deleteWithDish(@RequestParam List<Long> ids){

        setmealService.deleteWithDish(ids);

        return R.success("delete success");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@RequestParam List<Long> ids,@PathVariable Integer status){
        ids.forEach(id -> {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        });

        return R.success("change status success");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> getList(Long categoryId){
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId,categoryId);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);

        return R.success(list);
    }

    @GetMapping("/dish/{id}")
    public R<SetmealDto> getSetMealList(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        return R.success(setmealDto);


    }

}
