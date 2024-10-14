package me.jazzy.e_commerce_app.repository;

import me.jazzy.e_commerce_app.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser_Id(Long id);
}