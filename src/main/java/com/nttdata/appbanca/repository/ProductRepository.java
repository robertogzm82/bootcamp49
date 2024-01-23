package com.nttdata.appbanca.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
	                                      
	List<Customer> findBy_customerId(String customerid);
}
