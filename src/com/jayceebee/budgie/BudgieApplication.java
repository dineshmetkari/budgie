package com.jayceebee.budgie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.jayceebee.budgie.db.BudgieDBAdapter;
import com.jayceebee.budgie.parsers.AbstractParser;
import com.jayceebee.budgie.parsers.InvestecParser;
import com.jayceebee.budgie.services.BudgieService;

import android.app.Application;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class BudgieApplication extends Application {
	
	public static String TAG="Budgie";
	public static final String CUSTOM_INTENT = "com.jayceebee.budgie.intent.action.UPDATETRANSACTIONS"; //TODO this need to be deleted
	public static final String EVENT_UPDATE_TRANSACTIONS = "com.jayceebee.budgie.intent.action.UPDATETRANSACTIONS";
	public static final String UPDATE_CATEGORIES_EVENT = "com.jayceebee.budgie.intent.action.UPDATECATEGORIES"; //TODO: change name
	public static final String EVENT_UPDATE_MERCHANTS = "com.jayceebee.budgie.intent.action.UPDATEMERCHANTS";
	public static final String EVENT_CATEGORY_UPDATEALL = "com.jayceebee.budgie.intent.category.UPDATEALLSQL";
	public static final String sharedPrefsKey = "budgiePrefs";
	
	private BudgieDBAdapter dbHelper;
	private ContentResolver mContentResolver;
	private IntentFilter mIntentFilter;
	private SMSReceiver mSMSReceiver;
	
	private Date beginDateInterval;
	private Date endDateInterval;
	
	public static ArrayList<AbstractParser> parsers;
	//public static ArrayList<Transaction> transactions;
	//public static ArrayList<TransactionCategory> categories;
	
	
	@Override
	public void onCreate() {
		super.onCreate();

		registerReceiver(mSMSReceiver, mIntentFilter);
		
		mContentResolver = getContentResolver();
		
		//before we do anything lets start the main service - will do nothing if already started
		Log.d(TAG, "About to try and start Budgie Service");
		if(!BudgieService.start()){
        	Log.e(TAG, "Failed to start Budgie services");
        	return; // Should exit
        }
		
		parsers = new ArrayList<AbstractParser>();
						
		//setup DB
		dbHelper = new BudgieDBAdapter(this.getApplicationContext());
		dbHelper.open();
		
		/*Cursor catCursor = dbHelper.fetchAllCategories();
		catCursor.moveToFirst();
        while (catCursor.isAfterLast() == false) {
        	Log.d("TAG", "we have some sql entries");
            //categories.add(new TransactionCategory(catCursor.getString(1), 1000));
       	    catCursor.moveToNext();
        }
        catCursor.close();*/
		
		
		InvestecParser investecParser = new InvestecParser();
		parsers.add(investecParser);

	}
	
	public BudgieApplication() {
		super();
		mSMSReceiver = new SMSReceiver(this);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		
		//setup the default date interval
		Calendar cal = Calendar.getInstance();
		Date currentDate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDayOfMonthDate = cal.getTime();
		this.setBeginDateInterval(firstDayOfMonthDate);
		this.setEndDateInterval(currentDate);
				
		BudgieService.setBudgieApplication(this);    
	}

	public void parseExistingSMS() {
    	
        Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(mSmsQueryUri, null, null, null, null);
            if (cursor == null) {
                return;
            }
    
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                Log.d(TAG, "Parsing " + body);
                for (AbstractParser parser : BudgieApplication.parsers) {
                	if (parser.isValidParser(body)) {
                		//add transaction to DB
                		Transaction t = parser.process(body);
                		//BudgieApplication.transactions.add(parser.process(body));
                		Log.d(TAG, "Saved transaction with ID: " + dbHelper.storeTransaction(t));
                		//dbHelper.createMerchant(t.getPlace());
                		break;
                	}
                }
            
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return;
    }

	public BudgieDBAdapter getDbHelper() {
		return dbHelper;
	}
	
	public Date getBeginDateInterval() {
		return beginDateInterval;
	}

	public void setBeginDateInterval(Date beginDateInterval) {
		this.beginDateInterval = beginDateInterval;
	}

	public Date getEndDateInterval() {
		return endDateInterval;
	}

	public void setEndDateInterval(Date endDateInterval) {
		this.endDateInterval = endDateInterval;
	}

}
