package com.multipartyloops.evochia.core.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;
import com.multipartyloops.evochia.core.order.exceptions.ProductIdNotMatchingException;
import com.multipartyloops.evochia.core.order.exceptions.ProductOptionMatchingNotFoundException;
import com.multipartyloops.evochia.core.product.dto.ProductDto;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import com.multipartyloops.evochia.entrypoints.order.dtos.NewOrder;
import com.multipartyloops.evochia.persistance.order.OrderRepository;
import com.multipartyloops.evochia.persistance.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository<OrderInfoDto> orderRepository;
    private final ProductRepository<ProductDto> productRepository;
    private ObjectMapper objectMapper;

    public OrderService(OrderRepository<OrderInfoDto> orderRepository,
                        ProductRepository<ProductDto> productRepository,
                        ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    public String addNewOrder(NewOrder newOrder) {
        final var orderInfoDto = orderRepository.addNewOrder(
                newOrder.getTableId(),
                newOrder.getUserId(),
                newOrder.getComments(),
                constructDetailsFromNewOrder(newOrder)
        );
        return orderInfoDto.getOrderId();
    }

    private String constructDetailsFromNewOrder(NewOrder newOrder) {
        OrderDetails orderDetails = new OrderDetails();
        newOrder.getDetails().forEach(orderDetail -> {
            final var productFromDb = getProductById(orderDetail.getProductId());
            orderDetails.addToOrder(
                    new OrderDetails.OrderProduct(
                            UUID.randomUUID().toString(),
                            orderDetail.getProductId(),
                            getProductOptionsFromProductOptionIds(orderDetail.getOptions(), productFromDb),
                            orderDetail.getDiscountPercentage(),
                            orderDetail.getNotes(),
                            false,
                            orderDetail.getTerminalId(),
                            productFromDb.getName(),
                            productFromDb.getPrice(),
                            false
                    )
            );
        });
        return orderDetails.toJson(objectMapper);
    }

    private List<ProductOptionDto> getProductOptionsFromProductOptionIds(List<String> options, ProductDto productFromDb) {
        return options.stream()
                      .map(optionId -> productFromDb.getProductOptions().stream()
                                                    .filter(option -> option.getProductOptionId().equals(optionId))
                                                    .findFirst()
                                                    .orElseThrow(() -> new ProductOptionMatchingNotFoundException(String.format("Could not find product option id %s for product: %s", optionId, productFromDb.getProductId()))))
                      .collect(Collectors.toList());
    }

    private ProductDto getProductById(String productId) {
        return productRepository.getProductById(productId)
                                .orElseThrow(() -> new ProductIdNotMatchingException(String.format("Could not find product with id: %s", productId)));
    }
}
