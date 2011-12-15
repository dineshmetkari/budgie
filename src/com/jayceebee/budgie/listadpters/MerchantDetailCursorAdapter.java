package com.jayceebee.budgie.listadpters;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jayceebee.budgie.R;
import com.jayceebee.budgie.db.BudgieDBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MerchantDetailCursorAdapter extends SimpleCursorAdapter {

	private Cursor c;
    private Context context;
    private static final SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy");
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00"); 
    
	public MerchantDetailCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.c = c;
		this.context = context;
	}
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.row_merchantdetail, null);
	       }
	       this.c.moveToPosition(pos);		
	       TextView tt = (TextView) v.findViewById(R.id.transaction_date);
           TextView bt = (TextView) v.findViewById(R.id.transaction_amount);
           if (tt != null) {
        	     Date d = new Date(Long.valueOf(c.getString(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_DATE)))*1000);
                 tt.setText(df.format(d));                            
           }
           if(bt != null){
        	   //String amount = Double.toString(c.getDouble(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_AMOUNT)));
        	   bt.setText("R" + decimalFormat.format(c.getDouble(c.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_AMOUNT))));
        	     
                 
           }
           return(v);
	}
}
