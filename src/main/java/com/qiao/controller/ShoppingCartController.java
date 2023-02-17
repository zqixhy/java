package com.qiao.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiao.common.BaseContext;
import com.qiao.common.R;
import com.qiao.entity.ShoppingCart;
import com.qiao.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);

    }

    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());

        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        String dishFlavor = shoppingCart.getDishFlavor();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,userId);

        if(null != dishId){
            lqw.eq(ShoppingCart::getDishId,dishId).eq(dishFlavor !=null,ShoppingCart::getDishFlavor,dishFlavor);
        } else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(lqw);

        if(null != one){
            one.setNumber(one.getNumber()+1);
            shoppingCartService.updateById(one);
        } else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lqw);
        return R.success("delete success");

    }

    @PostMapping("/sub")
    public R<String> update(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        if(dishId != null){
            lqw.eq(ShoppingCart::getDishId,dishId);
        } else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart item = shoppingCartService.getOne(lqw);
        Integer number = item.getNumber();
        if(number > 1){
            item.setNumber(item.getNumber()-1);
            shoppingCartService.updateById(item);
        }else {
            shoppingCartService.removeById(item);
        }

        return R.success("update success");

    }

}
