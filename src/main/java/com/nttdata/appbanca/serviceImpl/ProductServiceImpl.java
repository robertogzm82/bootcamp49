package com.nttdata.appbanca.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.repository.CustomerRepository;
import com.nttdata.appbanca.repository.ProductRepository;
import com.nttdata.appbanca.service.CustomerService;
import com.nttdata.appbanca.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Transactional
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Transactional
	public Optional<Product> getProduct(String id) {
		return productRepository.findById(id);
	}

	@Transactional
	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}

	public boolean ExistsCustomerId(Product product) {
		String customerId =  product.get_customerId();
		return customerService.getCustomer(customerId).isPresent();
	}
	
	public String getCustomerTipo(Product product) {
		return customerService.getCustomer(product.get_customerId()).get().getTipo().name();
	}

	public int countTipoProducto(Product producto,String tipoProducto) {
		return productRepository.findBy_customerId(producto.get_customerId())
				.stream().filter(p->p.getTipo().name().equals(tipoProducto))
				.collect(Collectors.toList())
				.size();
	}

}
