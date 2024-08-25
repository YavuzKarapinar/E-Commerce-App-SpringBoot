package me.jazzy.e_commerce_app.service;

import lombok.AllArgsConstructor;
import me.jazzy.e_commerce_app.model.ProductOrder;
import me.jazzy.e_commerce_app.model.User;
import me.jazzy.e_commerce_app.repository.ProductOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private ProductOrderRepository orderRepository;

    public List<ProductOrder> getOrders(User user) {
        return orderRepository.findByUser(user)
                .stream().toList();
    }
}