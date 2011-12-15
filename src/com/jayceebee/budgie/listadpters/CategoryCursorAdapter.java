package com.jayceebee.budgie.listadpters;

import com.jayceebee.budgie.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CategoryCursorAdapter extends SimpleCursorAdapter {

	private Cursor c;
    private Context context;
    
	public CategoryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.c = c;
		this.context = context;
	}
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.categoryrow, null);
	       }
	       this.c.moveToPosition(pos);		
	       TextView tt = (TextView) v.findViewById(R.id.categoryname);
           TextView bt = (TextView) v.findViewById(R.id.categorylimit);
           if (tt != null) {
                 tt.setText(c.getString(1));                            
           }
           if(bt != null){
                 bt.setText("Limit set to : "+ c.getDouble(2));
           }
	       return(v);
	}
}
