package ru.astondevs.goodsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.goodsservice.dto.ProductDto;
import ru.astondevs.goodsservice.service.ProductService;

@RestController
@RequestMapping("/api/goods/")
public class ProductController {

    public static final String SELL_MESSAGE = "Product sold";

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(productService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        return ResponseEntity.ok(productService.readById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> sellProduct(@PathVariable("id") Long id, @RequestBody ProductDto dto){
        productService.sellProduct(id, dto);
        return ResponseEntity.ok(SELL_MESSAGE);
    }
}
