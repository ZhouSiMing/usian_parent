package com.usian.feign;


import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-item-service")
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
}
