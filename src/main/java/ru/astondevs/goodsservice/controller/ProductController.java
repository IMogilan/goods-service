package ru.astondevs.goodsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.goodsservice.dto.ProductDto;
import ru.astondevs.goodsservice.service.ProductService;

import static ru.astondevs.goodsservice.util.Constant.DEFAULT_PAGE;
import static ru.astondevs.goodsservice.util.Constant.DEFAULT_SIZE;
import static ru.astondevs.goodsservice.util.Constant.SELL_MESSAGE;

@RestController
@RequestMapping("/api/goods/")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false, defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(required = false, defaultValue = DEFAULT_SIZE) int size) {
        return ResponseEntity.ok(productService.readAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.readById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> sellProduct(@PathVariable("id") Long id, @RequestBody ProductDto dto) {
        productService.sellProduct(id, dto);
        return ResponseEntity.accepted().body(SELL_MESSAGE);
    }
}
