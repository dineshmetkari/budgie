/*package com.jayceebee.budgie.activity;

import java.util.ArrayList;
import java.util.List;

import com.jayceebee.achartengine.chart.BudgetChart;
import com.jayceebee.budgie.db.BudgieDBAdapter;
import com.jayceebee.budgie.listadpters.CategoryCursorAdapter;
import com.jayceebee.budgie.listadpters.TransactionCursorAdapter;
import com.jayceebee.budgie.parsers.AbstractParser;
import com.jayceebee.budgie.parsers.InvestecParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;
import com.jayceebee.budgie.Transaction;


public class home extends Activity {
    *//** Called when the activity is first created. *//*
	
	IntentFilter mIntentFilter;
	BudgieApplication app;
	Cursor cursor;
	TransactionCursorAdapter cursorAdapter;
	
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.i(BudgieApplication.TAG, "Received Filter in Activity");
			//adapter.notifyDataSetChanged();
			
			//cursor = app.getDbHelper().fetchAllTransactions();//((BudgieApplication)(arg0.getApplicationContext()).					this.getApplication()).getDbHelper().fetchAllTransactions();
			cursor.requery();
		}
	};
	
	private void checkFirstRun() {
		SharedPreferences myPrefs = this.getSharedPreferences(BudgieApplication.sharedPrefsKey, MODE_WORLD_READABLE);
		
		boolean runBefore = myPrefs.getBoolean("FIRST_RUN", false);
		
		if (!runBefore) {
			//first time to run app - ask to check inbox for existing SMS
			SharedPreferences.Editor prefsEditor = myPrefs.edit();
	        prefsEditor.putBoolean("FIRST_RUN", true); //TODO this should be true 
	        prefsEditor.commit();
	        
	        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Scan SMS inbox?");
			alertDialog.setMessage("This is the first time you have run this application. Would you like to scan your SMS's" +
					" for all previous transactions?");
			alertDialog.setButton("Yes, please", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					app.parseExistingSMS();
					Context context = getApplicationContext();
					Toast.makeText(context, "Done checking your SMS's", Toast.LENGTH_SHORT).show();
				}
				
			});

			alertDialog.setButton2("No Thanks", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alertDialog.show();
		}
        
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (BudgieApplication)this.getApplication();
    	
      	setContentView(R.layout.main);
      	
      	checkFirstRun();
      
      	mIntentFilter = new IntentFilter();
      	mIntentFilter.addAction(BudgieApplication.CUSTOM_INTENT);
      	registerReceiver(mIntentReceiver, mIntentFilter);
      	      	        
      	ListView lv = (ListView) findViewById(R.id.TransactionList);
      	
      	String [] from = new String []{BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME, BudgieDBAdapter.COLUMN_VIEW_DATE, BudgieDBAdapter.COLUMN_VIEW_AMOUNT};
      	int [] to = new int []{R.id.tv_transaction_place, R.id.tv_transaction_date, R.id.tv_transaction_amount};
      	
      	cursor = ((BudgieApplication)this.getApplication()).getDbHelper().fetchAllTransactions();
      	Log.d(BudgieApplication.TAG, "FETCHED [" + cursor.getCount() + "] TRANSACTION FROM VIEW");
      	cursorAdapter = new TransactionCursorAdapter(this, R.layout.transactionrow, cursor, from, to);
      	//adapter = new TransactionAdapter(this, R.layout.transactionrow, BudgieApplication.transactions);
      	lv.setAdapter(cursorAdapter);
      	registerForContextMenu(lv);  
      	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.homeoptionsmenu, menu);
	    return true;
	}
	
	public void showCategories()
	{
		Intent i = new Intent(this.getApplicationContext(), CategoryHome.class);
		startActivity(i);
	
	}
	
	public void showMerchants()
	{
		Intent i = new Intent(this.getApplicationContext(), MerchantHome.class);
		startActivity(i);	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.showmerchants:
	    		showMerchants();
	    		return true;	    	
		    case R.id.exit:
		        this.finish();
		        return true;
		    case R.id.about:
		        //showAbout();
		        return true;
		    case R.id.editcategories:
		    	showCategories();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.layout.transactioncontextmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  
	  
	  //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = info.position;
	  Log.i(BudgieApplication.TAG, "MenuItem clicked is " + Integer.toString(menuItemIndex));
	  //String[] menuItems = getResources().getStringArray(R.array.menu);
	  //String menuItemName = menuItems[menuItemIndex];
	  //String listItemName = Countries[info.position];

	  switch (item.getItemId()) {
		  case R.id.assigncategory:
			  //assignCategory();
			  return true;
		  case R.id.removecategory:
			  //removeCategory();
			  return true;
		  case R.id.deletetransaction:
			  //deleteTransaction();
			  return true;
		  default:
		    return super.onContextItemSelected(item);
	  }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(BudgieApplication.TAG, "Unregistering broadcastlistener");
		unregisterReceiver(mIntentReceiver);
	}
}*/