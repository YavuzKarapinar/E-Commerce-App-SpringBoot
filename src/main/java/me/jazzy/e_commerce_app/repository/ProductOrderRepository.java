package me.jazzy.e_commerce_app.repository;

import me.jazzy.e_commerce_app.model.ProductOrder;
import me.jazzy.e_commerce_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    Optional<ProductOrder> findByUser(User user);
}