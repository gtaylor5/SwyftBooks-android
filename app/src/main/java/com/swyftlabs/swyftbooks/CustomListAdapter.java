package com.swyftlabs.swyftbooks;


import android.content.Context;
import android.content.Intent;

import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;



class CustomListAdapter extends ArrayAdapter<Book>{
    public CustomListAdapter(Context context, Book[] books) {
        super(context, R.layout.activity_book_profile2, books);
    }

    String retailer;
    TextView title;
    TextView author;
    TextView seller;
    TextView percentageSavings;
    ImageView retailerLogo;
    Button buy;
    Button sell;
    Button rent;

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

        retailer = String.valueOf(getItem(position).retailer.retailerName);
        title = (TextView)customView.findViewById(R.id.titleViewNormal);
        author = (TextView)customView.findViewById(R.id.authorTextView);
        seller = (TextView)customView.findViewById(R.id.sellerTextView);
        percentageSavings = (TextView)customView.findViewById(R.id.percentageView);
        retailerLogo = (ImageView) customView.findViewById(R.id.retailerLogo);
        buy = (Button)customView.findViewById(R.id.buyButton);
        sell = (Button)customView.findViewById(R.id.sellButton);
        rent = (Button)customView.findViewById(R.id.rentButton);



        sell.setTypeface(type);
        rent.setTypeface(type);
        buy.setTypeface(type);
        
        //set retailer image in view.

        setCell(position);

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

    public void setCell(int position){

        switch(getItem(position).retailer.retailerName){

            case("ValoreBooks.com"):
                retailerLogo.setImageResource(R.drawable.valore);
                break;
            case("Amazon.com"):
                retailerLogo.setImageResource(R.drawable.amznlogo);
                break;
            case("Chegg.com"):
                retailerLogo.setImageResource(R.drawable.chegg);
                break;
            case("BookRenter.com"):
                retailerLogo.setImageResource(R.drawable.bookrenterlogo);
                break;
            case("eCampus.com"):
                retailerLogo.setImageResource(R.drawable.ecampuslogo);
                break;
            case("AbeBooks.com"):
                retailerLogo.setImageResource(R.drawable.abebookslogoprofile);
                break;
            case("VitalSource.com"):
                retailerLogo.setImageResource(R.drawable.vitalsource);
                break;
            case("BiggerBooks.com"):
                retailerLogo.setImageResource(R.drawable.biggerbooks);
                break;

        }
        final int pos = position;
        if(getItem(position).rentPrices.size() == 0){
            rent.setClickable(false);
            rent.setAlpha(.25f);
        }else{

            rent.setClickable(true);
            rent.setText("Rent for: $" + String.format("%.2f", getItem(position).rentPrices.get(0)));
            rent.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                    query.whereEqualTo("name", getItem(pos).retailer.retailerName);

                    try {
                        ParseObject object = query.getFirst();
                        object.increment("timesSelected");
                        object.saveInBackground();

                    } catch (ParseException e) {

                    }
                    ParseObject object = new ParseObject(getItem(pos).seller);
                    object.put("ISBN", getItem(pos).ISBN);
                    object.put("Type", "Rent");
                    object.put("Price", getItem(pos).rentPrices.get(0));
                    object.put("Earnings", getItem(pos).rentPrices.get(0) * .075);
                    object.saveInBackground();
                    object.increment("TimesSearched");
                    object.saveInBackground();
                    ParseAnalytics.trackEvent(getItem(pos).seller);
                    ParseAnalytics.trackEvent("Rent");

                    String link;
                    if(getItem(pos).retailer.rentLink == ""){
                        link = getItem(pos).retailer.deepLink;
                    }else{
                        link = getItem(pos).retailer.rentLink;
                    }

                    Uri url = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);

                }
            });

        }

        if(getItem(position).buyPrices.size() == 0){
            buy.setAlpha(.25f);
            buy.setClickable(false);

        }else{

            buy.setClickable(true);
            buy.setText("Buy for: $" + String.format("%.2f", getItem(position).buyPrices.get(0)));
            buy.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                    query.whereEqualTo("name", getItem(pos).retailer.retailerName);

                    try {
                        ParseObject object = query.getFirst();
                        object.increment("timesSelected");
                        object.saveInBackground();

                    } catch (ParseException e) {

                    }
                    ParseObject object = new ParseObject(getItem(pos).seller);
                    object.put("ISBN", getItem(pos).ISBN);
                    object.put("Type", "Buy");
                    object.put("Price", getItem(pos).buyPrices.get(0));
                    object.put("Earnings", getItem(pos).buyPrices.get(0) * .075);
                    object.saveInBackground();
                    object.increment("TimesSearched");
                    object.saveInBackground();
                    ParseAnalytics.trackEvent(getItem(pos).seller);
                    ParseAnalytics.trackEvent("Buy");

                    String link = getItem(pos).retailer.deepLink;
                    Uri url = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);

                }
            });
        }

        if(getItem(position).sellPrice == 0){
            sell.setAlpha(.25f);
            sell.setClickable(false);

        }else{

            sell.setClickable(true);
            sell.setText("Sell for: $" + String.format("%.2f", getItem(position).sellPrice));
            sell.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Retailer");
                    query.whereEqualTo("name", getItem(pos).retailer.retailerName);

                    try {
                        ParseObject object = query.getFirst();
                        object.increment("timesSelected");
                        object.saveInBackground();

                    } catch (ParseException e) {

                    }
                    ParseObject object = new ParseObject(getItem(pos).seller);
                    object.put("ISBN", getItem(pos).ISBN);
                    object.put("Type", "Sell");
                    object.put("Price", getItem(pos).sellPrice);
                    object.put("Earnings", getItem(pos).sellPrice * .075);
                    object.saveInBackground();
                    object.increment("TimesSearched");
                    object.saveInBackground();
                    ParseAnalytics.trackEvent(getItem(pos).seller);
                    ParseAnalytics.trackEvent("Sell");

                    String link = getItem(pos).retailer.deepLink;
                    Uri url = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);

                }
            });
        }

    }


}
