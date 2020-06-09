package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImpI implements ItemParamService {

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;


    @Autowired
    private TbItemParamMapper tbItemParamMapper;


    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private RedisClient redisClient;



    @Override
    public TbItemParam  selectItemParamByItemCatId(Long itemCatId) {


        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> itemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if (itemParamList!=null && itemParamList.size()>0){
            return itemParamList.get(0);
        }
        return null;
    }

    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {

        PageHelper.startPage(page,rows);
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        tbItemParamExample.setOrderByClause("updated DESC");
        List<TbItemParam> itemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        for (TbItemParam t:itemParamList){
            System.out.println(t.getParamData()+"----------");
        }

        PageInfo<TbItemParam> pageInfo = new PageInfo<>(itemParamList);

        PageResult pageResult = new PageResult();
        pageResult.setTotalPage(Long.valueOf(pageInfo.getPages()));
        pageResult.setPageIndex(pageInfo.getPageNum());
        pageResult.setResult(pageInfo.getList());
        return pageResult;
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {

        //1.判断该类别的商品是否有规格模板
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> itemParamList = tbItemParamMapper.selectByExample(tbItemParamExample);
        if (itemParamList.size()>0){
            return 0;
        }

        //2.保存规格模板信息
        Date date = new Date();
        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setParamData(paramData);
        tbItemParam.setUpdated(date);
        tbItemParam.setCreated(date);
        return tbItemParamMapper.insertSelective(tbItemParam);
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        tbItemParamMapper.deleteByPrimaryKey(id);
        return 0;
    }

    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {

        //1.先查询redis,如果有直接返回
        TbItemParamItem TbItemParamItem = (com.usian.pojo.TbItemParamItem) redisClient.get(ITEM_INFO + ":" + itemId + ":" + PARAM);
        if (TbItemParamItem!=null){
            return TbItemParamItem;
        }
        //2.再查询mysql,并把查询结果缓存到redis
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<com.usian.pojo.TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        if (tbItemParamItems!=null && tbItemParamItems.size()>0){
            TbItemParamItem = tbItemParamItems.get(0);
            redisClient.set(ITEM_INFO + ":" + itemId + ":" + PARAM,TbItemParamItem);
            redisClient.expire(ITEM_INFO + ":" + itemId + ":" + PARAM,ITEM_INFO_EXPIRE);
            return TbItemParamItem;
        }
        /********************解决缓存穿透************************/
        //把空对象保存到缓存
        redisClient.set(ITEM_INFO + ":" + itemId + ":"+ PARAM,null);
        //设置缓存的有效期
        redisClient.expire(ITEM_INFO + ":" + itemId + ":"+ PARAM,30L);
        return null;
    }
}
