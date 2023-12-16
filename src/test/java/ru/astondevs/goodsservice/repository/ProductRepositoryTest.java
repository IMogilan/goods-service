package ru.astondevs.goodsservice.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.astondevs.goodsservice.model.Product;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void registerTestcontainersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void findAllSuccess() {
        var actualResult = productRepository.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(10);
    }

    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        productRepository.deleteAll();
        var actualResult = productRepository.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void findByIdSuccess() {
        var id = 1L;
        var expectingResult = new Product(1L, "Book1", "Description for Book1", 10, BigDecimal.valueOf(19.99));
        var actualResult = productRepository.findById(id);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(expectingResult);
    }

    @Test
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent() {
        var id = 10000L;
        var actualResult = productRepository.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void updateSuccess() {
        var id = 1L;

        var prevListSize = productRepository.findAll().size();

        var optional = productRepository.findById(id);
        assertThat(optional).isPresent();
        var entityInDatabase = optional.get();

        var previousQuantity = entityInDatabase.getQuantity();
        var updatedQuantity = previousQuantity - 1;

        var productWithUpdatedQuantity = new Product(id, entityInDatabase.getName(), entityInDatabase.getDescription(),
                updatedQuantity, entityInDatabase.getPrice());

        var updatedValue = productRepository.save(productWithUpdatedQuantity);
        assertThat(updatedValue).isNotNull();
        assertThat(updatedValue.getId()).isEqualTo(id);
        assertThat(updatedValue).isEqualTo(productWithUpdatedQuantity);

        var newListSize = productRepository.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

}