package me.jazzy.e_commerce_app.service;

import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.model.Product;
import me.jazzy.e_commerce_app.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }
}