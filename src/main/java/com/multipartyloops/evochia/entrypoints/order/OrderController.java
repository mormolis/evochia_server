package com.multipartyloops.evochia.entrypoints.order;

import com.multipartyloops.evochia.core.order.OrderService;
import com.multipartyloops.evochia.entrypoints.order.dtos.NewOrder;
import com.multipartyloops.evochia.entrypoints.order.dtos.NewOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequestMapping(value = "/order/new", method = RequestMethod.POST)
    public ResponseEntity<NewOrderResponse> newOrder(@RequestHeader Map<String, String> RequestHeaders, @RequestBody NewOrder body) {
        String newOrderId = orderService.addNewOrder(body);
        return new ResponseEntity<>(new NewOrderResponse(newOrderId), HttpStatus.CREATED);
    }

}
