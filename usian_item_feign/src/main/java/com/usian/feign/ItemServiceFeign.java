package com.usian.feign;


import com.usian.fallback.ItemServiceFallback;
import com.usian.pojo.*;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "usian-item-service",fallbackFactory = ItemServiceFallback.class)
public interface ItemServiceFeign {

    /*
    * 查询商品信息
    * */


    @RequestMapping("/service/item/selectItemInfo")
    public TbItem selectItemInfo(@RequestParam Long itemId);

    /*
    * 分页查询商品信息
    *
    * */

    @RequestMapping("/service/item/selectTbItemAllByPage")
    public  PageResult selectTbItemAllByPage(@RequestParam Integer page, @RequestParam Long rows);


    /*
    * 根据id查询商品类目
    *
    * */

    @RequestMapping("/service/itemCat/selectItemCategoryByParentId")
    List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id);


    /*
    * 查询商品规格参数模板
    * */

    @RequestMapping("/service/itemParam/selectItemParamByItemCatId/{itemCatId}")
    TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId);


    @RequestMapping("/service/item/insertTbItem")
    Integer insertTbItem(@RequestBody TbItem tbItem, @RequestParam String desc, @RequestParam String itemParams);


    //分页查询商品规格模板
    @RequestMapping("/service/itemParam/selectItemParamAll")
    PageResult selectItemParamAll(@RequestParam Integer page, @RequestParam Integer rows);


    //添加商品规格模板
    @RequestMapping("/service/itemParam/insertItemParam")
    Integer insertTtemParam(@RequestParam Long itemCatId, @RequestParam String paramData);


    /*
    * 删除商品规格模板
    * */
    @RequestMapping("/service/itemParam/deleteItemParamById")
    Integer deleteItemParamById(@RequestParam Long id);
    /*
    * 删除商品
    * */
    @RequestMapping("/service/item/deleteItemById")
    Integer deleteItemById(@RequestParam("id") Long itemId);
    /*
    * 查询首页商品分类
    * */
    @RequestMapping("/service/itemCat/selectItemCategoryAll")
    CatResult selectItemCategoryAll();

    /*
    * 商品修改回显
    * */
    @RequestMapping("/service/item/preUpdateItem")
    Map<String,Object> preUpdateItem(@RequestParam("itemId") Long itemId);

    /*
    * 商品修改
    * */
    @RequestMapping("/service/item/updateTbItem")
    Integer updateTbItem(TbItem tbItem);

    /*
    * 查询商品介绍
    * */
    @RequestMapping("/service/item/selectItemDescByItemId")
    TbItemDesc selectItemDescByItemId(@RequestParam Long itemId);

    /*
    * 根据商品 ID 查询商品规格参数
    * */
    @RequestMapping("/service/itemParam/selectIbItemParamItemByItemId")
    TbItemParamItem selectTbItemParamItemByItemId(@RequestParam Long itemId);
}
