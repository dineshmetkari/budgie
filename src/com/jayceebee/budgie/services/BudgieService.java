package com.jayceebee.budgie.services;

import com.jayceebee.budgie.BudgieApplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BudgieService extends Service {

	private static boolean started = false;
	private static BudgieApplication budgieApp;
	private BroadcastReceiver smsReceiver;
	private IntentFilter intentFilter;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	  public void onCreate() {
	    super.onCreate();
	    smsReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				Log.d("BUDGIE", "Received SMS");
				
			}
		};
		
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(smsReceiver, intentFilter);
	    Log.d(BudgieApplication.TAG, "Creating Budgie Service");
	  }
	 
	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    Log.d(BudgieApplication.TAG, "Destroying Budgie Service");
	 
	    
	  }
	  public static boolean start() {
		  if(BudgieService.started){
			Log.d(BudgieApplication.TAG, "Budgie Service already Started");
			return true;
		  }
		  Log.d(BudgieApplication.TAG, "Budgie Service Started");
		  
		  BudgieService.started = true;
		  return true;
	  }
	  
	  public static boolean stop() {
		  Log.d(BudgieApplication.TAG, "Budgie Service Stopped");
		  if(!BudgieService.started){
				return true;
		  }
		  
		  BudgieService.started = false;
		  return true;
	  }
	  
	  public static void setBudgieApplication(BudgieApplication app){
			BudgieService.budgieApp = app;
		}

}
