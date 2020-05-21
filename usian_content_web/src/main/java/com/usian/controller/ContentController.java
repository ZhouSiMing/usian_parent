package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("backend/content")
public class ContentController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;

    //内容查询
    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(@RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "300") Integer rows,Long categoryId){
        PageResult pageResult =  contentServiceFeign.selectTbContentAllByCategoryId(page,rows,categoryId);
        if (pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
            return Result.error("查询结果");
    }
}
