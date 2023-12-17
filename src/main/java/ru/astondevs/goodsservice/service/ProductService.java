package ru.astondevs.goodsservice.service;

import org.springframework.data.domain.PageRequest;
import ru.astondevs.goodsservice.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> readAll(PageRequest pageRequest);

    ProductDto readById(Long id);

    void sellProduct(Long id, ProductDto productDto);
}
