package com.nttdata.appbanca.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

	private int errorcode;
	private String mensaje;
	
}
