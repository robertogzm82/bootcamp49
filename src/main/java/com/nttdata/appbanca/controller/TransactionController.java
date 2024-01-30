package com.nttdata.appbanca.controller;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.appbanca.model.ApiError;
import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.Transaction;
import com.nttdata.appbanca.service.CustomerService;
import com.nttdata.appbanca.service.ProductService;
import com.nttdata.appbanca.service.TransactionService;

import lombok.extern.log4j.Log4j2;


@RestController
@Log4j2
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ProductService productService;
	
	private String messageError="";
	
	@PostMapping("/transaction")
	public ResponseEntity<?> saveTransaction(@RequestBody Transaction transaction){
		try {
			if( IsValid(transaction) 
					) {
				//Product product_save = productService.getProduct(transaction.getProductid()).orElseThrow();
				String customer_id = productService.getProduct(transaction.getProductid()).orElseThrow().getCustomerId();
				transaction.setCustomerid(customer_id);
				//product_save.getTransactions().add(transaction);
				return  new ResponseEntity<Transaction>( transactionService.saveTransaction(transaction)
						                                , HttpStatus.CREATED ); 
			}
			return new ResponseEntity<ApiError>( new ApiError( HttpStatus.NOT_FOUND.value()
                    , messageError )
                    , HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(transaction);
			e.printStackTrace();
			return new ResponseEntity<String>(  e.fillInStackTrace().toString() , HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	public boolean IsValid(Transaction transaction) {
		if ( // Comisiones y Pagos son transacciones que se usan con los productos: 
			 // creditos personales, empresariales y tarjetas de credito
			 
		    isCargoConsumoTarjetaCreditoLimitValid(transaction)
			 
			 // El cliente puede cargar una transaction retiro o deposito para un cliente con plazo fijo  
			 // (Falta)
			 //&& isPersonaCargaConsumoValid(transaction) 
			 // Cliente persona con producto PlazoFijo solo permite una transaccion sea retiro 
			 // o deposito el dia configurado del mes
			 //&& isClienteUnRetiroDepositoDiaconfigurado(transaction)
			 ) {
			log.info("Transaccion válida");
			return true;
		}
		else {
			log.info("Transaccion inválida");
			return false;
		}
	}
	
	/*
	private boolean isClienteUnRetiroDepositoDiaconfigurado(Transaction transaction) {
		productService.getProduct( t.get_productId() ).get().getTipo().name().equals("")
		Calendar cal = Calendar.getInstance();
		Date dateinicio = new Date();
		dateinicio = GetDatefirstDayofMonth();
		
		
		Predicate<Transaction> v1 = t -> productService.getProduct( t.get_productId() ).get().equals("plazoFijo");
		Predicate<Integer> v2 = t -> "${transaction.validation.plazofijo.dia}" == Integer.valueOf(t);
		Function<Transaction,Integer> v3 = transaccion ->  transactionService.getByCustomer(  
				customerService.getCustomer(transaccion.get_customerId() ) )
				.stream()
				.filter(t ->    t.get_productId() ) ;

		if( v1.test(transaction) && v2.test(cal.get(Calendar.DAY_OF_MONTH) &&   )
			return true;
		else	
			return false;
	}
	*/
	
	private int getDeuda(Transaction transaction) {
		log.info(transactionService.findByProductId( transaction.getProductid()));
		return transactionService.findByProductId( transaction.getProductid())
				.stream()
				.mapToInt( t -> { if( t.getTipo().name().equals("consumo") )
				                    return t.getMonto();
								  else {}
				                  if ( t.getTipo().name().equals("pago") )
				            	    return t.getMonto()*(-1);
				                  else {} 
				                  if ( t.getTipo().name().equals("comision")  )
				                	return t.getMonto();  
				                  else return  0;
				} )
				.peek(t -> System.out.println(t)) 
				.sum();
	}
	
	private boolean isCargoConsumoTarjetaCreditoLimitValid(Transaction transaction) {
		int limite = productService.getProduct(transaction.getProductid()).orElseThrow().getLimite();
		int total_deuda = getDeuda(transaction);
		if (  transaction.getTipo().name().equals("consumo") 
			  && total_deuda + transaction.getMonto() >= limite ) {
			messageError = "${'transaction.validation.isCargoConsumoTarjetaCreditoLimitValid'}";
			return false;
		}
		else {
			return true;
		}
	}

	@SuppressWarnings("deprecation")
	private Date GetDatefirstDayofMonth() {
		int mes = LocalDateTime.now().getMonthValue();
		int año = LocalDateTime.now().getYear();
		return new Date(año , mes ,1);	
	}

	@SuppressWarnings("deprecation")
	private Date GetDateLasttDayofMonth() {
		int mes = LocalDateTime.now().getMonthValue();
		int año = LocalDateTime.now().getYear();
		Calendar cal = Calendar.getInstance();
	    int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    return new Date(año, mes ,day);
	}
	
	private boolean isPagoCommisionCreditoValid(Transaction transaction) {
		String tipoproducto =  productService.getProduct(transaction.getProductid()).orElseThrow().getTipo().name();
		//String tipoproducto = productService.getProduct(transaction.get_productId() ).get().getTipo().name();
		if( ( tipoproducto.equals("ahorro") || tipoproducto.equals("cuentaCorriente")
			|| tipoproducto.equals("plazoFijo") )
			&& ( transaction.getTipo().equals("deposito") || transaction.getTipo().equals("retiro") ) ) 
			return true;
		else 
			return false;
	}

	/*
	private boolean isDepositoRetiroCuentaValid(Transaction transaction) {
		Product producto = productService.getProduct(transaction.getProductid()).orElseThrow();
		String tipoproducto =  producto.getTipo().name();
		String tipoCliente = customerService.getCustomer(transaction.getCustomerid()).orElseThrow().getTipo().name();
		//String tipoproducto = productService.getProduct(transaction.get_productId() ).get().getTipo().name();
		if( ( tipoproducto.equals("ahorro") || tipoproducto.equals("cuentaCorriente")
			|| tipoproducto.equals("plazoFijo") || tipoproducto.equals("tarjetaEmpresarial")  )
			&& ( transaction.getTipo().equals("pago") || transaction.getTipo().equals("comision") ) ) 
			return true;
		else 
			return false;
	}
	*/
	
	@GetMapping("/transaction/{id}")
	public ResponseEntity<?> GetTransaction( @PathVariable(value="id") String id ){
		try {
			return  new ResponseEntity<Transaction>(transactionService.getTransaction(id).orElseThrow() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<ApiError>( new ApiError( HttpStatus.NOT_FOUND.value() 
					                                           , e.fillInStackTrace().toString())
                                                 , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/transaction/product/{id}")
	public ResponseEntity<?> GetTransactiomByProduct( @PathVariable(value="id") String id ){
		try {
			return  new ResponseEntity<List<Transaction>>( transactionService.findByProductId(id) , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<ApiError>( new ApiError( HttpStatus.NOT_FOUND.value() 
					                                           , e.fillInStackTrace().toString())
                                                 , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/transaction")
	public ResponseEntity<?> GetAllTransaction(){
		try {
			return  new ResponseEntity<List<Transaction>>( transactionService.getAllTransaction() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<String>( e.fillInStackTrace().toString() 
                    , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
