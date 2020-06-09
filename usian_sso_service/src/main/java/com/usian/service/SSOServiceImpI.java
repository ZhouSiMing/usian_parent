package com.usian.service;

import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import com.usian.utils.MD5Utils;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SSOServiceImpI implements SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;


    //注册信息校验
    @Override
    public Boolean checkUserInfo(String checkValue, Integer checkFlag) {
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        //1.设置查询条件
        if (checkFlag==1) {
            criteria.andUsernameEqualTo(checkValue);
        } else if (checkFlag == 2) {
            criteria.andPhoneEqualTo(checkValue);
        }
        //2.校验用户信息是否合格
        List<TbUser> tbUserList = tbUserMapper.selectByExample(tbUserExample);
        if (tbUserList==null || tbUserList.size()==0){
            //如果没有返回true
            return true;
        }
            //如果有返回false。
            return false;
    }
    //用户注册
    @Override
    public Integer userRegister(TbUser tbUser) {

        String pwd = MD5Utils.digest(tbUser.getPassword());
        tbUser.setPassword(pwd);
        Date date = new Date();
        tbUser.setCreated(date);
        tbUser.setUpdated(date);


        return tbUserMapper.insertSelective(tbUser);
    }

    //用户登录
    @Override
    public Map userLogin(String username, String password) {
        //1.把password加密
        String pwd = MD5Utils.digest(password);
        //2.判断用户名密码是否正确
        TbUserExample TbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = TbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(pwd);
        List<TbUser> tbUserList = this.tbUserMapper.selectByExample(TbUserExample);
        if(tbUserList == null || tbUserList.size() <= 0){
            return null;
        }
        //3.登陆成功后把user装到redis，并设置失效时间
        TbUser tbUser = tbUserList.get(0);
        String token = UUID.randomUUID().toString();
        redisClient.set(USER_INFO+":"+token,tbUser);
        redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
        //4.返回结果

        Map<String,String> map = new HashMap<String,String>();
        map.put("token",token);
        map.put("userid",tbUser.getId().toString());
        map.put("username",tbUser.getUsername());
        return map;

    }

    //查询用户登录是否过期
    @Override
    public TbUser getUserByToken(String token) {
        TbUser tbuser = (TbUser) redisClient.get(USER_INFO + ":" + token);
        if (tbuser!=null){
            redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
            return tbuser;
        }
        return null;
    }

    //退出登录
    @Override
    public Boolean logOut(String token) {
        return redisClient.del(USER_INFO+":"+token);
    }
}
