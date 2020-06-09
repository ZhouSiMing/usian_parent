package com.usian.controller;


import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.service.ItemService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/item")
public class ItemController {


    @Autowired
    private ItemService itemService;

    /*
    * 查询商品信息
    * */
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return itemService.selectItemInfo(itemId);

    }

    /*
    * 分页查询商品信息
    * */
    @RequestMapping("selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(Integer page,Long rows){
        return itemService.selectTbItemAllByPage(page,rows);
    }


    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem, String desc, String itemParams){
        return itemService.insertTbItem(tbItem,desc,itemParams);
    }

    @RequestMapping("/deleteItemById")
    public Integer deleteItemById(@RequestParam Long id){
        return itemService.deleteItemById(id);
    }



    /**
     * 根据商品 ID 查询商品，商品分类，商品描述，商品规格参数
     * @param itemId
     * @return
     */
    @RequestMapping("/preUpdateItem")
    public Map<String,Object> preUpdateItem(Long itemId){
        return this.itemService.preUpdateItem(itemId);
    }

    @RequestMapping("/updateTbItem")
    public Integer updateTbItem(@RequestBody TbItem tbItem){
        return itemService.updateTbItem(tbItem);
    }


    @RequestMapping("/selectItemDescByItemId")
    public TbItemDesc selectItemDescByItemId(Long itemId){
        return itemService.selectItemDescByItemId(itemId);
    }
}
