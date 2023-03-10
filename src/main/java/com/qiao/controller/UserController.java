package com.qiao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiao.common.R;
import com.qiao.entity.User;
import com.qiao.service.UserService;
import com.qiao.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        String phone = user.getPhone();
        if(null != phone){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //request.getSession().setAttribute(phone,code);

            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("send success");
        }

        return R.error("send failed");
    }

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request,@RequestBody Map map){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        log.info("phone:{},code:{}",phone,code);

        //String c = (String)request.getSession().getAttribute(phone);
        Object c = redisTemplate.opsForValue().get(phone);
        if(c.equals(code)){
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            request.getSession().setAttribute("user",user.getId());

            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("login failed");

    }

}
