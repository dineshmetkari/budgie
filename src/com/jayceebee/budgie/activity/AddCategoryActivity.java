package com.jayceebee.budgie.activity;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;
import com.jayceebee.budgie.utils.BudgieUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCategoryActivity extends Activity implements OnClickListener  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcategory);
		Button button = (Button)findViewById(R.id.button_addcategory);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		//lets add the new category
		String categoryName = BudgieUtils.formatString(((EditText)findViewById(R.id.edit_categoryname)).getText().toString());
		double threshold = Double.parseDouble(((EditText)findViewById(R.id.edit_categorythreshhold)).getText().toString());
		Toast.makeText(this, "Creating Category: " + categoryName + " and threshold: " + threshold, Toast.LENGTH_LONG).show();
		
		BudgieApplication app = (BudgieApplication)this.getApplication();
		
		app.getDbHelper().createCategory(categoryName, threshold);
		//BudgieApplication.categories.add(new TransactionCategory(categoryName, threshold));
		
		Intent i = new Intent();
		setResult(100, i);
		this.finish();		
	}
	

}
