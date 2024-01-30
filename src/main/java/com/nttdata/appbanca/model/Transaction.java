package com.nttdata.appbanca.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Document(collection="transaction")
@Setter

@Getter

@AllArgsConstructor
public class Transaction {

	@Id
	private String id;
	
	private String productid;
	
	private String customerid;
	
	private TransactionType tipo;
	
	private int monto;
	
	private LocalDateTime date;
}
