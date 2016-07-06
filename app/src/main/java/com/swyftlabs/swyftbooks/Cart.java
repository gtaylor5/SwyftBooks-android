package com.swyftlabs.swyftbooks;

public class Cart {
	
	int cartSize = 0;
	Book[] booksToPurchase;
	double totalCost = 0;
	double shippingCost;
	double taxes;
	
	public void calculateTotalCost(){
		
		for(int i = 0; i < this.booksToPurchase.length; i++){
			
			//this.totalCost += this.booksToPurchase[i].usedPrice;
			//this.shippingCost += this.booksToPurchase[i].shippingCost;
			//this.taxes += this.booksToPurchase[i].taxes;
			
		}
		
	}
	

}
