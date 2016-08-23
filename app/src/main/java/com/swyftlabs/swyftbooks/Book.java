package com.swyftlabs.swyftbooks;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;



public class Book implements Serializable {
    
    //standard book attributes
    String ISBN = "";
    String bookTitle = "something";
    String bookAuthor = "something";
    String cheggPID = "";
    String lowestPriceType = "";
    String seller = "";
    double percentReturn = 0.0;

    ArrayList<Double> buyPrices = new ArrayList<Double>(2);
    ArrayList<Double> rentPrices = new ArrayList<Double>(2);

    double lowestPrice = 0.0;
    double sellPrice = 0.0;
    Retailer retailer = new Retailer();

    public Book(){ // default constructor
    }

    public Book(String retailer){ //additional constructor sets retailer name
        this.retailer.retailerName = retailer;
    }



    void sortPrices(){

        if(this.rentPrices.size() != 0 && this.buyPrices.size() != 0){

            Collections.sort(this.rentPrices);
            Collections.sort(this.buyPrices);
            if(this.rentPrices.get(0) < this.buyPrices.get(0)){

                this.lowestPrice = this.rentPrices.get(0);
                this.lowestPriceType = "Rent";

            }else{

                this.lowestPrice = this.buyPrices.get(0);
                this.lowestPriceType = "Buy";

            }

        }else if(this.rentPrices.size() != 0 && this.buyPrices.size() == 0){

            Collections.sort(this.rentPrices);
            this.lowestPrice = this.rentPrices.get(0);
            this.lowestPriceType = "Rent";

        }else if(this.buyPrices.size() != 0 && this.rentPrices.size() == 0){

            Collections.sort(this.buyPrices);
            this.lowestPrice = this.buyPrices.get(0);
            this.lowestPriceType = "Buy";

        }

        }
}

