package com.jayceebee.budgie.activity;

import com.jayceebee.budgie.db.BudgieDBAdapter;
import com.jayceebee.budgie.listadpters.MerchantCursorAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;

public class MerchantHome extends Activity {
    /** Called when the activity is first created. */
	
	IntentFilter mIntentFilter;
	MerchantCursorAdapter cursorAdapter;
	Cursor cursor;
	private BudgieApplication app;
	private long currentMerchantID;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("TAG", "OnActivityResult called");
		updateMerchantList();
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.i(BudgieApplication.TAG, "Received Filter in Activity");
			updateMerchantList();
		}
	};
	
	private void updateMerchantList() 
	{
		cursor.requery();
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (BudgieApplication)getApplication();
    	
      	setContentView(R.layout.merchantmain);
      	
      	mIntentFilter = new IntentFilter();
      	mIntentFilter.addAction(BudgieApplication.EVENT_UPDATE_MERCHANTS);
      	registerReceiver(mIntentReceiver, mIntentFilter);
      	      	        
      	ListView lv = (ListView) findViewById(R.id.MerchantList);
      	      	
      	String [] from = new String []{BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME, BudgieDBAdapter.COLUMN_VIEW_CATEGORY_NAME};
      	int [] to = new int []{R.id.merchantname, R.id.merchant_categoryname};
      	
      	cursor = ((BudgieApplication)this.getApplication()).getDbHelper().fetchAllMerchants();
      	cursorAdapter = new MerchantCursorAdapter(this, R.layout.merchantrow, cursor, from, to);
      	
      	lv.setAdapter(cursorAdapter);
      	registerForContextMenu(lv);      	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.optionscategory, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
		    case R.id.addcategory:
		        //howAddCategory();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.layout.contextmenu_merchant, menu);
	}
	
	private void assignMerchantToCategory(long merchantId)
	{
		currentMerchantID = merchantId;
		Toast.makeText(this, "Trying to assign merchantID: " + merchantId, Toast.LENGTH_LONG).show();
		
		Cursor categoryCursor = ((BudgieApplication)this.getApplication()).getDbHelper().fetchAllCategories();
		final CharSequence[] items = new CharSequence[categoryCursor.getCount()];
		
		int count = 0;
		categoryCursor.moveToFirst();
		
		while (!categoryCursor.isAfterLast()) {
			String catName = categoryCursor.getString(categoryCursor.getColumnIndex(BudgieDBAdapter.COLUMN_CATEGORY_NAME));
			items[count++] = catName;
			categoryCursor.moveToNext();
		}
		
		categoryCursor.close();
		

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Category");
		
	
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	
		        if (app.getDbHelper().assignCategoryToMerchant(currentMerchantID, items[item].toString())) {
		        	//Toast.makeText(getApplicationContext(), "Successful assignment", Toast.LENGTH_SHORT).show();
		        	updateMerchantList();
		        }
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  
	  
	  //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = info.position;
	  Log.i(BudgieApplication.TAG, "MenuItem clicked is " + Integer.toString(menuItemIndex));
	  
	  switch (item.getItemId()) {
		  case R.id.assigncategory:
			  assignMerchantToCategory(info.id);
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
}
