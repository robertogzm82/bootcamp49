package com.nttdata.appbanca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.Transaction;

public interface TransactionService {

	Transaction saveTransaction(Transaction transaction) ;
	
	Optional<Transaction> getTransaction(String id);
	
	List<Transaction> getAllTransaction();

	List<Transaction> findByProductId(String productId);
}
