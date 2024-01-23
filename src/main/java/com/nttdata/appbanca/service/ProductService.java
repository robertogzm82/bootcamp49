package com.nttdata.appbanca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;

public interface ProductService {

	Product saveProduct(Product product) ;
	
	Optional<Product> getProduct(String id);

	List<Product> getAllProduct();

	boolean ExistsCustomerId(Product product);
	
	String getCustomerTipo(Product product);

	int countTipoProducto(Product producto,String tipoProducto);
}
