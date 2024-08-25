package me.jazzy.e_commerce_app.repository;

import me.jazzy.e_commerce_app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}