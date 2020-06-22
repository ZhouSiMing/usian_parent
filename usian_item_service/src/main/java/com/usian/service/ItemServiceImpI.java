package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImpI implements ItemService{

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${BASE}")
    private String BASE;

    @Value("${DESC}")
    private String DESC;

    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;


    @Override
    public TbItem selectItemInfo(Long itemId) {
        //1.先查询redis，如果有直接返回
        //查询缓存
        TbItem tbItem = (TbItem) redisClient.get(ITEM_INFO + ":" + itemId + ":"+ BASE);
        if(tbItem!=null){
            return tbItem;
        }
        //2.先查询mysql，并把查询结果缓存到redis中
        /********************解决缓存击穿************************/
        if(redisClient.setnx("SETNX_LOCK_KEY:"+itemId,itemId,30L)) {
            tbItem = tbItemMapper.selectByPrimaryKey(itemId);
            /**
             * 解决缓存穿透
             */
            if(tbItem == null){
                //把空对象保存到缓存
                redisClient.set(ITEM_INFO + ":" + itemId + ":"+ BASE,tbItem);
                //设置缓存的有效期
                redisClient.expire(ITEM_INFO + ":" + itemId + ":"+ BASE,30L);
                return tbItem;
            }

            //把数据保存到缓存
            redisClient.set(ITEM_INFO + ":" + itemId + ":"+ BASE,tbItem);
            //设置缓存的有效期
            redisClient.expire(ITEM_INFO + ":" + itemId + ":"+ BASE,ITEM_INFO_EXPIRE);
            redisClient.del("SETNX_LOCK_KEY:"+itemId);
            return tbItem;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }
    }

    @Override
    public PageResult selectTbItemAllByPage(Integer page, Long rows) {
        PageHelper.startPage(page,rows.intValue());

        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated DESC");


        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(tbItemList);

        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(pageInfo.getPageNum());
        pageResult.setTotalPage(Long.valueOf(pageInfo.getPages()));
        pageResult.setResult(pageInfo.getList());
        return pageResult;
    }

    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {

        //1.保存商品信息
        long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        tbItem.setPrice(tbItem.getPrice()*100);
        int tbItemNum = tbItemMapper.insertSelective(tbItem);
        //2.保存商品描述信息
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        int tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);
        //3.保存商品规格信息
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        int tbItemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);

        //发送mq,完成索引同步
        amqpTemplate.convertAndSend("item_exchange","item.add",itemId);
        return tbItemNum+tbItemDescNum+tbItemParamItemNum;
    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @Override
    public Integer deleteItemById(Long id) {
        Integer  itemId = tbItemMapper.deleteByPrimaryKey(id);
        redisClient.del("portal_catresult_redis_key");
        return itemId;
    }

    /**
     * 预修改
     * @param itemId
     * @return
     */
    @Override
    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        //根据商品 ID 查询商品
        TbItem item = this.tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item", item);
        //根据商品 ID 查询商品描述
        TbItemDesc itemDesc = this.tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", itemDesc.getItemDesc());
        //根据商品 ID 查询商品类目
        TbItemCat itemCat = this.tbItemCatMapper.selectByPrimaryKey(item.getCid());
        map.put("itemCat", itemCat.getName());
        //根据商品 ID 查询商品规格参数
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list =
                this.tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        if (list != null && list.size() > 0) {
            map.put("itemParamItem", list.get(0).getParamData());
        }
        return map;
    }

    /**
     * 修改
     * @param tbItem
     * @return
     */
    @Override
    public Integer updateTbItem(TbItem tbItem) {
        tbItem.setUpdated(new Date());
        Integer integer=tbItemMapper.updateByPrimaryKeySelective(tbItem);
        redisClient.del("portal_catresult_redis_key");
        return integer;
    }

    /**
     * 根据商品 ID 查询商品描述
     * @param itemId
     * @return
     */
    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {

        //查询缓存
        TbItemDesc tbItemDesc = (TbItemDesc) redisClient.get(
                ITEM_INFO + ":" + itemId + ":"+ DESC);
        if(tbItemDesc!=null){
            return tbItemDesc;
        }

        /********************解决缓存击穿************************/
        if (redisClient.setnx("DESC_LOCK_KEY:"+itemId, itemId, 30)) {

            TbItemDescExample example = new TbItemDescExample();
            TbItemDescExample.Criteria criteria = example.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemDesc> itemDescList = tbItemDescMapper.selectByExampleWithBLOBs(example);

            if(itemDescList!=null && itemDescList.size()>0){
                //把数据保存到缓存
                redisClient.set(ITEM_INFO + ":" + itemId + ":"+ DESC,itemDescList.get(0));
                //设置缓存的有效期
                redisClient.expire(ITEM_INFO + ":" + itemId + ":"+ DESC,ITEM_INFO_EXPIRE);
                return itemDescList.get(0);
            }
            /**
             * 解决缓存穿透
             */
            //把空对象保存到缓存
            redisClient.set(ITEM_INFO + ":" + itemId + ":"+ DESC,null);
            //设置缓存的有效期
            redisClient.expire(ITEM_INFO + ":" + itemId + ":"+ DESC,30L);
            redisClient.del("DESC_LOCK_KEY:"+itemId);
        }else{
            //获取锁失败
            selectItemDescByItemId(itemId);
        }
        return null;
    }

    @Override
    public Integer updateTbItemByOrderId(String orderId) {

        //1.
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);

        //2.
        int result = 0;
        for (int i = 0 ; i < tbOrderItemList.size(); i++){
            TbOrderItem tbOrderItem = tbOrderItemList.get(i);
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum()-tbOrderItem.getNum());
            result += tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }

        return result;
    }
}
