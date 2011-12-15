package com.jayceebee.budgie.activity;

import com.jayceebee.budgie.db.BudgieDBAdapter;
import com.jayceebee.budgie.listadpters.CategoryCursorAdapter;

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

public class CategoryHome extends Activity {
    /** Called when the activity is first created. */
	
	IntentFilter mIntentFilter;
	CategoryCursorAdapter cursorAdapter;
	Cursor cursor;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("TAG", "OnActivityResult called");
		updateCategoryList();
	}

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.i(BudgieApplication.TAG, "Received Filter in Activity");
			updateCategoryList();
			//adapter.notifyDataSetChanged();
		}
	};
	
	private void updateCategoryList() 
	{
		cursor.requery();
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	
      	setContentView(R.layout.categorymain);
      	
      	mIntentFilter = new IntentFilter();
      	mIntentFilter.addAction(BudgieApplication.UPDATE_CATEGORIES_EVENT);
      	registerReceiver(mIntentReceiver, mIntentFilter);
      	      	        
      	ListView lv = (ListView) findViewById(R.id.CategoryList);
      	      	
      	String [] from = new String []{BudgieDBAdapter.COLUMN_CATEGORY_NAME, BudgieDBAdapter.COLUMN_CATEGORY_THRESHOLD};
      	int [] to = new int []{R.id.categoryname, R.id.edit_categorythreshhold};
      	
      	cursor = ((BudgieApplication)this.getApplication()).getDbHelper().fetchAllCategories();
      	cursorAdapter = new CategoryCursorAdapter(this, R.layout.categoryrow, cursor, from, to);
      	
      	lv.setAdapter(cursorAdapter);
      	registerForContextMenu(lv);      	
    }
	
	public void showAddCategory()
	{
		Intent i = new Intent(this.getApplicationContext(), AddCategoryActivity.class);
		startActivityForResult(i, 100);
		//startActivity(i);
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
		        showAddCategory();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.layout.contextmenucategory, menu);
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
		  case R.id.editcategory:
			  showAddCategory();
			  return true;
		  case R.id.removecategory:
			  removeCategory(info.id);
			  //adapter.remove(adapter.getItem(info.position));
			  return true;
		   default:
		    return super.onContextItemSelected(item);
	  }
	}
	
	private void removeCategory(final long id)
	{
		final BudgieApplication app = (BudgieApplication) this.getApplication();
		final String categoryName = app.getDbHelper().fetchCategory(id).getString(1);
		final CategoryHome act = this;
		
		

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Delete " + categoryName);
		alertDialog.setMessage("Are you sure you want to delete this Category?");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// deleting if user approved
				app.getDbHelper().deleteCategory(id);
				act.updateCategoryList();
				
				// notifying user for deletion
				Context context = getApplicationContext();
				Toast.makeText(context, categoryName + " " + "deleted",
						Toast.LENGTH_SHORT).show();
			}
			
		});

		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		alertDialog.show();
		
	}
	
	/*private void removeCategory(final String categoryName) {
		final BudgieApplication app = (BudgieApplication) this.getApplication();

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Delete " + categoryName);
		alertDialog.setMessage("Are you sure you want to delete this Category?");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// deleting if user approved
				app.getDbHelper().deleteCategory(categoryName);

				// notifying user for deletion
				Context context = getApplicationContext();
				Toast.makeText(context, categoryName + " " + "deleted",
						Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Context context = getApplicationContext();
				Toast.makeText(context, "canceled", Toast.LENGTH_SHORT).show();
			}
		});
		alertDialog.show();
	}*/

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.i(BudgieApplication.TAG, "Unregistering broadcastlistener");
		unregisterReceiver(mIntentReceiver);
	}
}
