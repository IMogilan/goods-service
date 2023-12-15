package ru.astondevs.goodsservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.goodsservice.dto.ProductDto;
import ru.astondevs.goodsservice.exception.PriceIncorrectException;
import ru.astondevs.goodsservice.exception.ProductOutOfStockException;
import ru.astondevs.goodsservice.mapper.ProductMapper;
import ru.astondevs.goodsservice.repository.ProductRepository;
import ru.astondevs.goodsservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDto> readAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto readById(Long id) {
        Objects.requireNonNull(id);

        var product = productRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Product with id = " + id + " not found"));
        return productMapper.toDto(product);
    }

    @Override
    public void sellProduct(Long id, ProductDto productDto, Integer requestedQuantity) {
        Objects.requireNonNull(id);

        var product = productRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Product with id = " + id + " not found"));

        var priceFromDataBase = product.getPrice();
        var priceFromRequest = productDto.price();
        if (!priceFromDataBase.equals(priceFromRequest)) {
            throw new PriceIncorrectException("Sorry, the price of the product has changed.");
        }

        Integer quantityInDb = product.getQuantity();
        if (quantityInDb <= 0) {
            throw new ProductOutOfStockException("Sorry, the product is currently out of stock.");
        } else if (quantityInDb - requestedQuantity < 0) {
            throw new ProductOutOfStockException("Sorry, the remain quantity of product " + quantityInDb);
        }

        quantityInDb -= requestedQuantity;
        product.setQuantity(quantityInDb);
        productRepository.save(product);
    }
}
