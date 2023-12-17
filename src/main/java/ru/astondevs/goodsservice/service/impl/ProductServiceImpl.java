package ru.astondevs.goodsservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    public List<ProductDto> readAll(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest).stream()
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
        Objects.requireNonNull(productDto);
        var requestedQuantity = productDto.quantity();
        var priceFromRequest = productDto.price();
        Objects.requireNonNull(requestedQuantity);
        Objects.requireNonNull(priceFromRequest);

        var product = productRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Product with id = " + id + " not found"));

        var priceFromDataBase = product.getPrice();
        if (!priceFromDataBase.equals(priceFromRequest)) {
            throw new PriceIncorrectException("Sorry, the price of the product has changed.");
        }

        var quantityInDataBase = product.getQuantity();
        if (quantityInDataBase <= 0) {
            throw new ProductOutOfStockException("Sorry, the product is currently out of stock.");
        } else if (requestedQuantity > quantityInDataBase) {
            throw new ProductOutOfStockException("Sorry, the remain quantity of product " + quantityInDataBase);
        }

        quantityInDataBase -= requestedQuantity;
        product.setQuantity(quantityInDataBase);
        productRepository.save(product);
    }
}
