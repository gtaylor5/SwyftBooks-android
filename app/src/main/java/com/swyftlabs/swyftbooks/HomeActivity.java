package com.swyftlabs.swyftbooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.lang.Runtime.*;


public class HomeActivity extends AppCompatActivity {

    private String[] theRetailers = {"BookRenter.com","eCampus.com","ValoreBooks.com", "Chegg.com", "VitalSource.com"};
    int visibility;
    ListAdapter resultsAdapter;
    Book[] bookResultsArray;
    private ArrayList<Book> bookResults = new ArrayList<Book>();
    private String ISBN;
    private EditText isbnText;
    ListView homeScreenListView;
    private TextView appName;
    private ProgressBar progressBar;
    RelativeLayout bg;
    WebView internet;



    Handler handler = new Handler(){
           @Override
        public void handleMessage(Message msg) {

            resultsAdapter = new CustomListAdapter(getApplicationContext(), bookResultsArray);
            homeScreenListView.setAdapter(resultsAdapter);
            homeScreenListView.setDividerHeight(0);

            visibility = homeScreenListView.getVisibility();
            homeScreenListView.setVisibility(View.GONE);
            homeScreenListView.setVisibility(visibility);
               progressBar.setVisibility(View.GONE);

               if(bookResultsArray.length==0){

                   if(ISBN.length() != 10 || ISBN.length() != 13){

                               Toast.makeText(getApplicationContext(),"Invalid ISBN number. Please make sure it is 10 or 13 digits.",Toast.LENGTH_LONG).show();

                   }else if(ISBN.length() == 10 || ISBN.length() == 13)

                   Toast.makeText(getApplicationContext(),"The book you were looking for could not be found. Please try again.",Toast.LENGTH_LONG).show();

               }

            homeScreenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    progressBar.setVisibility(View.VISIBLE);

                    Log.i("AppInfo", String.valueOf(position));
                    String link = bookResultsArray[position].retailer.deepLink;
                    Uri uri = Uri.parse(link);
                    internet.setWebViewClient(new WebViewClient(){


                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);

                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                    internet.loadUrl(uri.toString());
                    internet.getSettings().setJavaScriptEnabled(true);
                    internet.setVisibility(View.VISIBLE);
                    //Intent intent = new Intent(internet, uri);
                    //startActivity(intent);

                }

            });


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Thin.ttf");
        Typeface type2 = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bg = (RelativeLayout)findViewById(R.id.homeBG);
        internet = (WebView)findViewById(R.id.bookBrowser);
        appName = (TextView)findViewById(R.id.logo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        isbnText = (EditText) findViewById(R.id.searchBarEditText);
        String myString = "<i>" + "Swyft" + "</i>" + "Books";
        appName.setText(Html.fromHtml(myString));
        appName.setTypeface(type2);
        isbnText.setTypeface(type2);
        homeScreenListView = (ListView) findViewById(R.id.resultsListView);
        progressBar.setVisibility(View.INVISIBLE);
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

    }


