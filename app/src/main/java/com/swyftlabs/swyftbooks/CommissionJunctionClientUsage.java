package com.swyftlabs.swyftbooks;

/**
 * Created by Gerard on 7/5/2016.
 */

import android.os.AsyncTask;

import com.loopj.android.http.*;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
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

         if(theBook.retailer.retailerName == "VitalSource.com"){
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
