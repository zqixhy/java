package com.qiao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiao.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
