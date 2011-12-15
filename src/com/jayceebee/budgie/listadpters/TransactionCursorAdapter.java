package com.jayceebee.budgie.listadpters;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;
import com.jayceebee.budgie.db.BudgieDBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TransactionCursorAdapter extends SimpleCursorAdapter {

	private Cursor c;
    private Context context;
    
	public TransactionCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.c = c;
		this.context = context;
	}
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.transactionrow, null);
	       }
	       
	       this.c.moveToPosition(pos);
	       Log.d(BudgieApplication.TAG, "Moving cursor to position " + pos + " and cat name is " + c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME)) 
	    		   + " and category is " + c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_CATEGORY_NAME)));
	       
	       
	       
	       TextView tvTransactionPlace = (TextView) v.findViewById(R.id.tv_transaction_place);
           TextView tvTransactionDate = (TextView) v.findViewById(R.id.tv_transaction_date);
           TextView tvTransactionAmount = (TextView) v.findViewById(R.id.tv_transaction_amount);
           TextView tvTransactionCategory = (TextView) v.findViewById(R.id.tv_transaction_category);
           if (tvTransactionPlace != null) {
        	   String categoryName;
        	    
        	   if ((categoryName = c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_CATEGORY_NAME))) != null && !categoryName.equalsIgnoreCase("")) {
        		   tvTransactionCategory.setText(categoryName);
        		   //tvTransactionCategory.setVisibility(TextView.VISIBLE);
        		   //tvTransactionPlace.setText("Where: " + c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME)) + " (" + categoryName + ")");
        	   } else {
        		   tvTransactionCategory.setText("");
        	   }
        	   tvTransactionPlace.setText(c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME)));        		   
           }
           if (tvTransactionDate != null) {
           	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
           	int timeStamp = c.getInt(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_DATE));
           	           	
           	Date date = new Date(Long.valueOf(timeStamp)*1000);     	
          	tvTransactionDate.setText(dateFormatter.format(date));
           }
           if(tvTransactionAmount != null){
        	     String AmountHeader = context.getString(R.string.currencysign);
                 tvTransactionAmount.setText(AmountHeader.concat(Double.toString(c.getDouble(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_AMOUNT)))));
           }
	       return(v);
	}
}
