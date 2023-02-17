package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiao.common.R;
import com.qiao.entity.Employee;
import com.qiao.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        if(emp == null){
            return R.error("username doesn't exist");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("password is wrong");
        }
        if(emp.getStatus() == 0){
            return R.error("the account is abandoned");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("sign out success");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee){
        log.info("新增员工：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

/*        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/

        employeeService.save(employee);

        return R.success("add success");
    }

    @GetMapping("/page")
    public R<Page> selectPage(int page, int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        Page pageinfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        lqw.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageinfo,lqw);
        return R.success(pageinfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee){
        log.info(employee.toString());
/*        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/
        employeeService.updateById(employee);

        return R.success("update success");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("get employee info by id...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("no employee info");
    }









}
