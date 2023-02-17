package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiao.common.BaseContext;
import com.qiao.common.R;
import com.qiao.dto.OrdersDto;
import com.qiao.entity.AddressBook;
import com.qiao.entity.OrderDetail;
import com.qiao.entity.Orders;
import com.qiao.entity.ShoppingCart;
import com.qiao.service.AddressBookService;
import com.qiao.service.OrderDetailService;
import com.qiao.service.OrdersService;
import com.qiao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;


    @GetMapping("/userPage")
    public R<Page> getUserPage(int page, int pageSize){
        Page<Orders> ordersPage = new Page(page,pageSize);

        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId, BaseContext.getCurrentId());
        lqw.orderByDesc(Orders::getCheckoutTime);
        orderService.page(ordersPage,lqw);

        return R.success(ordersPage);

    }

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String number, String beginTime,String endTime) throws ParseException {

        Page<Orders> ordersPage = new Page(page,pageSize);
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (number != null){
            lqw.eq(Orders::getNumber,number);
        }
        if(beginTime != null){
            LocalDateTime begin = LocalDateTime.parse(beginTime, formatter);
            lqw.ge(Orders::getCheckoutTime,begin);
        }
        if(endTime != null){
            LocalDateTime end = LocalDateTime.parse(endTime, formatter);
            lqw.le(Orders::getCheckoutTime,end);
        }

        lqw.orderByDesc(Orders::getCheckoutTime);
        orderService.page(ordersPage,lqw);

        return R.success(ordersPage);
    }


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("submit success");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        orderService.again(orders);

        return R.success("success");
    }

    @PutMapping
    public R<String> editStatus(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> luw = new LambdaUpdateWrapper<>();
        luw.eq(Orders::getId,orders.getId());
        luw.set(Orders::getStatus,orders.getStatus());
        orderService.update(luw);
        return R.success("edit status success");
    }

}
