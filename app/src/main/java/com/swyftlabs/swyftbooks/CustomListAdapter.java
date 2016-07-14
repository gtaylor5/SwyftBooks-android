package com.swyftlabs.swyftbooks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivity;


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
        ArrayList<Double> nonZeroPrices = new ArrayList<Double>();
        ArrayList<Double> prices = new ArrayList<Double>();
        String deepLink = getItem(position).retailer.deepLink;
        String bookTitle = getItem(position).bookTitle;
        String bookAuthor = getItem(position).bookAuthor;
        double newPrice = getItem(position).newPrice;
        double rentPrice_178 = getItem(position).rentPrice_178;
        double rentPrice_90= getItem(position).rentPrice_90;
        double rentPrice_46 = getItem(position).rentPrice_46;
        double rentPriceSemester = getItem(position).rentPrice_semester;
        double rentPriceSpring = getItem(position).rentPrice_spring;
        double rentPriceFall = getItem(position).rentPrice_fall;
        double rentPriceSummer = getItem(position).rentPrice_summer;
        double usedPrice = getItem(position).usedPrice;
        double marketPrice = getItem(position).marketPlacePrice;
        double listPrice = getItem(position).listPrice;
        double lowestPrice = getItem(position).lowestPrice;

        String retailer = String.valueOf(getItem(position).retailer.retailerName);
        final String purchaseURL = String.valueOf(getItem(position).retailer.deepLink);
        TextView title = (TextView)customView.findViewById(R.id.titleViewNormal);
        TextView author = (TextView)customView.findViewById(R.id.authorTextView);
        TextView seller = (TextView)customView.findViewById(R.id.sellerTextView);
        TextView percentageSavings = (TextView)customView.findViewById(R.id.percentageView);
        ImageView retailerLogo = (ImageView) customView.findViewById(R.id.retailerLogo);
        final Button buy = (Button)customView.findViewById(R.id.buyButton);
        final Button sell = (Button)customView.findViewById(R.id.sellButton);
        final Button rent = (Button)customView.findViewById(R.id.rentButton);
        LinearLayout buttons = (LinearLayout)customView.findViewById(R.id.buttons);



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
                retailerLogo.setImageResource(R.drawable.ecampuslogo);
                rent.setText("Rent for: $"+String.format("%.2f",getItem(position).rentPrice_178));
                final int pos3 = position;
                rent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String link = getItem(pos3).retailer.deepLink;
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
        percentageSavings.setText("$"+String.format("%.2f", lowestPrice));
        percentageSavings.setTypeface(type);

        return customView; // return view.

    }


}
