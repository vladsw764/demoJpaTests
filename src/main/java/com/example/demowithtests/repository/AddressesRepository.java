package com.example.demowithtests.repository;

import com.example.demowithtests.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressesRepository extends JpaRepository<Address, Integer> {
}
