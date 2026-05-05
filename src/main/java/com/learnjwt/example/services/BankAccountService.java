package com.learnjwt.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.learnjwt.example.dto.BankAccountRequest;
import com.learnjwt.example.dto.BankAccountResponse;
import com.learnjwt.example.entity.BankAccount;
import com.learnjwt.example.exception.ConflictException;
import com.learnjwt.example.exception.ResourceNotFoundException;
import com.learnjwt.example.repository.BankingAcountRepo;

@Service
public class BankAccountService {
    
    @Autowired
    private BankingAcountRepo bankingAcountRepo;

    public List<BankAccount> getAllAccounts() {
        return bankingAcountRepo.findAll();
    } 

    public BankAccount getBankAccountById(Long id) {
        return bankingAcountRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + id));
    }

    public BankAccountResponse createBankAccount(BankAccountRequest requestBankAccount) {
        boolean existingAccount = bankingAcountRepo.existsByAccountNumber(requestBankAccount.getAccountNumber());
        if (existingAccount) {
            throw new ConflictException("Bank account with number " + requestBankAccount.getAccountNumber() + " already exists");
        }

        BankAccount newBankAccount = new BankAccount();
        mapRequestToBankAccount(requestBankAccount, newBankAccount);

        try {
            BankAccount savedBankAccount = bankingAcountRepo.save(newBankAccount);
            BankAccountResponse response = new BankAccountResponse();
            mapBankAccountToResponse(savedBankAccount, response);
            return response;
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Bank account with number " + requestBankAccount.getAccountNumber() + " already exists");
        }
    }

    public BankAccountResponse updateBankAccount(Long id, BankAccountRequest requestBankAccountRequest) {
        BankAccount existingBankAccount = bankingAcountRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + id));

        if (!existingBankAccount.getAccountNumber().equals(requestBankAccountRequest.getAccountNumber())
                && bankingAcountRepo.existsByAccountNumber(requestBankAccountRequest.getAccountNumber())) {
            throw new ConflictException("Bank account with number " + requestBankAccountRequest.getAccountNumber() + " already exists");
        }

        mapRequestToBankAccount(requestBankAccountRequest, existingBankAccount);

        try {
            BankAccount savedBankAccount = bankingAcountRepo.save(existingBankAccount);
            BankAccountResponse response = new BankAccountResponse();
            mapBankAccountToResponse(savedBankAccount, response);
            return response;
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Bank account with number " + requestBankAccountRequest.getAccountNumber() + " already exists");
        }
    }

    public void deleteBankAccount(Long id) {
        BankAccount existingBankAccount = bankingAcountRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + id));
        bankingAcountRepo.delete(existingBankAccount);
    }



    public void mapRequestToBankAccount(BankAccountRequest request, BankAccount bankAccount) {
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountHolderName(request.getAccountHolderName());
        bankAccount.setBalance(request.getBalance());
        bankAccount.setAccountType(request.getAccountType());
    }

    public void mapBankAccountToResponse(BankAccount bankAccount, BankAccountResponse response) {
        response.setAccountNumber(bankAccount.getAccountNumber());
        response.setAccountHolderName(bankAccount.getAccountHolderName());
        response.setBalance(bankAccount.getBalance());
        response.setAccountType(bankAccount.getAccountType());
    }
}
