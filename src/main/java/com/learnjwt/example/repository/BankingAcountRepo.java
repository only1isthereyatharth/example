package com.learnjwt.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnjwt.example.entity.BankAccount;

public interface BankingAcountRepo extends JpaRepository<BankAccount, Long>{
    boolean existsByAccountNumber(String accountNumber);
}
