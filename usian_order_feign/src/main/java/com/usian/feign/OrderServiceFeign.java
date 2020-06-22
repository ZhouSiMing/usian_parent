package com.usian.feign;

import com.usian.pojo.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-order-servoce")
public interface OrderServiceFeign {

    @RequestMapping("/service/order/insertOrder")
    public String insertOrder(OrderInfo orderInfo);
}