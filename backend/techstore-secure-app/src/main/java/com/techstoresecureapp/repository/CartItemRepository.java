package com.techstoresecureapp.repository;

import com.techstoresecureapp.entity.CarItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CarItem, Long> {

    List<CarItem> findByUserId(Long userId);

    Optional<CarItem> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);
}