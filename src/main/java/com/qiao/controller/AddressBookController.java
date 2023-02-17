package com.qiao.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiao.common.BaseContext;
import com.qiao.common.R;
import com.qiao.entity.AddressBook;
import com.qiao.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> getList(AddressBook addressBook){
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(null != userId,AddressBook::getUserId,userId);
        lqw.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(lqw);
        return R.success(list);

    }

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("save success");
    }

    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(null != addressBook){
            return R.success(addressBook);
        }

        return R.error("get addressbook failed");

    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("update success");
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        addressBookService.removeById(ids);
        return R.success("delete success");
    }

    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,1);
        AddressBook address = addressBookService.getOne(lqw);
        return R.success(address);
    }


}
