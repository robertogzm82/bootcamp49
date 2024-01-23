package com.nttdata.appbanca.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection="product")
@Data
@AllArgsConstructor
public class Product {

	@Id
	private String _id;
	
	private String _customerId;
	
	private ProductType tipo;
	private List<String> titulares;
	 
}
