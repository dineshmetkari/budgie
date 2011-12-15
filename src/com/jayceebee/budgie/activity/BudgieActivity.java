package com.jayceebee.budgie.activity;

import java.text.SimpleDateFormat;
import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;
import com.jayceebee.budgie.db.BudgieDBAdapter;
import com.jayceebee.budgie.listadpters.BudgieExpandableListAdapter;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.AdapterView.AdapterContextMenuInfo;

public class BudgieActivity extends ExpandableListActivity {
	private BudgieExpandableListAdapter mAdapter;
	private BudgieApplication app;
	private Cursor merchantCursor;
	private Cursor categoryCursor;
	private Cursor transactionCursor;
	private IntentFilter mIntentFilter;
	
	
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		switch (groupPosition) {
		case 0: //merchant
			if (merchantCursor.moveToPosition(childPosition)) {
				String merchantName = 
					merchantCursor.getString(merchantCursor.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME));
				Intent i = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("merchant_name", merchantName);
				i.setClass(this, MerchantDetailHome.class);
				i.putExtra("com.jayceebee.budgie.merchantdetailname", bundle);
				this.startActivity(i);				
			}
			break;
		case 1: //category
			if (categoryCursor.moveToPosition(childPosition)) {
				String categoryName = categoryCursor.getString(categoryCursor.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_CATEGORY_NAME));
				Intent i = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("category_name", categoryName);
				i.setClass(this, CategoryDetailHome.class);
				i.putExtra("com.jayceebee.budgie.categorydetailname", bundle);
				this.startActivity(i);
			}
			break;
		case 2: //transaction
			//for now there is nothing to do when clicking on a transaction
			break;
		}
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}


	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.i(BudgieApplication.TAG, "Received Filter in Activity - updating expandable lists");
			updateExpandableListAdapter();
			//adapter.notifyDataSetChanged();
		}
	};
	
	public void updateExpandableListAdapter() {
		merchantCursor.requery();
		categoryCursor.requery();
		transactionCursor.requery();
		mAdapter.notifyDataSetChanged();
	}

	private void checkFirstRun() {
		SharedPreferences myPrefs = this.getSharedPreferences(
				BudgieApplication.sharedPrefsKey, MODE_WORLD_READABLE);

		boolean runBefore = myPrefs.getBoolean("FIRST_RUN", false);

		if (!runBefore) {
			// first time to run app - ask to check inbox for existing SMS
			SharedPreferences.Editor prefsEditor = myPrefs.edit();
			prefsEditor.putBoolean("FIRST_RUN", true); // TODO this should be
														// true
			prefsEditor.commit();

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Scan SMS inbox?");
			alertDialog
					.setMessage("This is the first time you have run this application. Would you like to scan your SMS's"
							+ " for all previous transactions?");
			alertDialog.setButton("Yes, please",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							app.parseExistingSMS();
							Context context = getApplicationContext();
							Toast.makeText(context, "Done checking your SMS's",
									Toast.LENGTH_SHORT).show();
						}

					});

			alertDialog.setButton2("No Thanks",
					new DialogInterface.OnClickListener() {
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

		app = (BudgieApplication) this.getApplication();

		setContentView(R.layout.main);
		
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
		
		//set interval date for all views and display in main activity
		TextView dateInterval_text = (TextView)findViewById(R.id.tv_dateinterval);
		dateInterval_text.setText(df.format(app.getBeginDateInterval()) + " - " + df.format(app.getEndDateInterval()));
		
		mIntentFilter = new IntentFilter();
      	mIntentFilter.addAction(BudgieApplication.UPDATE_CATEGORIES_EVENT);
      	mIntentFilter.addAction(BudgieApplication.EVENT_CATEGORY_UPDATEALL);
      	mIntentFilter.addAction(BudgieApplication.EVENT_UPDATE_MERCHANTS);
      	mIntentFilter.addAction(BudgieApplication.EVENT_UPDATE_TRANSACTIONS);
      	
      	registerReceiver(mIntentReceiver, mIntentFilter);
		
		checkFirstRun();

		merchantCursor = app.getDbHelper().fetchAllMerchantsByGroup();
		categoryCursor = app.getDbHelper().fetchAllCategoriesByGroup();
		transactionCursor = app.getDbHelper().fetchAllTransactions();

		// Cache the ID column index
		// mGroupIdColumnIndex = groupCursor.getColumnIndexOrThrow(People._ID);

		// Set up our adapter.
		mAdapter = new BudgieExpandableListAdapter(this, /*app,*/ merchantCursor, categoryCursor, transactionCursor);
		setListAdapter(mAdapter);
		registerForContextMenu(getExpandableListView());
		

		// setListAdapter(mAdapter);
	}
	
	/*@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.layout.transactioncontextmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  //int menuItemIndex = info.position;
	  	  
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
	}*/
	
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
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.homeoptionsmenu, menu);
	    return true;
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
	protected void onDestroy() {
		super.onDestroy();
		merchantCursor.close();
		transactionCursor.close();
		categoryCursor.close();
	}

}
