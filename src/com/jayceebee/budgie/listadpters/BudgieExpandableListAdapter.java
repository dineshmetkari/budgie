package com.jayceebee.budgie.listadpters;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.R;
import com.jayceebee.budgie.db.BudgieDBAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BudgieExpandableListAdapter extends BaseExpandableListAdapter{

	private ArrayList<String> groups;
	//private BudgieApplication app;
	private Cursor merchantCursor;
	private Cursor categoryCursor;
	private Cursor transactionCursor;
	

	private final LayoutInflater mInflater;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

	public BudgieExpandableListAdapter(Context context, /*BudgieApplication app, */Cursor merchantCursor, Cursor categoryCursor, Cursor transactionCursor) {
		mInflater = LayoutInflater.from(context);
		//this.app = app;
		this.transactionCursor = transactionCursor;
		this.categoryCursor = categoryCursor;
		this.merchantCursor = merchantCursor;
		
		groups = new ArrayList<String>();
		groups.clear();
		groups.add("Merchants");
		groups.add("Categories");
		groups.add("Transactions");
	}	

	public Object getChild(int groupPosition, int childPosition) {
		switch (groupPosition) {
		case 0: 
			return merchantCursor.moveToPosition(childPosition);
		case 1:
			return categoryCursor.moveToPosition(childPosition);
		case 2:
			return transactionCursor.moveToPosition(childPosition);
		}
		
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		View view = convertView;
		ChildViewWrapper wrapper;
		Log.d("BUDGIE", "groupPosition is " + groupPosition);
		switch (groupPosition) {
		case 0:
			//if (view == null) {
				view = mInflater.inflate(R.layout.expandablechild, null);
				wrapper = new ChildViewWrapper(view);
				view.setTag(wrapper);
			//} else {
				wrapper = (ChildViewWrapper) view.getTag();
			//}

			if (merchantCursor.moveToPosition(childPosition)){
				wrapper.getTitle().setText(merchantCursor.getString(1));
				wrapper.getTotal().setText("R" + decimalFormat.format(merchantCursor.getDouble(3)));
			};
			break;
		case 1:
			//if (view == null) {
				view = mInflater.inflate(R.layout.expandablechild, null);
				wrapper = new ChildViewWrapper(view);
				view.setTag(wrapper);
			//} else {
				wrapper = (ChildViewWrapper) view.getTag();
			//}

			if (categoryCursor.moveToPosition(childPosition)){
				wrapper.getTitle().setText(categoryCursor.getString(2));
				wrapper.getTotal().setText("R" + decimalFormat.format(categoryCursor.getDouble(3)));
			};
			break;
		case 2:
			//if (view == null) {
				view = mInflater.inflate(R.layout.expandablechild_transaction, null);
				wrapper = new ChildViewWrapper(view);
				view.setTag(wrapper);
			//} else {
				wrapper = (ChildViewWrapper) view.getTag();
			//}
			
			
			if (transactionCursor.moveToPosition(childPosition)){
				wrapper.getTitle().setText(transactionCursor.getString(transactionCursor.getColumnIndex(BudgieDBAdapter.COLUMN_VIEW_MERCHANT_NAME)));
				Date d = new Date(Long.valueOf(transactionCursor.getInt(2))*1000);
				Log.d("BUDGIE", "DAte is " + d);
				
				wrapper.getDate().setText(dateFormatter.format(d));
				wrapper.getTotal().setText("R" + decimalFormat.format(transactionCursor.getDouble(transactionCursor.getColumnIndex("AMOUNT"))));
			};
			break;
		
		}

		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		Log.d("BUDGIE", "Group position is " + groupPosition);
		switch (groupPosition) {
		case 0:// merchant
			return merchantCursor.getCount();
		case 1:// category
			return categoryCursor.getCount();
		case 2:// transaction
			Log.d("BUDGIE", "Transaction count is " + transactionCursor.getCount());
			return transactionCursor.getCount();
		}
		
		return 0;
	}

	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		View view = convertView;
		GroupViewWrapper wrapper;

		if (view == null) {
			view = mInflater.inflate(R.layout.expandablegroup, null);
			wrapper = new GroupViewWrapper(view);
			view.setTag(wrapper);
		} else {
			wrapper = (GroupViewWrapper) view.getTag();
		}

		//Interface i = (Interface) getGroup(groupPosition);
		switch (groupPosition) {
		case 0://merchant
			wrapper.getIcon().setImageResource(R.drawable.shop24);
			break;
		case 1://category
			wrapper.getIcon().setImageResource(R.drawable.category24);
			break;
		case 2://transaction
			wrapper.getIcon().setImageResource(R.drawable.transaction24);
			break;
		}
		wrapper.getTitle().setText(groups.get(groupPosition));

		return view;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * View wrapper for child row as described in The Busy Coder's Guide to
	 * Android Development by Mark L. Murphy.
	 */
	static class ChildViewWrapper {

		private final View mBase;
		private TextView mTitel;
		private TextView mTotal;
		private TextView mDate;

		ChildViewWrapper(View base) {
			mBase = base;
		}

		TextView getTitle() {
			if (mTitel == null) {
				mTitel = (TextView) mBase.findViewById(R.id.inter);
			}
			return mTitel;
		}

		TextView getTotal() {
			if (mTotal == null) {
				mTotal = (TextView) mBase.findViewById(R.id.total_bytes);
			}
			return mTotal;
		}
		
		TextView getDate() {
			if (mDate == null) {
				mDate = (TextView) mBase.findViewById(R.id.txdate);
			}
			return mDate;
		}
	}

	/**
	 * View wrapper for group row as described in The Busy Coder's Guide to
	 * Android Development by Mark L. Murphy.
	 */
	static class GroupViewWrapper {

		private final View mBase;
		private ImageView mIcon;
		private TextView mTitel;
		//private TextView mUpdate;

		GroupViewWrapper(View base) {
			mBase = base;
		}

		ImageView getIcon() {
			if (mIcon == null) {
				mIcon = (ImageView) mBase.findViewById(R.id.icon);
			}
			return mIcon;
		}

		TextView getTitle() {
			if (mTitel == null) {
				mTitel = (TextView) mBase.findViewById(R.id.title_left);
			}
			return mTitel;
		}

	}

}
