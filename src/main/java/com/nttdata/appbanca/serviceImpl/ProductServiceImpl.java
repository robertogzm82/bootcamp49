package com.nttdata.appbanca.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nttdata.appbanca.controller.ProductController;
import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.ProductType;
import com.nttdata.appbanca.repository.CustomerRepository;
import com.nttdata.appbanca.repository.ProductRepository;
import com.nttdata.appbanca.repository.TransactionRepository;
import com.nttdata.appbanca.service.CustomerService;
import com.nttdata.appbanca.service.ProductService;
import com.nttdata.appbanca.service.TransactionService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private TransactionService transactionService;
	
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
	
	@Transactional
	public String getCustomerTipo(Product product) {
		return customerService.getCustomer( product.getCustomerId() ).get().getTipo().name() ;
	}

	@Transactional
	public int cantProducTipoProducCustomer(Product producto,ProductType producttype) {
		//productRepository.findById(p.get_Id()) 
		return customerService.getCustomer( producto.getCustomerId() ).get().getProductos()
				.stream()
				.filter(p-> productRepository.findById( p.getId() ).get().getTipo().name().equals(producttype.name()) )
				.collect(Collectors.toList())
				.size();
	}
	
	/*
	@Override
	public int getConsumoTotal(Product producto) {
		
		//TransactionType a;
		//a.name()
		return transactionService.getByCustomer(customer).stream()
			    .filter( a -> a.equals("") )
			    
		       .mapToInt( t -> if( t.getTipo().name().equals("") )     
						  return t.getMonto();
						else
						  return t.getMonto()*(-1);		
				)
		       .sum();
	}
    */

	@Override
	public int getConsumoTotal(Product producto) {
		return transactionService.findByProductId( producto.getId() )  //get().getProductos()
				.stream()
				.mapToInt( (p)  -> { if(p.getTipo().name().equals("consumo") )
				                   return p.getMonto();  
								 else if ( p.getTipo().name().equals("pago") )
									return p.getMonto()*(-1);
								 else if ( p.getTipo().name().equals("comision") )
									return p.getMonto();
								 else {}
								 return 0;
					} )
				//.collect(Collectors.toList())
				.sum();
	}

	@Override
	public List<Product> findByCustomerId(String id) {
		return productRepository.findByCustomerId(id) ;
	}

}
