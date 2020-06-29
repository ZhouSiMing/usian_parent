package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.Request;

import java.util.Map;

@RestController
@RequestMapping("/backend/item")
@Api("商品管理接口")
public class ItemController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /*
    * 查询商品详情
    * */

    @PostMapping("/selectItemInfo")

    @ApiOperation(value = "查询商品基本信息",notes = "根据itemId查询该商品的基本信息")
    @ApiImplicitParam(name="itemId",type = "Long",value = "商品id")

    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem == null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }

    /*
    * 分页查询商品列表
    * */

    @GetMapping("/selectTbItemAllByPage")

    @ApiOperation(value = "查询商品并分页处理",notes = "分页查询商品信息，每页显示2条")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",type = "Integer",value = "页码"),
            @ApiImplicitParam(name = "rows",type = "Long",value = "每页多少条")
    })

    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "2") Long rows){
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page,rows);
        if(pageResult.getResult()!=null && pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
            return Result.error("查无结果");
    }




    @PostMapping("/insertTbItem")

    @ApiOperation(value = "添加商品",notes = "添加商品及描述和规格参数信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "desc",type = "String",value = "商品描述信息"),
            @ApiImplicitParam(name = "itemParams",type = "String",value = "商品规格参数")
    })

    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer result = itemServiceFeign.insertTbItem(tbItem,desc,itemParams);
        if (result==3){
            return Result.ok();
        }
            return Result.error("保存失败");
    }

    /**
     * 删除商品
     * @param itemId
     * @return
     */
    @RequestMapping("/deleteItemById")
    public Result deleteItemById(Long itemId){
        Integer ItemById = itemServiceFeign.deleteItemById(itemId);
        if(itemId!=null){
            return Result.ok();
        }
        return Result.error("删除失败");
    }

    /**
     * 根据itemId回显商品信息
     * @param itemId
     * @return
     */
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String,Object> map = itemServiceFeign.preUpdateItem(itemId);
        if(map.size()>0){
            return Result.ok(map);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("/updateTbItem")
    public Result updateTbItem(TbItem tbItem) {
        Integer integer=itemServiceFeign.updateTbItem(tbItem);
        if(integer!=null){
            return Result.ok();
        }
        return Result.error("修改失败");
    }
}


