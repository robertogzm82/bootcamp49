package com.nttdata.appbanca.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nttdata.appbanca.model.Product;
import com.nttdata.appbanca.model.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

	//@Query("{'productid = ?0'}")
	List<Transaction> findByProductid(String productId);
	
	//@Query("{'product.id = ?0' ,'date':{ $lt: ?1, $gt: ?2} }")
	//List<Transaction> findByProductDatesBetween(String productId, Date startDate, Date endDate);
	
	
}
