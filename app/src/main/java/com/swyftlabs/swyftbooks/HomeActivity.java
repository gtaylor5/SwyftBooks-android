package com.swyftlabs.swyftbooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class HomeActivity extends AppCompatActivity {

    private String[] theRetailers = {"VitalSource.com","BookRenter.com","eCampus.com","ValoreBooks.com", "Chegg.com", "AbeBooks.com", "BiggerBooks.com", "Amazon.com"};
    int visibility;
    ListAdapter resultsAdapter;
    Book[] bookResultsArray;
    public static ArrayList<Book> bookResults = new ArrayList<Book>();
    private String ISBN;
    private EditText isbnText;
    ListView homeScreenListView;
    private ProgressBar progressBar;
    RelativeLayout bg;
    WebView internet;

    static String title = "";
    static String author = "";

    static AtomicInteger count;
    public static boolean done = false;


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

        ParseUser user = ParseUser.getCurrentUser();
        user.logOut();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ParseAnalytics.trackAppOpenedInBackground(this.getIntent());

        
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
                    ParseAnalytics.trackEvent("search");
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
        bookResults.clear();

        ISBN = String.valueOf(isbnText.getText());
        ISBN = ISBN.replaceAll("-", "");

        Runnable r = new Runnable() {
            @Override
            public void run() {


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        count = new AtomicInteger(8);

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
             getTitleAndAuthor();
                trackSearch(ISBN);
            }catch(Exception e){

            }
            done = false;
            for(int i = 0; i < theRetailers.length; i++){

                Book temp = new Book(theRetailers[i]);
                temp.retailer.buildURL(ISBN);
                temp.ISBN = ISBN;

                try {

                    if(temp.retailer.retailerName == "VitalSource.com"){
                        continue;
                    }
                    temp.bookTitle = title;
                    temp.bookAuthor = author;
                    System.out.println(i +" : " + temp.retailer.urlToSearchForBook);
                    DownloadWebpageTask task = new DownloadWebpageTask(count, temp);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        bookResults.add((task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, temp.retailer.urlToSearchForBook).get()));
                    else
                        bookResults.add(task.execute(temp.retailer.urlToSearchForBook).get());


                }catch(Exception e) {
                    continue;
                }
            }
            while(done != true){
                continue;
            }
            bookResults.trimToSize();
            removeNullAndSetLowestPrices();
            Collections.sort(bookResults, new Comparator<Book>() {
                @Override
                public int compare(Book lhs, Book rhs) {
                    return (int)Math.floor(lhs.lowestPrice - rhs.lowestPrice);
                }
            });
            bookResultsArray = new Book[bookResults.size()];
            bookResultsArray = bookResults.toArray(bookResultsArray);
            bookResults.clear();
            System.gc();

        }
                handler.sendEmptyMessage(0);

            }
        };

        Thread t = new Thread(r);
        t.start();

    }

    static void removeNullAndSetLowestPrices(){
        for(int i = 0; i < bookResults.size(); i++) {
            if (bookResults.get(i) == null) {
                bookResults.remove(i);
            }
            bookResults.get(i).sortPrices();
        }
    }

    static void trackSearch(String ISBN){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ISBN");
        query.whereEqualTo("ISBNumber", ISBN);

        try {
            ParseObject object = query.getFirst();
            object.increment("TimesSearched");
            object.saveInBackground();

        } catch (ParseException e) {
            e.printStackTrace();
            ParseObject object = new ParseObject("ISBN");
            object.put("ISBNumber", ISBN);
            object.put("TimesSearched", 1);
            try {

                object.put("title", title);
                object.save();

            }catch(Exception ex){

                ex.printStackTrace();

            }
        }

    }

    //method to hide keyboard    
    public static void hideSoftKeyboard(HomeActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void getTitleAndAuthor() throws Exception, ExecutionException {

        Book tempBook = new Book("Amazon.com");
        tempBook.retailer.buildURL(ISBN);
        tempBook.retailer.XMLFile = new DownloadWebpageTask(new AtomicInteger(1), tempBook).execute(tempBook.retailer.urlToSearchForBook).get().retailer.XMLFile;

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource input = new InputSource();
        input.setCharacterStream(new StringReader(tempBook.retailer.XMLFile));

        Document xmlDoc = builder.parse(input);

        if (xmlDoc == null) {
            return;
        }
        NodeList BookData = xmlDoc.getElementsByTagName("Title");
        NodeList author = xmlDoc.getElementsByTagName("Author");
        if(author.getLength() == 0){
            NodeList creators = xmlDoc.getElementsByTagName("Creator");
            this.author = creators.item(0).getTextContent();
        }else {
            this.author = author.item(0).getTextContent();
        }
        title = BookData.item(0).getTextContent();
    }


}//end class




