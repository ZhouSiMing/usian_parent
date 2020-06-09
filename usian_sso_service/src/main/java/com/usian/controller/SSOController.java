package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import com.usian.utils.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/sso")
public class SSOController {


    private SSOService ssoService;
    //注册信息校验
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Boolean checkUserInfo(@PathVariable String checkValue, @PathVariable Integer checkFlag){
        return ssoService.checkUserInfo(checkValue,checkFlag);
    }

    //用户注册
    @RequestMapping("/userRegister")
    public Integer userRegister(@RequestBody TbUser tbUser){
        return ssoService.userRegister(tbUser);
    }

    //用户登录
    @RequestMapping("/userLogin")
    public Map userLogin(String username,String password){
       return ssoService.userLogin(username,password);
    }

    //查询用户登录是否过期
    @RequestMapping("/getUserByToken/{token}")
    public TbUser getUserByToken(@PathVariable String token) {
        return ssoService.getUserByToken(token);
    }

    //退出登录
    @RequestMapping("/logOut")
    public Boolean logOut(String token) {
        return ssoService.logOut(token);
    }


}
