package com.usian.fegin;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-search-service")
public interface SearchServiceFeign {

    @RequestMapping("/service/search/importAll")
    boolean importAll();
}
