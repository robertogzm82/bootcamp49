package com.nttdata.appbanca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.ProductType;

public interface ProductService {

	Product saveProduct(Product product) ;
	
	Optional<Product> getProduct(String id);

	List<Product> getAllProduct();
	
	String getCustomerTipo(Product product);
	
	List<Product> findByCustomerId(String id);
	
	int getConsumoTotal(Product producto);
	
	int cantProducTipoProducCustomer(Product product,ProductType producttype);
	
}
