package com.nttdata.appbanca.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection="customer")
@Data
@AllArgsConstructor
public class Customer {

	@Id
	private String _id;
	
	private CustomerType tipo;
	
	private String name;
	 
}
