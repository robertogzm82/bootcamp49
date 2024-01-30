package com.nttdata.appbanca.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Transaction;
import com.nttdata.appbanca.repository.CustomerRepository;
import com.nttdata.appbanca.repository.ProductRepository;
import com.nttdata.appbanca.repository.TransactionRepository;
import com.nttdata.appbanca.service.CustomerService;
import com.nttdata.appbanca.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Transactional
	public Transaction saveTransaction(Transaction transaction) {
		transaction.setDate(LocalDateTime.now());
		return transactionRepository.save(transaction);
	}

	@Transactional
	public Optional<Transaction> getTransaction(String id) {
		return transactionRepository.findById(id);
	}
	
	@Transactional
	public List<Transaction> getAllTransaction() {
		return transactionRepository.findAll();
	}

	@Transactional
	public List<Transaction> findByProductId(String productId) {
		return transactionRepository.findByProductid(productId);
	}
	
}
