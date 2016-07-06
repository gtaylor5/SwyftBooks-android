package com.swyftlabs.swyftbooks;


import android.util.Log;

import java.io.IOException;
import java.io.Serializable;



public class Retailer implements Serializable {
	
	String retailerName = "stuff"; //name of seller
	String urlToSearchForBook = "stuff"; // search url
	String XMLFile = "stuff"; //xml file to be parsed
	String deepLink = ""; // link to buy book on site.
	
	//build url based on ISBN number
	public void buildURL(String ISBN){

		switch (this.retailerName) {

			case ("ValoreBooks.com"):
				this.urlToSearchForBook = "http://prices.valorebooks.com/lookup-multiple-categories?SiteID=3FZG6Y&ProductCode=" + ISBN +
						"&TrackingID=3FZG6Y&Level=Detailed&NumberToShow=35&MinimumCondition=5&ShowEditionType=yes";
				this.deepLink = "http://www.valorebooks.com/affiliate/buy/siteID=3FZG6Y/ISBN="+ISBN+"?default=used";
				return;
			case ("Chegg.com"):
				this.urlToSearchForBook = "http://api.chegg.com/rent.svc?KEY=ada6c485ab35b1d2d8189fc08e5c9015&PW=2745708" +
						"&R=XML&V=2.0&isbn=" + ISBN + "&with_pids=1&results_per_page=1";
				return;
			case ("Amazon.com"):
				this.urlToSearchForBook = ""; // INSERT URL CONCATENTATION HERE
				return;
			case ("half.com"):
				this.urlToSearchForBook = "";// INSERT URL CONCATENTATION HERE
				return;
			case ("Textbooks.com"):
				this.urlToSearchForBook = "";// INSERT URL CONCATENTATION HERE
				return;
			case("BookRenter.com"):
				this.urlToSearchForBook = "http://www.bookrenter.com/api/fetch_book_info?developer_key=XzswnzpLScXvQ2309Z2968GJ8zjKmAmV&version=2012-01-01&isbn="+ISBN+"&BookDetails=y";
				this.deepLink = "http://www.bookrenter.com/api/product/"+ISBN+"?developer_key=XzswnzpLScXvQ2309Z2968GJ8zjKmAmV";
				return;
			case("eCampus.com"):
				this.urlToSearchForBook= "http://www.ecampus.com/botpricexml.asp?isbn="+ ISBN;
				this.deepLink = "http://www.kqzyfj.com/click-8044180-5029466-1390403653000?ISBN="+ISBN;
				return;
			case("VitalSource.com"):
				this.urlToSearchForBook = "/v2/product-search?website-id=8044180&advertiser-ids=2544507&isbn="+ISBN;
				return;
		}
	}
	
	//chegg deeplink has special requirements
	public void setCheggDeepLink(String pid){
		this.deepLink = "http://www.chegg.com/?referrer=ada6c485ab35b1d2d8189fc08e5c9015&pids="+pid;
	}

	public Retailer(){// default constructor
	}

	public Retailer(String name){
		this.retailerName = name;
	}
}
