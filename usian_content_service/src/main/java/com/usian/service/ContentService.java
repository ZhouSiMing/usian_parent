package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;

import java.util.List;

public interface ContentService {

    PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId);
}
