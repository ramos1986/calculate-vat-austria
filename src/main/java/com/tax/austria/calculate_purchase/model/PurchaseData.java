package com.tax.austria.calculate_purchase.model;

import lombok.Data;

@Data
public class PurchaseData {
    private Integer rate;
	private Double gross;
	private Double tax;
	private Double net;
}
