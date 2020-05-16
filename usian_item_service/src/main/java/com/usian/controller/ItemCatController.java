package com.usian.controller;


import com.usian.pojo.TbItemCat;
import com.usian.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("service/itemCat")
public class ItemCatController {

    @Autowired
    private ItemCatService ItemCatService;

    /*
    *根据商品id查询商品类目
    * */


    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(Long id){
        return ItemCatService.selectItemCategoryByParentId(id);
    }
}
