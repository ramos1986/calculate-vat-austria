package com.tax.austria.calculate_purchase.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.tax.austria.calculate_purchase.model.PurchaseData;


@Component
public class TaxService {
	// Those values can't change and are the same for every instance.
	static final ArrayList<Integer> availableRates = new ArrayList<>(Arrays.asList(10, 13, 20));
	static final String INVALID_RATE = "Invalid rate (available options: 10, 13, 20).";
	static final String INVALID_INPUT = "Invalid input, you can only fill one field besides the rate: Net, Gross or Tax.";
	static final Integer ROUND_VALUE = 2;
    	
	public ResponseEntity<Object> calculateTax(PurchaseData data) {

        if (!availableRates.contains(data.getRate())) {
            return new ResponseEntity<>(INVALID_RATE, HttpStatus.UNPROCESSABLE_ENTITY); //422 
        }

        Supplier<Stream<Double>> streamValues = () -> Stream.of(data.getGross(), data.getNet(), data.getTax());
		if (streamValues.get().allMatch(x -> x == null)) {
            return new ResponseEntity<>(INVALID_INPUT, HttpStatus.BAD_REQUEST);
		}

		if (streamValues.get().filter(x -> x != null).count() > 1) {
			return new ResponseEntity<>(INVALID_INPUT, HttpStatus.UNPROCESSABLE_ENTITY); //422 
		}
		
		return new ResponseEntity<>(calculateVAT(data), HttpStatus.OK);
	}
	
	private PurchaseData calculateVAT(PurchaseData purchaseData) {
		if (purchaseData.getTax() == null && purchaseData.getNet() == null) {
			purchaseData.setTax(purchaseData.getGross() * purchaseData.getRate()/100);
			purchaseData.setNet(purchaseData.getTax() + purchaseData.getGross());
		} else if (purchaseData.getGross() == null && purchaseData.getNet() == null) {
			purchaseData.setGross((purchaseData.getTax() / purchaseData.getRate())*100);
			purchaseData.setNet(purchaseData.getTax() + purchaseData.getGross());
		} else if (purchaseData.getGross() == null && purchaseData.getTax() == null) {
			Double taxFromGross = Double.valueOf(purchaseData.getRate())/100 
					/ (1 + Double.valueOf(purchaseData.getRate())/100);
			purchaseData.setTax(purchaseData.getNet() * taxFromGross);
			purchaseData.setGross(purchaseData.getNet() - purchaseData.getTax());
		}
		
        formatFields(purchaseData);
		return purchaseData;
	}

    private void formatFields(PurchaseData purchaseData){
        purchaseData.setTax(formatDouble(purchaseData.getTax()));
        purchaseData.setGross(formatDouble(purchaseData.getGross()));
        purchaseData.setNet(formatDouble(purchaseData.getNet()));
    }

    private Double formatDouble(Double val){
        return BigDecimal.valueOf(val).setScale(ROUND_VALUE, RoundingMode.HALF_UP).doubleValue();
    }
	
}
