package com.jayceebee.budgie.listadpters;

import java.util.ArrayList;

import com.jayceebee.budgie.R;
import com.jayceebee.budgie.TransactionCategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends ArrayAdapter<TransactionCategory>{
	
	private ArrayList<TransactionCategory> categories;
	private Context context;

    public CategoryListAdapter(Context context, int textViewResourceId, ArrayList<TransactionCategory> categories) {
            super(context, textViewResourceId, categories);
    		    	
            this.categories = categories;
            this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.categoryrow, null);
            }
            TransactionCategory category = categories.get(position);
            if (category != null) {
                    TextView tt = (TextView) v.findViewById(R.id.categoryname);
                    TextView bt = (TextView) v.findViewById(R.id.categorylimit);
                    if (tt != null) {
                          tt.setText(category.getName());                            
                    }
                    if(bt != null){
                          bt.setText("Limit set to : "+ category.getLimit());
                    }
                    
            }
            return v;
    }
    
    

}
