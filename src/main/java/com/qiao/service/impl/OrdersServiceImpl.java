package com.qiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiao.common.BaseContext;
import com.qiao.common.CustomException;
import com.qiao.entity.*;
import com.qiao.mapper.OrdersMapper;
import com.qiao.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    @Override
    public void submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);

        if(shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("shopping cart empty, please add items");
        }

        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("please add address first");
        }

        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = new ArrayList<>();
        shoppingCartList.forEach(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetails.add(orderDetail);
        });

        orderDetailService.saveBatch(orderDetails);


        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setAddress((addressBook.getProvinceName() == null? "":addressBook.getProvinceName())
                +(addressBook.getCityName()== null? "":addressBook.getCityName())
                +(addressBook.getDistrictName()== null? "":addressBook.getDistrictName())
                +(addressBook.getDetail()== null? "":addressBook.getDetail()));


        this.save(orders);

        shoppingCartService.remove(lqw);

    }

    @Override
    public void again(Orders orders) {

        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OrderDetail::getOrderId,orders.getId());

        List<OrderDetail> orderDetailList = orderDetailService.list(lqw);

        List<ShoppingCart> shoppingCartList = new ArrayList<>();

        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            if(orderDetail.getDishId() != null){
                shoppingCart.setDishId(orderDetail.getDishId());
                shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            } else {
                shoppingCart.setSetmealId(orderDetail.getSetmealId());
            }
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartList.add(shoppingCart);
        });

        shoppingCartService.saveBatch(shoppingCartList);

    }
}
