package com.nttdata.appbanca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;

public interface CustomerService {

	Customer saveCustomer(Customer customer) ;
	
	Optional<Customer> getCustomer(String id);

	List<Customer> getAllCustomer();
	
	
	
}
