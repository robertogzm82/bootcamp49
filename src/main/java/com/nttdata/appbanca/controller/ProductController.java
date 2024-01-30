package com.nttdata.appbanca.controller;

import java.util.function.Predicate;
import java.util.ArrayList;
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

import com.nttdata.appbanca.dto.CreateProductDto;
import com.nttdata.appbanca.dto.ProductDto;
import com.nttdata.appbanca.model.ApiError;
import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.model.PerfilType;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.ProductType;
import com.nttdata.appbanca.model.Transaction;
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
	
	private String messageError="";
	
	@PostMapping("/product")
	public ResponseEntity<?> saveProduct(@RequestBody Product product){
		try {
			//if(Predicate<>productService.ExistsCustomerId(product))
			Predicate<Product> valid = (producto) -> { producto = product;
				                                       return IsValid(producto); };
		    //log.info( valid.test(product) );
			if( valid.test(product) ) {
				Customer customer_product = customerService.getCustomer(product.getCustomerId()).orElseThrow();
				log.info("aqui");
				product.setCustomerId( product.getCustomerId() );
				product.setTitulares(new ArrayList<String>());
				//product.setTransactions(new ArrayList<Transaction>());
				productService.saveProduct(product);
				log.info("Producto guardado");

				customer_product.getProductos().add(product);
				customerService.saveCustomer(customer_product);
				log.info("Cliente actualizado");
				if( productService.findByCustomerId( product.getCustomerId()  )
					.stream()
					.filter( p -> p.getTipo().name().equals("tarjetaCredito"))
					.count()==1
					&& customer_product.getTipo().name().equals("persona")
					&& product.getTipo().name().equals("ahorro")
					) {
					customer_product.setPerfil(PerfilType.VIP);
					customerService.saveCustomer(customer_product);
				}
				if( productService.findByCustomerId( product.getCustomerId()  )
					.stream()
					.filter( p -> p.getTipo().name().equals("cuentaCorriente"))
					.count()==1 &&
					productService.findByCustomerId( product.getCustomerId()  )
					.stream()
					.filter( p -> p.getTipo().name().equals("tarjetaEmpresarial"))
					.count()>=1
					) {
						
				}
				return  new ResponseEntity<Product>(product, HttpStatus.CREATED); 
			}
			else
				return new ResponseEntity<ApiError>( new ApiError(HttpStatus.NOT_FOUND.value()
						                             , messageError )
						                             , HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			//System.out.println(product);
			 e.printStackTrace();
			return new ResponseEntity<String>(  e.fillInStackTrace().toString() , HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	private boolean IsValid(Product producto) {
		if ( // El cliente tipo persona no puede tener : crédito empresarial, tarjeta empresarial
			 isClientePersonaCreditoEmpresarial(producto)	
			 
			 // El cliente tipo empresa no puede tener : crédito personal , tarjeta personal, cuenta de ahorro ni plazofijo
			 && isClienteEnterpriseCreditoEmpresarial(producto)
			 
			 // El cliente tipo persona puede tener solo un credito personal  
			 && isCustomerPersonOneCreditPerson(producto) 
			 
			 // El cliente persona solo puede tener 1 cuenta de ahorro, una cuenta corriento o un plazo fijo
			 && isOneCustomerOneAhorroAndOneCuentaCorriente(producto) 
			 
			 // El cliente persona solo puede tener 1 cuenta de ahorro, una cuenta corriento o un plazo fijo
			 //&& isCustomerEmpresarialhasCuentaAhorrosOPlazoFijo(producto)  
			 )
			return true;
		else {
			return false;
		}
	}


	private boolean isClientePersonaCreditoEmpresarial(Product product) {
		Customer customer = customerService.getCustomer( product.getCustomerId() ).orElseThrow();
		log.info("Cliente validation 1 , begin");
		if( customer.getTipo().name().equals("persona") && product.getTipo().name().equals("creditoEmpresarial") ||
				customer.getTipo().name().equals("persona") && product.getTipo().name().equals("tarjetaEmpresarial")	) {
			log.info("validation 1 , false");
			return false;
		}
		else
			return true;
	}

	private boolean isClienteEnterpriseCreditoEmpresarial(Product product) {
		Customer customer = customerService.getCustomer( product.getCustomerId() ).orElseThrow();
		log.info("Cliente validation 2 , begin");
		if( customer.getTipo().name().equals("empresa") && product.getTipo().name().equals("creditoPersonal") ||
			customer.getTipo().name().equals("empresa") && product.getTipo().name().equals("tarjetaCredito") ||
			customer.getTipo().name().equals("empresa") && product.getTipo().name().equals("ahorro") ||
			customer.getTipo().name().equals("empresa") && product.getTipo().name().equals("plazoFijo") ) {
			messageError="Error intento de ingresar producto no valida a cliente empresa";
			return false;
		}
		else
			return true;
	}
	
	private boolean isCustomerPersonOneCreditPerson(Product product) {
		Customer customer = customerService.getCustomer( product.getCustomerId() ).orElseThrow();
		if( customer.getTipo().name().equals("persona") && product.getTipo().name().equals("creditoPersonal")  ) 
			if( customer.getProductos()
					.stream()
					.filter( p -> p.getTipo().name().equals("creditoPersonal") )
					.count() > 0 ) {
				log.info("validation 3 , false");
				return false;
				}
			else 
				return true;
		else
			return true;	
	}
	
	private boolean isCustomerEmpresarialhasCuentaAhorrosOPlazoFijo(Product producto) {
		if( productService.getCustomerTipo(producto).equals("empresa") 
			&& (  producto.getTipo().name().equals("ahorro") 
			   || producto.getTipo().name().equals("plazoFijo")  )  )	{
			return false;
		} else
			return true;
	}

	                
	private boolean isOneCustomerOneAhorroAndOneCuentaCorriente(Product producto) {
		if ( ( producto.getTipo().name().equals("ahorro") && 
			  	productService.cantProducTipoProducCustomer( producto, ProductType.ahorro  ) == 1 ) || 
		   ( ( producto.getTipo().name().equals("cuentaCorriente") 
				   || producto.getTipo().name().equals("plazoFijo")  ) &&
		    ( productService.cantProducTipoProducCustomer( producto, ProductType.cuentaCorriente ) +
		      productService.cantProducTipoProducCustomer( producto, ProductType.plazoFijo ) == 1  ) ) ) {
			messageError = "${product.validation.isOneCustomerOneAhorroAndOneCuentaCorriente}";
			return false;
		}
		else
			return true;
	}
	


	private boolean isCreditoEmpPerCustomerValid(Product producto) {
		if ( productService.getCustomerTipo(producto).equals("persona") &&
		     producto.getTipo().name().equals("creditoEmpresarial")) {
			messageError = "${product.validation.CustomerInvalid}";
			return false;
		}
		return true;
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
	
	@GetMapping("/product/balance/{idproducto}")
	public ResponseEntity<?> GetBalanceProduct (@PathVariable String idproducto){
		try {
			Product product = productService.getProduct(idproducto).orElseThrow();
			int limit = product.getLimite();
			int expensesTotal = productService.getConsumoTotal(product);
			int saldo =  limit - expensesTotal ; 
			ProductDto response = new ProductDto( idproducto,saldo );
			return  new ResponseEntity<ProductDto>( response , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<ApiError>( new ApiError(HttpStatus.NOT_FOUND.value()
                    , messageError )
                    , HttpStatus.NOT_FOUND);
		}
		
	}
}
