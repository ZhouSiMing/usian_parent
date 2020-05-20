package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/content")
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService ContentCategoryService;

    @RequestMapping("/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(Long id){
        return ContentCategoryService.selectContentCategoryByParentId(id);
    }
}
