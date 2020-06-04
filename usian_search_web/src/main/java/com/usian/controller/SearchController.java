package com.usian.controller;


import com.netflix.discovery.converters.Auto;
import com.usian.fegin.SearchServiceFeign;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchController {


    @Autowired
    private SearchServiceFeign searchServiceFeign;

    @RequestMapping("/importAll")
    public Result importAll(){
        boolean importAll = searchServiceFeign.importAll();
        if (importAll){
            return Result.ok();
        }
            return Result.error("导入失败！");
    }
}
