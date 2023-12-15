package ru.astondevs.goodsservice.service;

import ru.astondevs.goodsservice.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> readAll();

    ProductDto readById(Long id);

    void sellProduct(Long id, ProductDto productDto, Integer quantity);
}
