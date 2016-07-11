package com.swyftlabs.swyftbooks;

import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Book implements Serializable {
    
    //standard book attributes
    String ISBN = "";
    String bookTitle = "something";
    String bookAuthor = "something";
    String bookEdition = "";
    String bookPubDate = "";
    String urlToPurchase = "";
    String condition = "";
    String cheggPID = "";

    //unnecessary --> XML file is already in retailer class
    String BuyXMLFile = "stuff";

    //purchase prices
    double listPrice = 0.0;
    double newPrice = 0.0;
    double usedPrice = 0.0;
    double marketPlacePrice = 0.0;
    double eBookPrice = 0.0;
    double buyBackPrice = 0.0;

    //rental prices
    double rentPrice_fall = 0.0;
    double rentPrice_spring = 0.0;
    double rentPrice_summer = 0.0;
    double rentPrice_semester = 0.0;
    double rentPrice_178 = 0.0;
    double rentPrice_90 = 0.0;
    double rentPrice_46 = 0.0;

    double percentageSavings = -1.0;
    double lowestPrice = 0.0;
    
    Retailer retailer = new Retailer();

    public Book(){ // default constructor
    }

    public Book(String retailer){ //additional constructor sets retailer name
        this.retailer.retailerName = retailer;
    }

    //set percentagesavings value
    public void setPercentageSavings(){

        ArrayList<Double> nonZeroPrices = new ArrayList<Double>();
        ArrayList<Double> prices = new ArrayList<Double>();
        
        //add all possible prices to prices array list and trim
        prices.add(rentPrice_46);
        prices.add(rentPrice_90);
        prices.add(rentPrice_178);
        prices.add(rentPrice_fall);
        prices.add(rentPrice_semester);
        prices.add(rentPrice_summer);
        prices.add(usedPrice);
        prices.add(marketPlacePrice);
        prices.add(listPrice);
        prices.add(newPrice);
        prices.add(rentPrice_spring);
        prices.trimToSize();
        
        //fill non-zero prices array
        for(int j = 0; j < prices.size(); j++){
            if(prices.get(j) != 0){
                nonZeroPrices.add(prices.get(j));
            }
        }
        
        //trim non-zeroprices array
        nonZeroPrices.trimToSize();
        
        //if there are no prices return null
        if(nonZeroPrices.size() == 0){
            return;
        }
        
        //get max and min prices
        double max = Collections.max(nonZeroPrices, null);
        double min = Collections.min(nonZeroPrices, null);
        
        //calculate percentage savings and clear arraylists
        percentageSavings = Math.abs((max-min)/max)*100;
        lowestPrice = min;
        nonZeroPrices.clear();
        prices.clear();
    }
}