    public void myClickHandler(View view) {
        progressBar.setVisibility(view.VISIBLE);
        hideSoftKeyboard(this);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ISBN = String.valueOf(isbnText.getText());
                ISBN.replaceAll("-", "");

                synchronized (this) {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();



                    if (networkInfo != null && networkInfo.isConnected()) {
                        try {
                                for (int i = 0; i < theRetailers.length; i++) {

                                    Book temp = new Book();
                                    temp.retailer.retailerName = theRetailers[i];
                                    Log.i("AppInfo", theRetailers[i]);
                                    temp.retailer.buildURL(ISBN);
                                    temp = getBookAttrs(temp);
                                    temp.bookAuthor = getBookAttributes().bookAuthor;
                                    temp.bookTitle = getBookAttributes().bookTitle;
                                    temp.setPercentageSavings();
                                    if (temp.percentageSavings != 0 || temp.retailer.retailerName == "VitalSournce.com") {
                                        bookResults.add(temp);
                                    } else if(temp.retailer.retailerName == "VitalSource.com") {

                                        Log.i("AppInfo",temp.retailer.retailerName);
                                        bookResults.add(temp);

                                    }else {
                                            temp = null;
                                            System.gc();
                                        }
                                    }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bookResults.trimToSize();
                        Collections.sort(bookResults, new Comparator<Book>() {
                            @Override
                            public int compare(Book lhs, Book rhs) {
                                return (int)Math.floor(rhs.percentageSavings - lhs.percentageSavings);
                            }
                        });

                        bookResultsArray = new Book[bookResults.size()];
                        bookResultsArray = bookResults.toArray(bookResultsArray);

                        bookResults.clear();
                        System.gc();
                    }else{
                        System.out.println("Error");
                    }
                }
                handler.sendEmptyMessage(0);

            }
        };
                Thread getResults = new Thread(r);
                getResults.start();

    }

    public static void hideSoftKeyboard(HomeActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public Book getBookAttributes() throws Exception {

        Book tempBook = new Book("Chegg.com");
        tempBook.retailer.buildURL(ISBN);
        tempBook.retailer.XMLFile = new DownloadWebpageTask().execute(tempBook.retailer.urlToSearchForBook).get();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource input = new InputSource();
        input.setCharacterStream(new StringReader(tempBook.retailer.XMLFile));

        Document xmlDoc = builder.parse(input);

        if (xmlDoc == null) {

            Log.i("AppInfo", "The doc is empty.");

        }

        NodeList BookData = xmlDoc.getElementsByTagName("BookInfo");
        String bookTitle = "Title";

        NodeList authorInfo = xmlDoc.getElementsByTagName("Authors");
        String author = "Author";

        tempBook.bookTitle = getElementGenInfo(BookData, bookTitle);
        tempBook.bookAuthor = getElementGenInfo(authorInfo, author);

        return tempBook;

    }

    public Book getBookAttrs(Book theBook) throws Exception {

        if(theBook.retailer.retailerName == "VitalSource.com"){

            theBook.retailer.XMLFile = new CommissionJunctionClientUsage().getBookInfo(theBook.retailer.urlToSearchForBook);
            Log.i("AppInfo",theBook.retailer.XMLFile);

        }else {

            theBook.retailer.XMLFile = new DownloadWebpageTask().execute(theBook.retailer.urlToSearchForBook).get();

        }

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource input = new InputSource();
        input.setCharacterStream(new StringReader(theBook.retailer.XMLFile));

        Document xmlDoc = builder.parse(input);

        if (theBook.retailer.retailerName == "ValoreBooks.com") {

            NodeList rental = xmlDoc.getElementsByTagName("rental-offer");
            String nintyDay = "ninty-day-price";
            String semester = "semester-price";

            NodeList sale = xmlDoc.getElementsByTagName("sale-offer");
            String saleElement = "price";

            NodeList buyBack = xmlDoc.getElementsByTagName("buy-offer");
            String buyElement = "item-price";

            theBook.rentPrice_90 = getElement(rental, nintyDay)[0];
            theBook.rentPrice_semester = getElement(rental, semester)[0];
            double[] Prices = getElement(sale, saleElement);
            theBook.usedPrice = Prices[0];
            theBook.newPrice= Prices[1];
            theBook.buyBackPrice = getElement(buyBack, buyElement)[0];

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
            Log.i("AppInfo", theBook.retailer.deepLink);


        } else if (theBook.retailer.retailerName == "BookRenter.com") {

            NodeList error = xmlDoc.getElementsByTagName("error");
            if (error.getLength() != 0) {

                return null;

            } else {

                NodeList availability = xmlDoc.getElementsByTagName("availability");
                Log.i("AppInfo", availability.item(0).getTextContent());
                if(availability.getLength()!= 0) {
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

                    Log.i("AppInfo", theBook.retailer.deepLink);
                }else{

                    Log.i("AppInfo", "This might be the problem");
                    return null;

                }

            }


        }else if(theBook.retailer.retailerName == "eCampus.com"){

            NodeList listPrice = xmlDoc.getElementsByTagName("item");
            theBook.listPrice = getElement(listPrice, "ListPrice")[0];
            theBook.usedPrice = getElement(listPrice, "UsedPrice")[0];
            String stuff = Double.toString(getElement(listPrice,"NewPrice")[0]).replaceAll("\\$","");
            theBook.newPrice = Double.parseDouble(stuff);
            theBook.marketPlacePrice = getElement(listPrice, "MarketPlacePrice")[0];
            theBook.eBookPrice = getElement(listPrice, "eBookPrice")[0];
            theBook.rentPrice_178 = getElement(listPrice, "RentalPrice")[0];
            theBook.rentPrice_90 = getElement(listPrice, "Rental2Price")[0];
            theBook.rentPrice_46 = getElement(listPrice, "Rental3Price")[0];

            NodeList eBookPrice = xmlDoc.getElementsByTagName("eBookPrice");

        }else if(theBook.retailer.retailerName == "VitalSource.com"){

            NodeList info = xmlDoc.getElementsByTagName("product");
            NodeList url = xmlDoc.getElementsByTagName("buy-url");
            theBook.retailer.deepLink = url.item(0).getTextContent();
            theBook.usedPrice = getElement(info, "price")[0];

        }

        return theBook;


    }



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




