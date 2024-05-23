package com.tax.austria.calculate_purchase.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tax.austria.calculate_purchase.model.PurchaseData;
import com.tax.austria.calculate_purchase.services.TaxService;

@RestController
@RequestMapping("/v1/tax")
public class TaxController {
	
	@Autowired
	TaxService service;
	
	@GetMapping("/austria")
	public String list() {
		return "Hello";
	}
	
	@PostMapping("/austria")
	public ResponseEntity<Object> calculate(@RequestBody PurchaseData purchaseData) {
		//return new ResponseEntity<>((purchaseData), HttpStatus.OK);
		return service.calculateTax(purchaseData);
	}

}
