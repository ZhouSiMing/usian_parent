package com.usian.service;

import com.netflix.discovery.converters.Auto;
import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpI implements ItemCatService {

    @Autowired
    private TbItemCatMapper TbItemCatMapper;

    @Autowired
    private RedisClient redisClient;

    @Value("${PROTAL_CATRESULT_KEY}")
    private String PROTAL_CATRESULT_KEY;


    /*
    * 根据商品id查询商品类目
    * */

    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andStatusEqualTo(1);
        criteria.andParentIdEqualTo(id);
        return TbItemCatMapper.selectByExample(tbItemCatExample);
    }

    @Override
    public CatResult selectItemCategoryAll() {

        //1.先查redis
        CatResult catResultRedis = (CatResult) redisClient.get(PROTAL_CATRESULT_KEY);
        if (catResultRedis!=null){
            //2.如果redis有，直接ireturn
            return catResultRedis;
        }

        //3.如果redis没有，则查询数据库并把结果放到redis中



        //1.查询商品类目
        //因为一级菜单有子菜单，子菜单有子子菜单，所以要递归调用
        List catlist = getCatlist(0L);
        CatResult catResult = new CatResult();
        catResult.setData(catlist);

        redisClient.set(PROTAL_CATRESULT_KEY,catResult);

        return catResult;
    }

    private List getCatlist(Long parentId){
        //2.把查询结果装载到List<CatNode>,并且只装18次
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> tbItemCatList = TbItemCatMapper.selectByExample(tbItemCatExample);

        //拼接catnode
        List catNodeList = new ArrayList();
        int count = 0;
        for (int i = 0; i < tbItemCatList.size(); i++){
            TbItemCat tbItemCat = tbItemCatList.get(i);
            //该类目是父节点
            if (tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatlist(tbItemCat.getId()));
                catNodeList.add(catNode);
                count = count + 1;
                if (count == 18){
                    break;
                }
            }else{
                //该节点不是父节点，直接把类目名称添加到catnodelist
                catNodeList.add(tbItemCat.getName());
            }
        }
        return catNodeList;
    }
}
