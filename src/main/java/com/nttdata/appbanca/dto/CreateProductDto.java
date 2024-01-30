package com.nttdata.appbanca.dto;

import java.util.List;

import com.nttdata.appbanca.model.ProductType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CreateProductDto {

private String customerId;
	
	private ProductType tipo;
	
	private List<String> titulares;
	 
	private int limite;
	
	private int montoini;
}
