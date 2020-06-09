package com.usian.fegin;


import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchServiceFeign {

    @RequestMapping("/service/search/importAll")
    boolean importAll();

    @RequestMapping("/service/search/list")
    List<SearchItem> selectByq(@RequestParam String q, @RequestParam Long page, @RequestParam Integer pageSize);
}
