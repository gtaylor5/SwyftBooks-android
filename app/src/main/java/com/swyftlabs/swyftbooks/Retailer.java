package com.swyftlabs.swyftbooks;



import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Retailer implements Serializable {
	
	String retailerName = "stuff"; //name of seller
	String urlToSearchForBook = "stuff"; // search url
	String XMLFile = "stuff"; //xml file to be parsed
	String deepLink = ""; // link to buy book on site.
	String buyBackLink = "";
	String rentLink = "";

	private final String AWS_ACCESS_KEY_ID = "AKIAJWKQAZX4GB63XKKA";
	private final String AWS_SECRET_KEY = "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";
	
	//build url based on ISBN number
	public void buildURL(String ISBN){

		switch (this.retailerName) {

			case ("ValoreBooks.com"):
				this.urlToSearchForBook = "http://prices.valorebooks.com/lookup-multiple-categories?SiteID=3FZG6Y&ProductCode=" + ISBN +
						"&TrackingID=3FZG6Y&Level=Detailed&NumberToShow=35&MinimumCondition=5&ShowEditionType=yes";
				this.deepLink = "http://www.valorebooks.com/affiliate/buy/siteID=3FZG6Y/ISBN="+ISBN+"?default=used";
				this.buyBackLink = "http://www.valorebooks.com/affiliate/sell/siteID=3FZG6Y/ISBN="+ISBN+"?t_id=3FZG6Y";

				return;
			case ("Chegg.com"):
				this.urlToSearchForBook = "http://api.chegg.com/rent.svc?KEY=ada6c485ab35b1d2d8189fc08e5c9015&PW=2745708" +
						"&R=XML&V=2.0&isbn=" + ISBN + "&with_pids=1&results_per_page=1";
				return;
			case ("Amazon.com"):
				this.urlToSearchForBook = generateAmazonLink(ISBN);
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
			case("AbeBooks.com"):
				this.urlToSearchForBook = "http://search2.abebooks.com/search?isbn="+ISBN+"&clientkey=759b57aa-22c0-4d15-ad4d-328de084e968";
				return;
			case("CengageBrain.com"):
				this.urlToSearchForBook = "/v2/product-search?website-id=8044180&advertiser-ids=1845757&isbn="+ISBN;
				return;
			case("BiggerBooks.com"):
				this.urlToSearchForBook = "/v2/product-search?website-id=8044180&advertiser-ids=1087150&isbn="+ISBN;
				return;
		}
	}
	
	//chegg deeplink has special requirements
	public void setCheggDeepLink(String pid){
		this.deepLink = "http://chggtrx.com/click.track?CID=267582&AFID=393411&ADID=1088043&SID=&PIDs="+pid;
	}

	public Retailer(){// default constructor
	}

	public Retailer(String name){
		this.retailerName = name;
	}

	public String generateAmazonLink(String ISBN){


		final String ENDPOINT = "ecs.amazonaws.com";

		SignedRequestsHelper helper;
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String requestUrl = null;
		String title = null;

        /* The helper can sign requests in two forms - map form and string form */

        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
		System.out.println("Map form example:");
		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", "AWSECommerceService");
		params.put("AssociateTag", "swyftbooksapp-20");
		params.put("Operation", "ItemLookup");
		params.put("ResponseGroup", "Large");
		params.put("SearchIndex", "All");
		params.put("ItemId", ISBN);
		params.put("IdType", "ISBN");

		requestUrl = helper.sign(params);

		return requestUrl;

	}
}
