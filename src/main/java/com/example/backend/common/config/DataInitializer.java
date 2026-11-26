package com.example.backend.common.config;

import com.example.backend.product.Product;
import com.example.backend.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;

    public DataInitializer(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Product("Mechanical Keyboard", "75% hot-swappable keyboard", new BigDecimal("89.90"), 25),
                new Product("Wireless Mouse", "Ergonomic 2.4GHz mouse", new BigDecimal("39.50"), 120),
                new Product("USB-C Hub", "7-in-1 aluminum hub", new BigDecimal("49.00"), 60)
        ));
    }
}
