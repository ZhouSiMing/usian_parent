package com.usian.feign;

import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.channels.SelectableChannel;
import java.util.List;

@FeignClient("usian-content-service")
public interface ContentServiceFeign {

    @RequestMapping("/service/content/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id);
}
