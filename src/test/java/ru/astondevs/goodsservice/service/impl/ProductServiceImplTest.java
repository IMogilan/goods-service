package ru.astondevs.goodsservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astondevs.goodsservice.dto.ProductDto;
import ru.astondevs.goodsservice.exception.PriceIncorrectException;
import ru.astondevs.goodsservice.exception.ProductOutOfStockException;
import ru.astondevs.goodsservice.mapper.ProductMapper;
import ru.astondevs.goodsservice.model.Product;
import ru.astondevs.goodsservice.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.astondevs.goodsservice.TestData.getProduct1;
import static ru.astondevs.goodsservice.TestData.getProduct2;
import static ru.astondevs.goodsservice.TestData.getProductDto1;
import static ru.astondevs.goodsservice.TestData.getProductDto2;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository repository;
    @Mock
    private ProductMapper mapper;
    @InjectMocks
    private ProductServiceImpl service;

    private final ProductDto productDto1 = getProductDto1();
    private final ProductDto productDto2 = getProductDto2();
    private final List<ProductDto> dtoList = List.of(productDto1, productDto2);

    private final Product product1 = getProduct1();
    private final Product product2 = getProduct2();
    private final List<Product> products = List.of(product1, product2);


    @Test
    void readAll_whenInvoked_thenReturnAllDtoList() {
        List<ProductDto> expected = dtoList;
        when(repository.findAll()).thenReturn(products);
        when(mapper.toDto(product1)).thenReturn(productDto1);
        when(mapper.toDto(product2)).thenReturn(productDto2);

        List<ProductDto> actual = service.readAll();

        assertEquals(expected, actual);
    }

    @Test
    void readById_whenProductFound_thenReturnDto() {
        ProductDto expected = productDto1;
        when(repository.findById(productDto1.id())).thenReturn(Optional.of(product1));
        when(mapper.toDto(product1)).thenReturn(productDto1);

        ProductDto actual = service.readById(productDto1.id());

        assertEquals(expected, actual);
    }

    @Test
    void readById_whenProductNotFound_thenThrowException() {
        when(repository.findById(productDto1.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.readById(productDto1.id()));
    }

    @Test
    void sellProduct_whenProductFoundAndPriceAndQuantityOk_thenUpdatingInRepository() {
        Product product = getProduct1();
        product.setQuantity(product.getQuantity() - product1.getQuantity());
        when(repository.findById(productDto1.id())).thenReturn(Optional.of(product1));
        when(repository.save(product)).thenReturn(product);

        service.sellProduct(productDto1.id(), productDto1);

        verify(repository, atLeastOnce()).save(product);
    }

    @Test
    void sellProduct_whenProductNotFound_thenThrowException() {
        when(repository.findById(productDto1.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.sellProduct(productDto1.id(), productDto1));

        verify(repository, never()).save(any());
    }

    @Test
    void sellProduct_whenPriceIsDiffers_thenThrowException() {
        when(repository.findById(productDto2.id())).thenReturn(Optional.of(product1));

        assertThrows(PriceIncorrectException.class,
                () -> service.sellProduct(productDto2.id(), productDto2));

        verify(repository, never()).save(any());
    }

    @Test
    void sellProduct_whenStockIsZero_thenThrowException() {
        Product product = getProduct2();
        product.setQuantity(0);

        when(repository.findById(productDto2.id())).thenReturn(Optional.of(product));

        assertThrows(ProductOutOfStockException.class,
                () -> service.sellProduct(productDto2.id(), productDto2));

        verify(repository, never()).save(any());
    }

    @Test
    void sellProduct_whenRequestedMoreThanInStock_thenThrowException() {
        Product product = getProduct2();
        product.setQuantity(1);

        when(repository.findById(productDto2.id())).thenReturn(Optional.of(product));

        assertThrows(ProductOutOfStockException.class,
                () -> service.sellProduct(productDto2.id(), productDto2));

        verify(repository, never()).save(any());
    }
}