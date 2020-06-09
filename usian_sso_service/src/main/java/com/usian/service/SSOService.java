package com.usian.service;

import com.usian.pojo.TbUser;
import com.usian.utils.Result;

import java.util.Map;

public interface SSOService {

    //注册信息校验
    Boolean checkUserInfo(String checkValue, Integer checkFlag);

    //用户注册
    Integer userRegister(TbUser tbUser);

    //用户登录
    Map userLogin(String username, String password);

    //查询用户登录是否过期
    TbUser getUserByToken(String token);

    //退出登录
    Boolean logOut(String token);
}
