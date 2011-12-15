package com.jayceebee.budgie;


import com.jayceebee.budgie.parsers.AbstractParser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
	
	BudgieApplication app;
	
	public SMSReceiver(BudgieApplication app) {
		super();
		this.app = app;
	}
		

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i("SMSReceiver", "Received SMS Message");
		Bundle bundle = intent.getExtras();

        Object messages[] = (Object[]) bundle.get("pdus");
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++) {
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }
        
        String body = smsMessage[0].getMessageBody();
        
        for (AbstractParser parser : BudgieApplication.parsers) {
        	if (parser.isValidParser(body)) {
        		app.getDbHelper().storeTransaction(parser.process(body));
        	}
        }
		
        //lets broadcast an intent
        Intent i = new Intent();
        i.setAction(BudgieApplication.CUSTOM_INTENT);
        context.sendBroadcast(i);
        
	}

}