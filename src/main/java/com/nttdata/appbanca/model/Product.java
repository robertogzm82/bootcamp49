package com.nttdata.appbanca.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document(collection="product")
@Setter

@Getter

@AllArgsConstructor
public class Product {

	@Id
	private String id;

	private String customerId;
	
	private ProductType tipo;
	
	private List<String> titulares;
	 
	private int limite;
	
	
	//@DBRef
	//private List<Transaction> transactions;
	
}
