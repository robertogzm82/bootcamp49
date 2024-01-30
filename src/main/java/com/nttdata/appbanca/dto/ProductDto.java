package com.nttdata.appbanca.dto;

import java.util.List;

import com.nttdata.appbanca.model.CustomerType;
import com.nttdata.appbanca.model.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProductDto {

	private String idproducto;
	private Integer saldo;
	
}
