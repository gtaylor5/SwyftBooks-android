package com.swyftlabs.swyftbooks;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class CustomListAdapter extends ArrayAdapter<Book> {

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
        
        //set retailer image in view.
        switch(retailer){

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
            case("VitalSource.com"):
                retailerLogo.setImageResource(R.drawable.vitalsource);
                break;
            case("AbeBooks.com"):
                retailerLogo.setImageResource(R.drawable.abebookslogoprofile);
                break;
            case("TextBookUnderGround.com"):
                retailerLogo.setImageResource(R.drawable.textbookunderground);
                break;
            case("CengageBrain.com"):
                retailerLogo.setImageResource(R.drawable.cengagebrain);
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
        
        //format percentage savings
        percentageSavings.setText("$ "+String.valueOf(lowestPrice));
        percentageSavings.setTypeface(type);

        return customView; // return view.

    }




}
