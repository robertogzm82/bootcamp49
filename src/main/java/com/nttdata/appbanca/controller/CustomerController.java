package com.nttdata.appbanca.controller;


import java.util.List;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.appbanca.model.ApiError;
import com.nttdata.appbanca.model.Customer;
import com.nttdata.appbanca.service.CustomerService;


@RestController
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/customer")
	public ResponseEntity<?> saveCustomer(@RequestBody Customer customer){
		try {
			Customer customersave = customerService.saveCustomer(customer);
			//System.out.println(customer);
			return  new ResponseEntity<Customer>(customersave, HttpStatus.CREATED); 
		}catch (Exception e) {
			System.out.println(customer);
			return new ResponseEntity<String>(  e.fillInStackTrace().toString() 
					                         , HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
	}
	
	@GetMapping("/customer/{id}")
	public ResponseEntity<?> GetCustomer( @PathVariable(value="id") String id ){
		try {
			return  new ResponseEntity<Customer>(customerService.getCustomer(id).orElseThrow() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<ApiError>( new ApiError( HttpStatus.NOT_FOUND.value() 
					                                           , e.fillInStackTrace().toString())
                                                 , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/customer")
	public ResponseEntity<?> GetAllCustomer(){
		try {
			return  new ResponseEntity<List<Customer>>(customerService.getAllCustomer() , HttpStatus.OK); 
		}catch (Exception e) {
			return new ResponseEntity<String>( e.fillInStackTrace().toString() 
                    , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
