package com.learnjwt.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learnjwt.example.dto.BankAccountRequest;
import com.learnjwt.example.dto.BankAccountResponse;
import com.learnjwt.example.entity.BankAccount;
import com.learnjwt.example.repository.BankingAcountRepo;

@Service
public class BankAccountService {
    
    @Autowired
    private BankingAcountRepo bankingAcountRepo;

    public List<BankAccount> getAllAccounts() throws Exception {
        try {
            return bankingAcountRepo.findAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while fetching bank accounts", e);
        }
    } 

    public BankAccount getBankAccountById(Long id) throws Exception {
        try {
            return bankingAcountRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Bank account not found with id: " + id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while fetching bank account", e);
        }
    }

    public BankAccountResponse createBankAccount(BankAccountRequest requestBankAccount) throws Exception {
        boolean existingAccount = bankingAcountRepo.existsByAccountNumber(requestBankAccount.getAccountNumber());
        if (existingAccount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bank account with number " + requestBankAccount.getAccountNumber() + " already exists");
        }
        try {
            BankAccount newBankAccount = new BankAccount();
            mapRequestToBankAccount(requestBankAccount, newBankAccount);
            BankAccount savedBankAccount = bankingAcountRepo.save(newBankAccount);
            BankAccountResponse response = new BankAccountResponse();
            mapBankAccountToResponse(savedBankAccount, response);
            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while creating bank account", e);
        }
    }

    public BankAccountResponse updateBankAccount(Long id, BankAccountRequest requestBankAccountRequest) throws Exception {
        try {
            BankAccount existingBankAccount = bankingAcountRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found with id: " + id));
            mapRequestToBankAccount(requestBankAccountRequest, existingBankAccount);
            BankAccount savedBankAccount = bankingAcountRepo.save(existingBankAccount);
            BankAccountResponse response = new BankAccountResponse();
            mapBankAccountToResponse(savedBankAccount, response);
            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while updating bank account", e);
        }
    }

    public void deleteBankAccount(Long id) throws Exception {
        try {
            BankAccount existingBankAccount = bankingAcountRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found with id: " + id));
            bankingAcountRepo.delete(existingBankAccount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while deleting bank account", e);
        }
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
