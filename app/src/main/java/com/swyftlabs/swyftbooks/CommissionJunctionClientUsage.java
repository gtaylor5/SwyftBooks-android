package com.swyftlabs.swyftbooks;

/**
 * Created by Gerard on 7/5/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.*;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

public class CommissionJunctionClientUsage extends AsyncTask<String, Void, Book>{
    static String xmlfile = "";

    public static Book theBook;
    private final AtomicInteger workCounter;

    public CommissionJunctionClientUsage(AtomicInteger count, Book book){
        this.workCounter = count;
        this.theBook = book;
    }


    @Override
    protected Book doInBackground(String... params) {
        try {
            theBook.retailer.XMLFile = getBookInfo(params[0]);
            try {
                theBook = getBookAttrs(theBook);
                if(theBook.rentPrices.size() != 0){

                    for(int i = 0; i<theBook.rentPrices.size(); i++){

                        System.out.println(theBook.rentPrices.get(i));

                    }

                }

                if(theBook.buyPrices.size() != 0){

                    for(int i = 0; i<theBook.buyPrices.size(); i++){

                        System.out.println(theBook.buyPrices.get(i));

                    }

                }
            }catch (Exception e){

            }
            return theBook;
        }catch(IOException e){

            e.printStackTrace();
            return null;

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Book book) {
        super.onPostExecute(book);

        int tasksLeft = this.workCounter.decrementAndGet();

        if(tasksLeft == 0){
            HomeActivity.done = true;
        }
    }

    public String getBookInfo(String link) throws IOException{

        CommissionJunctionClient.get(link, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                xmlfile = responseString;
                System.out.println(xmlfile);

            }
        });

        return xmlfile;

    }

    public static Book getBookAttrs(Book theBook) throws Exception {


        //build xml document for parsing
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource input = new InputSource();
        input.setCharacterStream(new StringReader(theBook.retailer.XMLFile));
        Document xmlDoc = builder.parse(input);

        //parse specifically based on retailer xml layout
        if (theBook.retailer.retailerName == "ValoreBooks.com") {

            NodeList errorList = xmlDoc.getElementsByTagName("error");
            if(errorList.getLength() != 0){

                return null;

            }

            NodeList rental = xmlDoc.getElementsByTagName("rental-offer");
            String nintyDay = "ninty-day-price";
            String semester = "semester-price";
            String link = "link";

            NodeList sale = xmlDoc.getElementsByTagName("sale-offer");
            String saleElement = "price";

            NodeList buyBack = xmlDoc.getElementsByTagName("buy-offer");
            String buyElement = "item-price";

            theBook.rentPrices.add(getElement(rental, nintyDay)[0]);
            theBook.rentPrices.add(getElement(rental, semester)[0]);
            double[] Prices = getElement(sale, saleElement);
            theBook.buyPrices.add(Prices[0]);
            theBook.buyPrices.add(Prices[1]);
            if(buyBack.getLength()!= 0) {
                theBook.sellPrice = getElement(buyBack, buyElement)[0];
            }

            theBook.retailer.rentLink = getElementGenInfo(rental,link);
            theBook.retailer.deepLink = getElementGenInfo(sale,link);
            theBook.seller = "ValoreBooks";
            theBook.percentReturn = .075;

            return theBook;

        } else if (theBook.retailer.retailerName == "Chegg.com") {

            NodeList error = xmlDoc.getElementsByTagName("ErrorMessage");

            if (error.getLength() != 0) {

                return null;

            }

            NodeList titleInfo = xmlDoc.getElementsByTagName("BookInfo");
            NodeList rentPrice = xmlDoc.getElementsByTagName("Price");
            String price = rentPrice.item(0).getTextContent();
            theBook.rentPrices.add(Double.parseDouble(price));
            NodeList pids = xmlDoc.getElementsByTagName("Pid");
            theBook.cheggPID = pids.item(0).getTextContent();
            theBook.retailer.setCheggDeepLink(theBook.cheggPID);
            theBook.seller = "Chegg";
            theBook.percentReturn = .03;
            return theBook;

        } else if (theBook.retailer.retailerName == "BookRenter.com") {
            NodeList error = xmlDoc.getElementsByTagName("error");
            if (error.getLength() != 0) {
                return null;
            } else {
                NodeList availability = xmlDoc.getElementsByTagName("availability");
                if(!(availability.item(0).getTextContent().equalsIgnoreCase("Unavailable"))) {
                    NodeList prices = xmlDoc.getElementsByTagName("rental_price");
                    for (int i = 0; i < prices.getLength(); i++) {
                        if (i == 0) {
                            String stuff = prices.item(i).getTextContent();
                            stuff = stuff.replaceAll("\\$", "");
                            theBook.rentPrices.add(Double.parseDouble(stuff));
                        } else if (i == 1) {
                            String stuff = prices.item(i).getTextContent();
                            stuff = stuff.replaceAll("\\$", "");
                            theBook.rentPrices.add(Double.parseDouble(stuff));
                        }
                        String stuff = prices.item(i).getTextContent();
                        stuff = stuff.replaceAll("\\$", "");
                        theBook.rentPrices.add(Double.parseDouble(stuff));
                    }
                    NodeList url = xmlDoc.getElementsByTagName("book_url");
                    theBook.retailer.deepLink = url.item(0).getTextContent();
                    theBook.seller = "BookRenter";
                    return theBook;
                }else{

                    return null;

                }

            }


        }else if(theBook.retailer.retailerName == "eCampus.com"){

            String[] priceTypes = {"ListPrice", "UsedPrice", "NewPrice", "MarketPlacePrice", "eBookPrice","RentalPrice", "Rental2Price", "Rental3Price"};
            NodeList listPrice = xmlDoc.getElementsByTagName("item");
            for(int i = 0; i < priceTypes.length; i++){

                if(getElement(listPrice, priceTypes[i]).length != 0){

                    if(i < 5){

                        if(getElement(listPrice, priceTypes[i])[0] == 0.0){

                            continue;

                        }


                        if(i == 2){

                            String stuff = (getElementGenInfo(listPrice,"NewPrice")).replace("$","");
                            theBook.buyPrices.add(Double.parseDouble(stuff));
                            continue;

                        }

                        theBook.buyPrices.add(getElement(listPrice, priceTypes[i])[0]);

                    }else{

                        if(getElement(listPrice, priceTypes[i])[0] == 0.0){

                            continue;

                        }
                        theBook.rentPrices.add(getElement(listPrice, priceTypes[i])[0]);

                    }

                }

            }

            if(theBook.rentPrices.size() == 0 && theBook.buyPrices.size() == 0){

                return null;

            }
            theBook.seller = "eCampus";
            theBook.percentReturn = .08;
            return theBook;

        }else if(theBook.retailer.retailerName == "VitalSource.com"){
            try {
                NodeList info = xmlDoc.getElementsByTagName("product");
                NodeList title = xmlDoc.getElementsByTagName("name");
                theBook.bookTitle = title.item(0).getTextContent();
                NodeList url = xmlDoc.getElementsByTagName("buy-url");
                theBook.retailer.deepLink = url.item(0).getTextContent();
                theBook.buyPrices.add(getElement(info, "price")[0]);
                theBook.seller = "VitalSource";
                theBook.percentReturn = .03;
                return theBook;
            }catch(Exception e) {

                return null;

            }
        }else if(theBook.retailer.retailerName == "AbeBooks.com"){

            NodeList bookAttrs = xmlDoc.getElementsByTagName("Book");

            if(bookAttrs.getLength() != 0 ) {

                theBook.buyPrices.add(getElement(bookAttrs, "listingPrice")[0]);
                theBook.retailer.deepLink =  URLEncoder.encode(getElementGenInfo(bookAttrs, "listingUrl"), "UTF-8");
                theBook.retailer.deepLink = "http://www.dpbolvw.net/click-8044180-5435709?url=http://" + theBook.retailer.deepLink.replaceAll("=","%3D");
                Log.i("AppInfo", theBook.retailer.deepLink);
                theBook.seller = "AbeBooks";
                theBook.percentReturn = .05;

            }else {

                return null;

            }

        }else if(theBook.retailer.retailerName == "BiggerBooks.com"){
            try {
                NodeList info = xmlDoc.getElementsByTagName("product");
                NodeList url = xmlDoc.getElementsByTagName("buy-url");
                theBook.retailer.deepLink = url.item(0).getTextContent();
                theBook.buyPrices.add(getElement(info, "price")[0]);
                theBook.seller = "BiggerBooks";
                theBook.percentReturn = .05;
            }catch(Exception e){

                return null;

            }

        }else if(theBook.retailer.retailerName == "Amazon.com"){

            NodeList itemResponse = xmlDoc.getElementsByTagName("Errors");
            String error = "";
            if(itemResponse.getLength() == 0){

                NodeList link = xmlDoc.getElementsByTagName("Item");
                theBook.retailer.deepLink = getElementGenInfo(link,"DetailPageURL");
                NodeList listPrice = xmlDoc.getElementsByTagName("ListPrice");
                if(listPrice.getLength() != 0) {
                    theBook.buyPrices.add(new Double(getElement(listPrice, "Amount")[0] / 100));
                }
                NodeList newOffers = xmlDoc.getElementsByTagName("LowestNewPrice");
                if(newOffers.getLength() != 0) {
                    theBook.buyPrices.add(getElement(newOffers, "Amount")[0] / 100);
                }
                NodeList usedOffers = xmlDoc.getElementsByTagName("LowestUsedPrice");
                if(usedOffers.getLength()!=0) {
                    theBook.buyPrices.add(getElement(usedOffers, "Amount")[0] / 100);
                }
                NodeList sellBack = xmlDoc.getElementsByTagName("TradeInValue");
                if(sellBack.getLength() != 0) {
                    theBook.sellPrice = getElement(sellBack, "Amount")[0] / 100;
                }

                theBook.seller = "Amazon";
                theBook.percentReturn = .06;

                return theBook;

            }else{

                return null;

            }

        }

        // return book with attributes
        return theBook;
    } //end method

    public static double[] getElement(NodeList node, String Element) {

        double prices;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        double[] priceArray = {0, 0};

        try {

            for (int i = 0; i < node.getLength(); i++) {

                org.w3c.dom.Element element = (org.w3c.dom.Element) node.item(i);
                NodeList name = element.getElementsByTagName(Element);
                org.w3c.dom.Element line = (Element) name.item(0);
                Node child = line.getFirstChild();

                if (child instanceof CharacterData) {

                    CharacterData data = (CharacterData) child;
                    prices = Double.parseDouble(String.valueOf(data.getData()));

                    if (prices > max) {

                        max = prices;

                    }
                    if (prices < min) {

                        min = prices;

                    }

                }


            }

            priceArray[0] = min;
            priceArray[1] = max;
            return priceArray;

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return priceArray;


        }

    }

    public static String getElementGenInfo(NodeList node, String Element) {

        String info = "";

        try {

            for (int i = 0; i < node.getLength(); i++) {

                Element element = (Element) node.item(i);
                NodeList name = element.getElementsByTagName(Element);
                Element line = (Element) name.item(0);
                Node child = line.getFirstChild();

                if (child instanceof CharacterData) {

                    CharacterData data = (CharacterData) child;
                    return info = String.valueOf(data.getData());

                }


            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return info;


        }

        return info;

    }


}
