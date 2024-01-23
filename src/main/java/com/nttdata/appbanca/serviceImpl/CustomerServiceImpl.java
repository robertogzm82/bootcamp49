package com.nttdata.appbanca.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.repository.CustomerRepository;
import com.nttdata.appbanca.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Transactional
	public Customer saveCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	@Transactional
	public Optional<Customer> getCustomer(String id) {
		return customerRepository.findById(id);
	}

	public List<Customer> getAllCustomer() {
		return customerRepository.findAll();
	}

}
