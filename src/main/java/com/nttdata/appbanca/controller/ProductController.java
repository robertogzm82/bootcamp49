package com.nttdata.appbanca.controller;

import java.util.function.Predicate;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.appbanca.model.ApiError;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.ProductType;
import com.nttdata.appbanca.service.CustomerService;
import com.nttdata.appbanca.service.ProductService;

import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Value("product.validation.CustomerInvalid")
	private String messageCustomerInvalid; 
	
	@Value("product.validation.CreditoEmpPerCustomerValid")
	private String messageCreditoEmpPerCustomerValid; 
	
	@Value("product.validation.CreditPerCustomerValid")
	private String messageCreditPerCustomerValid; 

	
	private String messageError="";
	
	@PostMapping("/product")
	public ResponseEntity<?> saveProduct(@RequestBody Product product){
		try {
			//if(Predicate<>productService.ExistsCustomerId(product))
			Predicate<Product> valid = (producto) -> { producto = product;
				                                       return IsValid(producto); };
			if( valid.test(product) ) {
				Product productsave = productService.saveProduct(product);
				return  new ResponseEntity<Product>(productsave, HttpStatus.CREATED); 
			}
			else
				return new ResponseEntity<ApiError>( new ApiError(HttpStatus.NOT_FOUND.value()
						                             ,  "${product.validation.CustomerInvalid}" )
						                             , HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			//System.out.println(product);
			 e.printStackTrace();
			return new ResponseEntity<String>(  e.fillInStackTrace().toString() , HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	private boolean IsValid(Product producto) {
		if ( // El _customerid debe existir 
			 isCustomerIdValid(producto) 
			 
			 // El cliente tipo persona no puede tener crédito empresarial
			 && isCreditoEmpPerCustomerValid(producto) 
			 
			 // Un cliente tipo persona solo debe tener un credito de tipo personal(producto)
			 //&& isCreditPerCustomerValid(producto)       
			 
			 // El cliente persona solo puede tener 1 cuenta de ahorro, una cuenta corriento o un plazo fijo
			 //&& isOneCustomerOneAhorroAndOneCuentaCorriente(producto) && 
			 
			 //Si el cliente es empresarial no puede tener cuenta de ahorro ni plazo fijo
			 //&& IsCustomerEmpresarialhasCuentaAhorrosOPlazoFijo(producto)  
			 )
			return true;
		else {
			return false;
		}
	}


	private boolean IsCustomerEmpresarialhasCuentaAhorrosOPlazoFijo(Product producto) {
		if( productService.getCustomerTipo(producto).equals("empresa") 
			&& (  producto.getTipo().name().equals("ahorro") 
			   || producto.getTipo().name().equals("plazoFijo")  )  )	{
			return false;
		} else
			return true;
	}

	private boolean isOneCustomerOneAhorroAndOneCuentaCorriente(Product producto) {
		if( ( productService.countTipoProducto(producto,ProductType.ahorro.toString()) == 1  
				&& producto.getTipo().name().equals("ahorro"))
		  || ( productService.countTipoProducto(producto, ProductType.cuentaCorriente.toString())==1  
		      && producto.getTipo().name().equals("cuentaCorriente")) 
		  || ( productService.countTipoProducto(producto, ProductType.plazoFijo.toString())==1  
	      && producto.getTipo().name().equals("plazoFijo")) )
			return false;
		else
			return true;
	}

	private boolean isCreditPerCustomerValid(Product producto) {
		if( productService.countTipoProducto(producto,ProductType.creditoPersonal.toString()) == 1 && 
			producto.getTipo().name().equals("creditoPersonal")	) {
			messageError = messageCreditPerCustomerValid;
			return false;
		}
		else {
			return true;
		}
	}

	private boolean isCreditoEmpPerCustomerValid(Product producto) {
		if ( productService.getCustomerTipo(producto).equals("persona") &&
		     producto.getTipo().name().equals("creditoEmpresarial")) {
			messageError = messageCustomerInvalid;
			return false;
		}
		return true;
	}

	private boolean isCustomerIdValid(Product producto) {
		if( customerService.getCustomer( producto.get_customerId() ).isPresent())
			return true;
		else {
			messageError = messageCreditoEmpPerCustomerValid;
			return false;
		}
	}

	@GetMapping("/product/{id}")
	public ResponseEntity<?> GetProduct( @PathVariable(value="id") String id ){
		try {
			return  new ResponseEntity<Product>(productService.getProduct(id).orElseThrow() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<ApiError>( new ApiError( HttpStatus.NOT_FOUND.value() 
					                                           , e.fillInStackTrace().toString())
                                                 , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/product")
	public ResponseEntity<?> GetAllCustomer(){
		try {
			return  new ResponseEntity<List<Product>>(productService.getAllProduct() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<String>( e.fillInStackTrace().toString() 
                    , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
