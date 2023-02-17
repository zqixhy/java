package com.qiao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiao.entity.Orders;

public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
    void again(Orders orders);
}
