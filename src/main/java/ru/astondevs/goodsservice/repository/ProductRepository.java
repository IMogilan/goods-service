package ru.astondevs.goodsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.goodsservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}