package ru.astondevs.goodsservice.dto;

import lombok.Value;

@Value
public record ProductDto(Long id, String name, String description, Integer quantity, Double price) {
}
