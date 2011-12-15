package com.jayceebee.budgie.db;

import com.jayceebee.budgie.BudgieApplication;
import com.jayceebee.budgie.Transaction;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class BudgieDBAdapter {
	
	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mCtx;    
    
	private static final String DATABASE_NAME = "budgie.db";
	private static final int DATABASE_VERSION = 6;

	// Table name
	public static final String TABLE_TRANSACTION = "btransaction";
	public static final String TABLE_CATEGORIES = "category";
	public static final String TABLE_MERCHANT = "merchant";
	
	public static final String VIEW_ALL_TRANSACTION = "view_all_transactions";
	public static final String VIEW_ALL_TRANSACTION_CATEGORY = "view_all_transactions_category";
	public static final String VIEW_ALL_MERCHANT = "view_all_merchants";
	public static final String VIEW_ALL_MERCHANT_GROUP = "view_all_transactions_merchants";
	public static final String VIEW_ALL_CATEGORY_GROUP = "view_all_transactions_categorygrp";

	// Columns
	public static final String COLUMN_CATEGORY_ID = "_id";
	public static final String COLUMN_CATEGORY_NAME = "NAME";
	public static final String COLUMN_CATEGORY_THRESHOLD = "THRESHOLD";
	
	public static final String COLUMN_MERCHANT_ID = "_id";
	public static final String COLUMN_MERCHANT_NAME = "NAME";
	public static final String COLUMN_MERCHANT_CATEGORY = "CATEGORY_ID";
	
	public static final String COLUMN_TRANSACTION_ID = "_id";
	public static final String COLUMN_TRANSACTION_PLACE = "PLACE_ID";
	public static final String COLUMN_TRANSACTION_DATE = "TRANSACTION_DATE";
	public static final String COLUMN_TRANSACTION_AMOUNT = "AMOUNT";
	
	public static final String COLUMN_VIEW_CATEGORY = "CATEGORY";
	public static final String COLUMN_VIEW_MERCHANT = "PLACE";
	public static final String COLUMN_VIEW_DATE = "DATE";
	public static final String COLUMN_VIEW_AMOUNT = "AMOUNT";
	public static final String COLUMN_VIEW_CATEGORY_NAME = "CATEGORY_NAME";
	public static final String COLUMN_VIEW_MERCHANT_NAME = "MERCHANT_NAME";
	public static final String COLUMN_VIEW_ID = "_id";
	
	public static final String createCategorySQL = "create table " + TABLE_CATEGORIES + "( " 
		+ COLUMN_CATEGORY_ID + " integer primary key autoincrement, " 
		+ COLUMN_CATEGORY_NAME + " text not null, " 
		+ COLUMN_CATEGORY_THRESHOLD + " real not null);";
	
	public static final String createTransactionSQL = "create table " + TABLE_TRANSACTION + "( " 
		+ COLUMN_TRANSACTION_ID + " integer primary key autoincrement, " 
		+ COLUMN_TRANSACTION_PLACE + " integer not null, " 
		+ COLUMN_TRANSACTION_DATE + " integer not null, "
		+ COLUMN_TRANSACTION_AMOUNT	+ " real not null);";
	
	public static final String createMerchantSQL = "create table " + TABLE_MERCHANT + "( " 
		+ COLUMN_MERCHANT_ID + " integer primary key autoincrement, " 
		+ COLUMN_MERCHANT_NAME + " text not null unique collate nocase, " 
		+ COLUMN_MERCHANT_CATEGORY + " integer not null default 1);";
	
	//SELECT btransaction._id, btransaction.PLACE_ID, btransaction.TRANSACTION_DATE, btransaction.AMOUNT, merchant.NAME as MERCHANT, merchant.CATEGORY 
	//from btransaction,merchant where PLACE_ID=merchant._id
	public static final String createTransactionViewSQL = "CREATE VIEW \"" + VIEW_ALL_TRANSACTION + "\" AS SELECT " 
		+ TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_ID + " AS " + COLUMN_VIEW_ID + "," 
		+ TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_PLACE + " AS " + COLUMN_VIEW_MERCHANT + "," 
		+ TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_DATE + " AS " + COLUMN_VIEW_DATE + ","
		+ TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_AMOUNT + " AS " + COLUMN_VIEW_AMOUNT + "," 
		+ TABLE_MERCHANT + "." + COLUMN_MERCHANT_NAME + " AS " + COLUMN_VIEW_MERCHANT_NAME + "," 
		+ TABLE_MERCHANT + "." + COLUMN_MERCHANT_CATEGORY + " AS " + COLUMN_VIEW_CATEGORY
		+ " FROM " + TABLE_TRANSACTION + ","  + TABLE_MERCHANT + " WHERE "
		+ TABLE_TRANSACTION + "." + COLUMN_TRANSACTION_PLACE + "=" + TABLE_MERCHANT + "." + COLUMN_MERCHANT_ID + ";";
	
	//view_alltransactions.MERCHANT, view_alltransactions.AMOUNT,category.NAME as CATEGORY_NAME FROM view_alltransactions 
	//LEFT JOIN category ON category._id=category";
	public static final String createTransactionCategoryViewSQL = "CREATE VIEW \"" + VIEW_ALL_TRANSACTION_CATEGORY + "\" AS SELECT "
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_ID + " AS " + COLUMN_VIEW_ID + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_DATE + " AS " + COLUMN_VIEW_DATE + "," 
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_AMOUNT + " AS " + COLUMN_VIEW_AMOUNT + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_MERCHANT_NAME + " AS " + COLUMN_VIEW_MERCHANT_NAME + ","
	+ TABLE_CATEGORIES + "." + COLUMN_CATEGORY_NAME + " AS " + COLUMN_VIEW_CATEGORY_NAME 
	+ " FROM " + VIEW_ALL_TRANSACTION + " LEFT JOIN "
	+ TABLE_CATEGORIES + " ON " + TABLE_CATEGORIES + "." + COLUMN_CATEGORY_ID + "=" + COLUMN_VIEW_CATEGORY + ";";
	
	public static final String createTransactionMerchantViewSQL = "CREATE VIEW \"" + VIEW_ALL_MERCHANT_GROUP + "\" AS SELECT "
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_ID + " AS " + COLUMN_VIEW_ID + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_DATE + " AS " + COLUMN_VIEW_DATE + "," 
	+ "SUM(" + VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_AMOUNT + ")" + " AS " + COLUMN_VIEW_AMOUNT + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_MERCHANT_NAME + " AS " + COLUMN_VIEW_MERCHANT_NAME + ","
	+ TABLE_CATEGORIES + "." + COLUMN_CATEGORY_NAME + " AS " + COLUMN_VIEW_CATEGORY_NAME 
	+ " FROM " + VIEW_ALL_TRANSACTION + " LEFT JOIN "
	+ TABLE_CATEGORIES + " ON " + TABLE_CATEGORIES + "." + COLUMN_CATEGORY_ID + "=" + COLUMN_VIEW_CATEGORY
	+ " GROUP BY " + COLUMN_VIEW_MERCHANT_NAME
	+ " ORDER BY " + COLUMN_VIEW_AMOUNT + " DESC;";
	
	public static final String createTransactionCategorygrpViewSQL = "CREATE VIEW \"" + VIEW_ALL_CATEGORY_GROUP + "\" AS SELECT "
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_ID + " AS " + COLUMN_VIEW_ID + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_DATE + " AS " + COLUMN_VIEW_DATE + "," 
	+ "SUM(" + VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_AMOUNT + ")" + " AS " + COLUMN_VIEW_AMOUNT + ","
	+ VIEW_ALL_TRANSACTION + "." + COLUMN_VIEW_MERCHANT_NAME + " AS " + COLUMN_VIEW_MERCHANT_NAME + ","
	+ TABLE_CATEGORIES + "." + COLUMN_CATEGORY_NAME + " AS " + COLUMN_VIEW_CATEGORY_NAME 
	+ " FROM " + VIEW_ALL_TRANSACTION + " LEFT JOIN "
	+ TABLE_CATEGORIES + " ON " + TABLE_CATEGORIES + "." + COLUMN_CATEGORY_ID + "=" + COLUMN_VIEW_CATEGORY
	+ " WHERE " + COLUMN_VIEW_CATEGORY_NAME + " IS NOT NULL"
	+ " GROUP BY " + COLUMN_VIEW_CATEGORY_NAME
	+ " ORDER BY " + COLUMN_VIEW_AMOUNT + " DESC;";
	
	//SELECT merchant._id, merchant.NAME as MERCHANT, category.NAME AS CATEGORY FROM merchant 
	//LEFT JOIN category ON merchant.CATEGORY=category._id
	public static final String createMerchantViewSQL = "CREATE VIEW \"" + VIEW_ALL_MERCHANT + "\" AS SELECT " 
	+ TABLE_MERCHANT + "." + COLUMN_MERCHANT_ID + " AS " + COLUMN_VIEW_ID + "," 
	+ TABLE_MERCHANT + "." + COLUMN_MERCHANT_NAME + " AS " + COLUMN_VIEW_MERCHANT_NAME + ","
	+ TABLE_CATEGORIES + "." + COLUMN_CATEGORY_NAME + " AS " + COLUMN_VIEW_CATEGORY_NAME 
	+ " FROM " + TABLE_MERCHANT 
	+ " LEFT JOIN "	+ TABLE_CATEGORIES + " ON " + TABLE_MERCHANT + "." 
	+ COLUMN_MERCHANT_CATEGORY + "=" + TABLE_CATEGORIES + "." + COLUMN_CATEGORY_ID + ";";
	
		
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private Context context;
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("BudgieData", "onCreate: " + createTransactionViewSQL);
			db.execSQL(createCategorySQL);
			db.execSQL(createTransactionSQL);
			db.execSQL(createMerchantSQL);
			db.execSQL(createTransactionViewSQL);
			db.execSQL(createTransactionCategoryViewSQL);
			db.execSQL(createMerchantViewSQL);
			db.execSQL(createTransactionCategorygrpViewSQL);
			db.execSQL(createTransactionMerchantViewSQL);
			
			db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" 
					+ COLUMN_CATEGORY_NAME + ","
					+ COLUMN_CATEGORY_THRESHOLD + ") VALUES (" 
					+ "'Uncategorised', -1);");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion >= newVersion)
				return;
			
			/*Log.d("BudgieData", "OnUpgrade called");
			String sql = null;
			if (oldVersion == 1) 
				sql = "alter table " + TABLE_CATEGORIES + " add column threshold REAL default 0;";
			if (oldVersion == 2)
				sql = "";
	
			Log.d("EventsData", "onUpgrade	: " + sql);
			if (sql != null)
				db.execSQL(sql);*/
			Log.d("TAG", "Creating new VIEW: " + createTransactionViewSQL);
			Log.d("TAG", "Creating new VIEW: " + createTransactionCategoryViewSQL);
			try {
				db.execSQL("DROP table " + TABLE_CATEGORIES);
				db.execSQL("DROP table " + TABLE_TRANSACTION);
				db.execSQL("DROP table " + TABLE_MERCHANT);
				db.execSQL("DROP view " + VIEW_ALL_TRANSACTION);
				db.execSQL("DROP view " + VIEW_ALL_TRANSACTION_CATEGORY);
				db.execSQL("DROP view " + VIEW_ALL_MERCHANT);
				db.execSQL("DROP VIEW " + VIEW_ALL_MERCHANT_GROUP);
				db.execSQL("DROP VIEW " + VIEW_ALL_CATEGORY_GROUP);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.execSQL(createCategorySQL);
			db.execSQL(createTransactionSQL);
			db.execSQL(createMerchantSQL);
			db.execSQL(createTransactionViewSQL);
			db.execSQL(createTransactionCategoryViewSQL);
			db.execSQL(createMerchantViewSQL);
			db.execSQL(createTransactionCategorygrpViewSQL);
			db.execSQL(createTransactionMerchantViewSQL);
			
			db.execSQL("INSERT INTO " + TABLE_CATEGORIES + " (" 
					+ COLUMN_CATEGORY_NAME + ","
					+ COLUMN_CATEGORY_THRESHOLD + ") VALUES (" 
					+ "'Uncategorised', 1);");
			
						
			//reset first run
			SharedPreferences myPrefs = context.getSharedPreferences(BudgieApplication.sharedPrefsKey, Context.MODE_WORLD_READABLE);
			SharedPreferences.Editor prefsEditor = myPrefs.edit();
		    prefsEditor.putBoolean("FIRST_RUN", false);  
		    prefsEditor.commit();
		}
	}
	
	public BudgieDBAdapter(Context context)
	{
		this.mCtx = context;
	}
	
	/**
     * Open the Budge database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public BudgieDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
	public long createMerchant(String name) {
		return createMerchant(name, -1);
	}
	
	public long createMerchant(String name, int categoryId) {
		long ret = 0;

		Log.d(BudgieApplication.TAG, "Creating new Merchant in DB: " + name);

		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_MERCHANT_NAME, name);
		//initialValues.put(COLUMN_MERCHANT_CATEGORY, -1);
		ret = mDb.insert(TABLE_MERCHANT, null, initialValues);
		
		Log.d(BudgieApplication.TAG, "return value from createMerchant: " + ret);
		return ret;
	}
	
	public boolean assignCategoryToMerchant(long merchantID, String categoryName) 
	{
		boolean ret = false;
		
		Cursor cursor = fetchCategory(categoryName);
		if (cursor!=null && cursor.moveToFirst() && cursor.getCount()==1) {
			int categoryId = cursor.getInt(cursor.getColumnIndex(BudgieDBAdapter.COLUMN_CATEGORY_ID));
			ContentValues args = new ContentValues();
	        args.put(COLUMN_MERCHANT_CATEGORY, categoryId);
	        
	        Intent i = new Intent();
	        i.setAction(BudgieApplication.EVENT_UPDATE_TRANSACTIONS);  //TODO prob need to send update for merchants too
	        this.mCtx.sendBroadcast(i);
	        
	        return mDb.update(TABLE_MERCHANT, args, COLUMN_MERCHANT_ID + "=" + merchantID, null) > 0;			
		}
		
		return ret;
	}
	
	
	
	public Cursor fetchAllMerchants() {
        //return mDb.query(TABLE_MERCHANT, new String[] {COLUMN_MERCHANT_ID, COLUMN_MERCHANT_NAME, COLUMN_MERCHANT_CATEGORY}, null, null, null, null, null);
		return mDb.query(VIEW_ALL_MERCHANT, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_CATEGORY_NAME}, null, null, null, null, null);
    }
	
	public Cursor fetchAllMerchantsByGroup() {
        //return mDb.query(TABLE_MERCHANT, new String[] {COLUMN_MERCHANT_ID, COLUMN_MERCHANT_NAME, COLUMN_MERCHANT_CATEGORY}, null, null, null, null, null);
		return mDb.query(VIEW_ALL_MERCHANT_GROUP, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_CATEGORY_NAME, COLUMN_VIEW_AMOUNT}, null, null, null, null, null);
    }
	
	public long fetchMerchantId(String merchantName){
		long ret = -1;
		
		Cursor cursor = mDb.query(TABLE_MERCHANT, new String[] {COLUMN_MERCHANT_ID}, COLUMN_MERCHANT_NAME + "='" + merchantName + "'", null, null, null, null);
		if (cursor!=null && cursor.moveToFirst()) {
			ret = cursor.getInt(cursor.getColumnIndex(COLUMN_MERCHANT_ID));
		}
		
		return ret;
		
	}
	
	public Cursor fetchAllCategoriesByGroup() {
        return mDb.query(VIEW_ALL_CATEGORY_GROUP, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_CATEGORY_NAME, COLUMN_VIEW_AMOUNT}, null, null, null, null, null);
    }
	
	/* TRANSACTION */
	
	public Cursor fetchAllTransactions()
	{
		return mDb.query(VIEW_ALL_TRANSACTION_CATEGORY, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_DATE, COLUMN_VIEW_AMOUNT, COLUMN_VIEW_CATEGORY_NAME}, 
				null, 
				null, 
				null, 
				null, 
				COLUMN_VIEW_DATE + " DESC");
	}
	
	public Cursor fetchAllTransactionsForMerchant(String merchantName) {
		return mDb.query(VIEW_ALL_TRANSACTION_CATEGORY, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_DATE, COLUMN_VIEW_AMOUNT, COLUMN_VIEW_CATEGORY_NAME}, 
				COLUMN_VIEW_MERCHANT_NAME + "='" + merchantName + "'", 
				null, 
				null, 
				null, 
				COLUMN_VIEW_DATE + " DESC");
	}
	
	public Cursor fetchAllMerchantsForCategory(String categoryName) {
		return mDb.query(VIEW_ALL_TRANSACTION_CATEGORY, new String[] {COLUMN_VIEW_ID, COLUMN_VIEW_MERCHANT_NAME, COLUMN_VIEW_DATE, "SUM(" + COLUMN_VIEW_AMOUNT +") AS AMOUNT", COLUMN_VIEW_CATEGORY_NAME}, 
				COLUMN_VIEW_CATEGORY_NAME + "='" + categoryName + "'", 
				null, 
				COLUMN_VIEW_MERCHANT_NAME, /*group by*/ 
				null, 
				COLUMN_VIEW_MERCHANT_NAME + " DESC");
	}
	
	public long storeTransaction(Transaction t) {
		//first try and create the merchant
		long merchantId = createMerchant(t.getPlace());
		
		if (merchantId == -1) { //merchant already exists
			merchantId = fetchMerchantId(t.getPlace());
		}
		
		//insert transaction
		Log.d("Budgie", "Creating new Transaction in DB:");
		long dateEPOCH = t.getDate().getTime()/1000;
		ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_TRANSACTION_PLACE, merchantId);
        initialValues.put(COLUMN_TRANSACTION_AMOUNT, t.getValue());
        initialValues.put(COLUMN_TRANSACTION_DATE, dateEPOCH);
        
        Intent i = new Intent();
        i.setAction(BudgieApplication.EVENT_UPDATE_TRANSACTIONS);  //TODO prob need to send update for merchants too
        this.mCtx.sendBroadcast(i);
		
        Log.d("Budgie", "Done Creating new Transaction in DB:");
        
        return mDb.insert(TABLE_TRANSACTION, null, initialValues);
        
	}
    

    /**
     * Create a new Category using the name provided. If the category is
     * successfully created return the new rowId for that category, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createCategory(String name, double threshold) {
    	Log.d("Budgie", "Creating new Category in DB: " + name);
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_CATEGORY_NAME, name);
        initialValues.put(COLUMN_CATEGORY_THRESHOLD, threshold);
        
        Intent i = new Intent();
        i.setAction(BudgieApplication.UPDATE_CATEGORIES_EVENT);
        this.mCtx.sendBroadcast(i);
        
        return mDb.insert(TABLE_CATEGORIES, null, initialValues);
    }
    
    /**
     * Delete the category with the given rowId
     * 
     * @param rowId id of category to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(long rowId) {
    	//First need to reassign all existing transactions to category -1
    	mDb.execSQL("UPDATE " + TABLE_MERCHANT + " set " 
    			+ COLUMN_MERCHANT_CATEGORY + " = 1 WHERE "
    			+ COLUMN_MERCHANT_CATEGORY + " = " + rowId + ";");
    	
    	Intent i = new Intent();
        i.setAction(BudgieApplication.EVENT_UPDATE_TRANSACTIONS);
        this.mCtx.sendBroadcast(i);
        return mDb.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_ID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteCategory(String categoryName) {

        mDb.execSQL("DELETE FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = '" + categoryName + "';");
        
        Intent i = new Intent();
        i.setAction(BudgieApplication.EVENT_UPDATE_TRANSACTIONS);
        this.mCtx.sendBroadcast(i);
        
        return true;
    }
    
    /**
     * Return a Cursor over the list of all categories in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllCategories() {

        return mDb.query(TABLE_CATEGORIES, new String[] {COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_THRESHOLD}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the category that matches the given rowId
     * 
     * @param rowId id of category to retrieve
     * @return Cursor positioned to matching category, if found
     * @throws SQLException if category could not be found/retrieved
     */
    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, TABLE_CATEGORIES, new String[] {COLUMN_CATEGORY_ID,
                    COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_THRESHOLD}, COLUMN_CATEGORY_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchCategory(String categoryName) throws SQLException {

        Cursor mCursor =

            mDb.query(true, TABLE_CATEGORIES, new String[] {COLUMN_CATEGORY_ID}, COLUMN_CATEGORY_NAME + "='" + categoryName + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Update the category using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the name value passed in
     * 
     * @param rowId id of category to update
     * @param name value to set category name to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCategory(long rowId, String name) {
        ContentValues args = new ContentValues();
        args.put(COLUMN_CATEGORY_NAME, name);
        
        return mDb.update(TABLE_CATEGORIES, args, COLUMN_CATEGORY_ID + "=" + rowId, null) > 0;
    }
}





