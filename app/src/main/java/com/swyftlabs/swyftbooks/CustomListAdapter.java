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



class CustomListAdapter extends ArrayAdapter<Book>{
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
                final int pos = position;
                if(getItem(position).buyBackPrice!=0.0) {
                    sell.setText("Sell for: $" + String.format("%.2f", getItem(position).buyBackPrice));
                    sell.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            String link = getItem(pos).retailer.buyBackLink;
                            Log.i("AppInfo", link);
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

                        String link = getItem(pos).retailer.deepLink;
                        Log.i("AppInfo",link);
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });

                break;
            case("Amazon.com"):
                final int pos7 = position;
                if(getItem(position).buyBackPrice != 0.0) {
                    sell.setText("Sell for: $" + String.format("%.2f", getItem(position).buyBackPrice));
                    sell.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            String link = getItem(pos7).retailer.deepLink;
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

                buy.setText("Buy for: $"+String.format("%.2f",getItem(position).usedPrice));
                buy.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String link = getItem(pos7).retailer.deepLink;
                        Log.i("AppInfo",link);
                        Uri uri = Uri.parse(link);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);

                    }

                });

                rent.setClickable(false);
                rent.setAlpha(.25f);


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
