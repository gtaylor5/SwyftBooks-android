package com.swyftlabs.swyftbooks;

import android.app.Activity;
import android.os.AsyncTask;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class HomeActivity extends AppCompatActivity {

    private String[] theRetailers = {"VitalSource.com","BookRenter.com","eCampus.com","ValoreBooks.com", "Chegg.com", "AbeBooks.com", "BiggerBooks.com", "Amazon.com"};
    private ArrayList<Book> bookResults = new ArrayList<Book>();
    private String ISBN;
    private EditText isbnText;
    int visibility;
    private TextView appName;
    private ProgressBar progressBar;

    ListAdapter resultsAdapter;
    Book[] bookResultsArray;
    ListView homeScreenListView;
    RelativeLayout bg;
    WebView internet;

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Handler handler = new Handler(){
           @Override
        public void handleMessage(Message msg) {
            
            //set listview adapter
            resultsAdapter = new CustomListAdapter(getApplicationContext(), bookResultsArray);
            homeScreenListView.setAdapter(resultsAdapter);
            homeScreenListView.setDividerHeight(0);
            
            //show list view or not depending on previous visibility
            visibility = homeScreenListView.getVisibility();
            homeScreenListView.setVisibility(View.GONE);
            homeScreenListView.setVisibility(visibility);
            
            //hide progressbar
            progressBar.setVisibility(View.GONE);

               if(bookResultsArray.length==0){
                    
                //check for correct ISBN length
                   if(ISBN.length() != 10 || ISBN.length() != 13){

                               Toast.makeText(getApplicationContext(),"Invalid ISBN number. Please make sure it is 10 or 13 digits.",Toast.LENGTH_LONG).show();

                   }else if(ISBN.length() == 10 || ISBN.length() == 13)
                    //book couldnt be found
                   Toast.makeText(getApplicationContext(),"The book you were looking for could not be found. Please try again.",Toast.LENGTH_LONG).show();

               }
        } //end thread handler
    };



    public void logOut(View view){

        user = mAuth.getCurrentUser();
        if(user != null){
            mAuth.signOut();
            return;
        }
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        };
        
        //Font used for isbnEditText and appName
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        //KeyBoard is always hidden
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //Link variables to assets in UI
        bg = (RelativeLayout)findViewById(R.id.homeBG);
        internet = (WebView)findViewById(R.id.bookBrowser);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        isbnText = (EditText) findViewById(R.id.searchBarEditText);
        homeScreenListView = (ListView)findViewById(R.id.resultsListView);
        TextView logOutTextView = (TextView)findViewById(R.id.logOut);

        //Set Typeface for appName and isbnText and

        isbnText.setTypeface(type2);
        logOutTextView.setTypeface(type);

        
        //set progressbar to invisible
        progressBar.setVisibility(View.INVISIBLE);
        
        //Set Action Listener for Search Icon in searchBar OR Search Icon on keyboard
        isbnText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    homeScreenListView.setAdapter(null);
                    bookResultsArray = null;
                    myClickHandler(getCurrentFocus());
                    
                    return true;
                }
                return false;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    //Main method for work done.
    public void myClickHandler(View view) {
        progressBar.setVisibility(view.VISIBLE); //Show progressBar
        hideSoftKeyboard(this); //Hide keyboard
        //Start new thread

        Runnable r = new Runnable() {
            @Override
            public void run() {
                //set ISBN and remove dashes if any
                ISBN = String.valueOf(isbnText.getText());
                ISBN.replaceAll("-", "");

                synchronized (this) {
                    //check for internet connection
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        
                        //Search for Books.
                        
                        try {
                                for (int i = 0; i < theRetailers.length; i++) {
                                    //Initialize Book
                                    Book temp = new Book();
                                    
                                    //Set book's retailer and build the search url
                                    temp.retailer.retailerName = theRetailers[i];
                                    Log.i("AppInfo", temp.retailer.retailerName);
                                    temp.retailer.buildURL(ISBN);
                                    Log.i("AppInfo", temp.retailer.urlToSearchForBook);
                                    temp.ISBN = ISBN;
                                    
                                    //Get book prices based on retailer name and url
                                    temp = getBookAttrs(temp);
                                    if(temp == null){

                                        continue;

                                    }
                                    
                                    //Set book's title and author using Chegg

                                        temp.bookAuthor = getBookAttributes().bookAuthor;
                                        temp.bookTitle = getBookAttributes().bookTitle;



                                    
                                    //calculate percentage savings
                                    temp.setPercentageSavings();
                                    
                                    //add book only if there is a price. need to fix this logic
                                    if (temp != null){

                                        bookResults.add(temp);

                                    }else {//if retailer has no prices set the book equal to null and garbage collect.
                                            temp = null;
                                            System.gc();
                                        }
                                    }
                        } catch (Exception e) { // catch exception if occurs
                            e.printStackTrace();
                        }
                        
                        //delete excess elements in arraylist
                        bookResults.trimToSize();
                        
                        //sort arraylist based on lowest price(highest to lowest)
                        Collections.sort(bookResults, new Comparator<Book>() {
                            @Override
                            public int compare(Book lhs, Book rhs) {
                                return (int)Math.floor(lhs.lowestPrice - rhs.lowestPrice);
                            }
                        });
                        
                        //set BookResultsArray
                        bookResultsArray = new Book[bookResults.size()];
                        bookResultsArray = bookResults.toArray(bookResultsArray);

                        for(int i = 0; i < bookResultsArray.length; i++){

                            Log.i("AppInfo", bookResultsArray[i].retailer.retailerName);

                        }
                        
                        //clear arrayList and garbage collect
                        bookResults.clear();
                        System.gc();
                    }else{
                        System.out.println("Error");
                    }
                }
                
                handler.sendEmptyMessage(0); //handler for information in thread

            }
        };
        //start thread
          Thread getResults = new Thread(r);
            getResults.start();

    }
    
    //method to hide keyboard    
    public static void hideSoftKeyboard(HomeActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    
    //Everything below this comment should be its own class:
    
    //use chegg to get title and author of book
    public Book getBookAttributes() throws Exception {

        Book tempBook = new Book("Amazon.com");
        tempBook.retailer.buildURL(ISBN);
        tempBook.retailer.XMLFile = new DownloadWebpageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tempBook.retailer.urlToSearchForBook).get();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource input = new InputSource();
        input.setCharacterStream(new StringReader(tempBook.retailer.XMLFile));

        Document xmlDoc = builder.parse(input);

        if (xmlDoc == null) {

            Log.i("AppInfo", "The doc is empty.");

        }

        xmlDoc.getDocumentElement().normalize();

        NodeList BookData = xmlDoc.getElementsByTagName("ItemAttributes");

        tempBook.bookTitle = getTitleAndAuthor(BookData, "Title");
        tempBook.bookAuthor = getTitleAndAuthor(BookData, "Author");

        if(mAuth.getCurrentUser() != null) {
            DatabaseReference ref = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Searches").child(ISBN);
            String timeStamp = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss").format(new Date());
            Map<String, Object> searchQueryUpdate = new HashMap<String, Object>();
            searchQueryUpdate.put("Author", tempBook.bookAuthor);
            searchQueryUpdate.put("Title", tempBook.bookTitle);
            searchQueryUpdate.put("ISBN", ISBN);
            searchQueryUpdate.put("Search Date", timeStamp);
            ref.updateChildren(searchQueryUpdate);
        }

        return tempBook;

    }
    
    //get book attributes generalized across all retailers

    public Book getBookAttrs(Book theBook) throws Exception {
        
        //Special statement for commission junction retailers
        if(theBook.retailer.retailerName == "VitalSource.com" || theBook.retailer.retailerName == "BiggerBooks.com"){

            theBook.retailer.XMLFile = new CommissionJunctionClientUsage().getBookInfo(theBook.retailer.urlToSearchForBook);
            Log.i("AppInfo", theBook.retailer.XMLFile);
            
        }else { //all other retailers

            theBook.retailer.XMLFile = new DownloadWebpageTask().execute(theBook.retailer.urlToSearchForBook).get();
            Log.i("AppInfo", theBook.retailer.XMLFile);

        }
        
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

            theBook.rentPrice_90 = getElement(rental, nintyDay)[0];
            theBook.rentPrice_semester = getElement(rental, semester)[0];
            double[] Prices = getElement(sale, saleElement);
            theBook.usedPrice = Prices[0];
            theBook.newPrice= Prices[1];
            if(buyBack.getLength()!= 0) {
                theBook.buyBackPrice = getElement(buyBack, buyElement)[0];
            }

            theBook.retailer.rentLink = getElementGenInfo(rental,link);
            theBook.retailer.deepLink = getElementGenInfo(sale,link);

            return theBook;

        } else if (theBook.retailer.retailerName == "Chegg.com") {

            NodeList error = xmlDoc.getElementsByTagName("ErrorMessage");

            if (error.getLength() != 0) {

                return null;

            }

            NodeList titleInfo = xmlDoc.getElementsByTagName("BookInfo");
            NodeList rentPrice = xmlDoc.getElementsByTagName("Price");
            String price = rentPrice.item(0).getTextContent();
            theBook.listPrice = getElement(titleInfo, "ListPrice")[0];
            theBook.rentPrice_semester = Double.parseDouble(price);
            NodeList pids = xmlDoc.getElementsByTagName("Pid");
            theBook.cheggPID = pids.item(0).getTextContent();
            theBook.retailer.setCheggDeepLink(theBook.cheggPID);

            return theBook;

        } else if (theBook.retailer.retailerName == "BookRenter.com") {
            NodeList error = xmlDoc.getElementsByTagName("error");
            if (error.getLength() != 0) {
                return null;
            } else {
                NodeList availability = xmlDoc.getElementsByTagName("availability");
                Log.i("AppInfo", availability.item(0).getTextContent());
                if(!(availability.item(0).getTextContent().equalsIgnoreCase("Unavailable"))) {
                    NodeList prices = xmlDoc.getElementsByTagName("rental_price");
                    for (int i = 0; i < prices.getLength(); i++) {
                        if (i == 0) {
                            String stuff = prices.item(i).getTextContent();
                            stuff = stuff.replaceAll("\\$", "");
                            theBook.rentPrice_fall = Double.parseDouble(stuff);
                        } else if (i == 1) {
                            String stuff = prices.item(i).getTextContent();
                            stuff = stuff.replaceAll("\\$", "");
                            theBook.rentPrice_spring = Double.parseDouble(stuff);
                        }
                        String stuff = prices.item(i).getTextContent();
                        stuff = stuff.replaceAll("\\$", "");
                        theBook.rentPrice_summer = Double.parseDouble(stuff);
                    }
                    NodeList url = xmlDoc.getElementsByTagName("book_url");
                    theBook.retailer.deepLink = url.item(0).getTextContent();
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

                            String stuff = Double.toString(getElement(listPrice,"NewPrice")[0]).replaceAll("$","");
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

            return theBook;

        }else if(theBook.retailer.retailerName == "VitalSource.com"){
            try {
                NodeList info = xmlDoc.getElementsByTagName("product");
                NodeList title = xmlDoc.getElementsByTagName("name");
                theBook.bookTitle = title.item(0).getTextContent();
                NodeList url = xmlDoc.getElementsByTagName("buy-url");
                theBook.retailer.deepLink = url.item(0).getTextContent();
                theBook.usedPrice = getElement(info, "price")[0];
                return theBook;
            }catch(Exception e) {

                return null;

            }
        }else if(theBook.retailer.retailerName == "AbeBooks.com"){

                NodeList bookAttrs = xmlDoc.getElementsByTagName("Book");

                if(bookAttrs.getLength() != 0 ) {

                    theBook.newPrice = getElement(bookAttrs, "listingPrice")[0];
                    theBook.retailer.deepLink =  URLEncoder.encode(getElementGenInfo(bookAttrs, "listingUrl"), "UTF-8");
                    theBook.retailer.deepLink = "http://www.dpbolvw.net/click-8044180-5435709?url=http://" + theBook.retailer.deepLink.replaceAll("=","%3D");
                    Log.i("AppInfo",theBook.retailer.deepLink);

                }else {

                    return null;

                }

        }else if(theBook.retailer.retailerName == "BiggerBooks.com"){
            try {
                NodeList info = xmlDoc.getElementsByTagName("product");
                NodeList url = xmlDoc.getElementsByTagName("buy-url");
                theBook.retailer.deepLink = url.item(0).getTextContent();
                theBook.usedPrice = getElement(info, "price")[0];
            }catch(Exception e){

                return null;

            }

        }else if(theBook.retailer.retailerName == "Amazon.com"){

            NodeList itemResponse = xmlDoc.getElementsByTagName("Errors");
            String error = "";
            Log.i("AppInfo",String.valueOf(itemResponse.getLength()));
            if(itemResponse.getLength() == 0){

                NodeList link = xmlDoc.getElementsByTagName("Item");
                theBook.retailer.deepLink = getElementGenInfo(link,"DetailPageURL");
                NodeList listPrice = xmlDoc.getElementsByTagName("ListPrice");
                if(listPrice.getLength() != 0) {
                    theBook.listPrice = new Double(getElement(listPrice, "Amount")[0] / 100);
                }
                NodeList newOffers = xmlDoc.getElementsByTagName("LowestNewPrice");
                if(newOffers.getLength() != 0) {
                    theBook.newPrice = getElement(newOffers, "Amount")[1] / 100;
                }
                NodeList usedOffers = xmlDoc.getElementsByTagName("LowestUsedPrice");
                if(usedOffers.getLength()!=0) {
                    theBook.usedPrice = getElement(usedOffers, "Amount")[1] / 100;
                    Log.i("AppInfo", String.valueOf(theBook.usedPrice));
                }
                NodeList sellBack = xmlDoc.getElementsByTagName("TradeInValue");
                if(sellBack.getLength() != 0) {
                    theBook.buyBackPrice = getElement(sellBack, "Amount")[1] / 100;
                    Log.i("AppInfo", String.valueOf(theBook.buyBackPrice));
                }

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
                Element line = (Element) name.item(0);
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
                    info = String.valueOf(data.getData());
                    System.out.println(info);
                    return info;
                }


            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return info;


        }

        return info;

    }

    public String getTitleAndAuthor(NodeList node, String element){
        Node n = node.item(0);
        Element el = (Element) n;
        return el.getElementsByTagName(element).item(0).getTextContent();
    }

}//end class




