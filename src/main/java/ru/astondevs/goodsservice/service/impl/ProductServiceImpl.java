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
    public void sellProduct(Long id, ProductDto productDto) {
        Objects.requireNonNull(id);

        var product = productRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Product with id = " + id + " not found"));

        if(product.getPrice() != productDto.price()){
            throw new PriceIncorrectException("Sorry, the price of the product has changed.");
        }

        var quantity = product.getQuantity();
        if (quantity <= 0) {
            throw new ProductOutOfStockException("Sorry, the product is currently out of stock.");
        }

        quantity -= 1;
        product.setQuantity(quantity);
        productRepository.save(product);
    }
}
