package com.swyftlabs.swyftbooks;


import android.content.Context;
import android.content.Intent;

import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;



class CustomListAdapter extends ArrayAdapter<Book> {
    static int pos = 0;
    public CustomListAdapter(Context context, Book[] books) {
        super(context, R.layout.activity_book_profile2, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listInflater = LayoutInflater.from(getContext());
        View customView = listInflater.inflate(R.layout.activity_book_profile2, parent, false);
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        Typeface type2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Thin.ttf");
        
        //all of this is really unncecessary. can be handled in book class.
        //useful if i want to implement a book profile activity.
        String bookTitle = getItem(position).bookTitle;
        String bookAuthor = getItem(position).bookAuthor;
        final double lowestPrice = getItem(position).lowestPrice;
        String priceType = getItem(position).lowestPriceType;

        String retailer = String.valueOf(getItem(position).retailer.retailerName);
        TextView title = (TextView)customView.findViewById(R.id.titleViewNormal);
        TextView author = (TextView)customView.findViewById(R.id.authorTextView);
        TextView seller = (TextView)customView.findViewById(R.id.sellerTextView);
        TextView percentageSavings = (TextView)customView.findViewById(R.id.percentageView);
        ImageView retailerLogo = (ImageView) customView.findViewById(R.id.retailerLogo);
        final Button buy = (Button)customView.findViewById(R.id.buyButton);
        final Button sell = (Button)customView.findViewById(R.id.sellButton);
        final Button rent = (Button)customView.findViewById(R.id.rentButton);



        sell.setTypeface(type);
        rent.setTypeface(type);
        buy.setTypeface(type);
        
        //set retailer image in view.
        switch(retailer){

            case("ValoreBooks.com"):
                retailerLogo.setImageResource(R.drawable.valore);
                if(getItem(position).buyBackPrice!=0.0) {
                    sell.setText("Sell for: $" + String.format("%.2f", getItem(position).buyBackPrice));
                    pos = position;
                    sell.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                            query.whereEqualTo("name", "ValoreBooks.com");

                            try {
                                ParseObject object = query.getFirst();
                                object.increment("timesSelected");
                                object.saveInBackground();

                            } catch (ParseException e) {

                            }

                            ParseObject object = new ParseObject("ValoreBooks");
                            object.put("ISBN", getItem(pos).ISBN);
                            object.put("Type", "Sell");
                            object.put("Price", getItem(pos).buyBackPrice);
                            object.put("Earnings", getItem(pos).buyBackPrice * .075);
                            object.saveInBackground();

                                object.increment("TimesSearched");
                                object.saveInBackground();



                            ParseAnalytics.trackEvent("ValoreBooks");
                            ParseAnalytics.trackEvent("Sell");
                            String link = getItem(pos).retailer.buyBackLink;
                            Uri uri = Uri.parse(link);
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);

                        }

                    });
                }else{

                    sell.setClickable(false);
                    sell.setAlpha(.25f);


                }

                rent.setText("Rent for: $"+String.format("%.2f",getItem(position).lowestPrice));
                rent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "ValoreBooks.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }

                        ParseObject object = new ParseObject("ValoreBooks");
                        object.put("ISBN", getItem(pos).ISBN);
                        object.put("Type", "Rent");
                        object.put("Price", getItem(pos).lowestPrice);
                        object.put("Earnings", getItem(pos).lowestPrice * .075);
                        object.saveInBackground();

                        ParseAnalytics.trackEvent("ValoreBooks.com");
                        ParseAnalytics.trackEvent("Rent");
                        String link = getItem(pos).retailer.rentLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });

                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).usedPrice));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "ValoreBooks.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        try {
                            ParseObject object = new ParseObject("ValoreBooks");
                            object.put("ISBN", getItem(pos).ISBN);
                            object.put("Type", "Buy");
                            object.put("Price", getItem(pos).usedPrice);
                            object.put("Earnings", getItem(pos).usedPrice * .075);
                            object.saveInBackground();
                        }catch(Exception e){

                            Log.i("AppInfo", e.toString());

                        }

                        ParseAnalytics.trackEvent("ValoreBooks.com");
                        ParseAnalytics.trackEvent("Buy");
                        String link = getItem(pos).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });

                break;
            case("Amazon.com"):
                retailerLogo.setImageResource(R.drawable.amznlogo);
                break;
            case("Chegg.com"):
                retailerLogo.setImageResource(R.drawable.chegg);
                rent.setText("Rent for: $"+String.format("%.2f",lowestPrice));
                sell.setClickable(false);
                sell.setAlpha(.25f);
                buy.setClickable(false);
                buy.setAlpha(.25f);
                final int pos1 = position;
                rent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "Chegg.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseObject object = new ParseObject("Chegg");
                        object.put("ISBN", getItem(pos1).ISBN);
                        object.put("Type", "Rent");
                        object.put("Price", lowestPrice);
                        object.put("Earnings", lowestPrice * .03);
                        object.saveInBackground();
                        ParseAnalytics.trackEvent("Chegg.com");
                        ParseAnalytics.trackEvent("Rent");
                        String link = getItem(pos1).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });

                break;
            case("BookRenter.com"):
                retailerLogo.setImageResource(R.drawable.bookrenterlogo);
                //only rental possibilities
                rent.setText("Rent for: $"+String.format("%.2f",getItem(position).lowestPrice));
                sell.setClickable(false);
                sell.setAlpha(.25f);
                buy.setClickable(false);
                buy.setAlpha(.25f);
                final int pos2 = position;
                rent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "BookRenter.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseAnalytics.trackEvent("BookRenter.com");
                        ParseAnalytics.trackEvent("Rent");
                        String link = getItem(pos2).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });
                break;
            case("eCampus.com"):
                getItem(position).rentPrices.trimToSize();
                Collections.sort(getItem(position).rentPrices);
                retailerLogo.setImageResource(R.drawable.ecampuslogo);
                final int pos3 = position;
                if(getItem(position).rentPrices.size() != 0) {
                    rent.setText("Rent for: $" + String.format("%.2f", getItem(position).rentPrices.get(0)));
                    rent.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                            query.whereEqualTo("name", "eCampus.com");

                            try {
                                ParseObject object = query.getFirst();
                                object.increment("timesSelected");
                                object.saveInBackground();

                            } catch (ParseException e) {

                            }
                            ParseObject object = new ParseObject("eCampus");
                            object.put("ISBN", getItem(pos3).ISBN);
                            object.put("Type", "Rent");
                            object.put("Price", getItem(pos3).rentPrices.get(0));
                            object.put("Earnings", getItem(pos3).rentPrices.get(0) * .08);
                            object.saveInBackground();
                            ParseAnalytics.trackEvent("eCampus.com");
                            ParseAnalytics.trackEvent("Rent");
                            String link = getItem(pos3).retailer.deepLink;
                            Uri uri = Uri.parse(link);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);

                        }

                    });
                }else {

                    rent.setText("Rent for: $");
                    rent.setClickable(false);
                    rent.setAlpha(.25f);

                }


                getItem(position).buyPrices.trimToSize();
                Collections.sort(getItem(position).buyPrices);
                int i = 0;
                while(i < getItem(position).buyPrices.size()){

                    if(getItem(position).buyPrices.get(i) == 0){
                            i++;
                            continue;
                    }

                    break;

                }
                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).buyPrices.get(i)));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "eCampus.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseObject object = new ParseObject("eCampus");
                        object.put("ISBN", getItem(pos3).ISBN);
                        object.put("Type", "Buy");
                        object.put("Price", getItem(pos3).buyPrices.get(0));
                        object.put("Earnings", getItem(pos3).buyPrices.get(0) * .055);
                        object.saveInBackground();
                        ParseAnalytics.trackEvent("eCampus.com");
                        ParseAnalytics.trackEvent("Buy");
                        String link = getItem(pos3).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });
                sell.setClickable(false);
                sell.setAlpha(.25f);
                break;
            case("VitalSource.com"):
                retailerLogo.setImageResource(R.drawable.vitalsource);
                author.setText("");
                sell.setClickable(false);
                sell.setAlpha(.25f);
                rent.setClickable(false);
                rent.setAlpha(.25f);
                final int pos4 = position;
                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).usedPrice));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "VitalSource.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseObject object = new ParseObject("VitalSource");
                        object.put("ISBN", getItem(pos4).ISBN);
                        object.put("Type", "Buy");
                        object.put("Price", getItem(pos4).usedPrice);
                        object.put("Earnings", getItem(pos4).usedPrice * .03);
                        object.saveInBackground();
                        ParseAnalytics.trackEvent("VitalSource.com");
                        ParseAnalytics.trackEvent("Buy");
                        String link = getItem(pos4).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });
                break;
            case("AbeBooks.com"):
                retailerLogo.setImageResource(R.drawable.abebookslogoprofile);
                sell.setClickable(false);
                sell.setAlpha(.25f);
                rent.setClickable(false);
                rent.setAlpha(.25f);
                final int pos5 = position;
                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).newPrice));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "AbeBooks.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseObject object = new ParseObject("AbeBooks");
                        object.put("ISBN", getItem(pos5).ISBN);
                        object.put("Type", "Buy");
                        object.put("Price", getItem(pos5).newPrice);
                        object.put("Earnings", getItem(pos5).newPrice * .05);
                        object.saveInBackground();
                        ParseAnalytics.trackEvent("AbeBooks.com");
                        ParseAnalytics.trackEvent("Buy");
                        String link = getItem(pos5).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });
                break;
            case("BiggerBooks.com"):
                retailerLogo.setImageResource(R.drawable.biggerbooks);
                author.setText("");
                sell.setClickable(false);
                sell.setAlpha(.25f);
                rent.setClickable(false);
                rent.setAlpha(.25f);
                final int pos6 = position;
                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).usedPrice));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                        query.whereEqualTo("name", "BiggerBooks.com");

                        try {
                            ParseObject object = query.getFirst();
                            object.increment("timesSelected");
                            object.saveInBackground();

                        } catch (ParseException e) {

                        }
                        ParseObject object = new ParseObject("BiggerBooks");
                        object.put("ISBN", getItem(pos6).ISBN);
                        object.put("Type", "Buy");
                        object.put("Price", getItem(pos6).usedPrice);
                        object.put("Earnings", getItem(pos6).usedPrice * .05);
                        object.saveInBackground();
                        ParseAnalytics.trackEvent("BiggerBooks.com");
                        ParseAnalytics.trackEvent("Buy");
                        String link = getItem(pos6).retailer.deepLink;
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });
                break;

        }



        //format title
        title.setText(bookTitle);
        title.setTypeface(type);
        
        //format author
        author.setText(bookAuthor);
        String myString = "<i>" + author.getText() + "</i>";
        author.setText(Html.fromHtml(myString));
        author.setTypeface(type);
        
        //format seller
        seller.setText("Seller: " + getItem(position).retailer.retailerName);
        seller.setTypeface(type2);
        
        //format price
        if(priceType == "Rent"){
            
            percentageSavings.setText("Rent");
            
        }else{
            
            percentageSavings.setText("Buy");
            
        }
        percentageSavings.setTypeface(type);

        return customView; // return view.

    }


}
