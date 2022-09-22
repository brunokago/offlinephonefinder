package com.abdev.offlinephonefinder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS_Received="android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG="SmsBroadcastReceiver";
    String msg,phone="";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    protected LocationManager locationManager;
    protected boolean gps_enabled, network_enabled;

    DatabaseHelper databaseHelper;

    SQLiteDatabase database;
    Cursor cursor;
    ArrayList<String> codesArray;
    private String locationCode;
    private String contactCode;
    private String ringerCode;


    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"intent received:"+intent.getAction());
//        databaseHelper = new DatabaseHelper(MainActivity.appContext);
//        database = databaseHelper.getWritableDatabase();
//        cursor  = database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CODES + "", null);
//
//        if(cursor.moveToFirst()){
//            do{
//                codesArray.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODES_CODE)));
//            } while(cursor.moveToNext());
//        }




        if(intent.getAction()==SMS_Received){
            Bundle dataBundle = intent.getExtras();
            if(dataBundle !=null){
                //creating protocol data unit protocol for transferring messages
                Object[] mypdu = (Object[])dataBundle.get("pdus");
                final SmsMessage [] message = new SmsMessage[mypdu.length];
                for (int i=0 ; i<mypdu.length; i++){
                    //for build version API 23
                    if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i],format);
                    }
                    else {
                        // API < 23
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg =message[i].getMessageBody();
                    phone=message[i].getDisplayOriginatingAddress();
                    //String message =msg;
                }

                String appCode = msg.substring(0,5);

                if(appCode.equalsIgnoreCase("<OPF>")){
                    Intent intentCall = new Intent(context, MainActivity.class);

                    intentCall.putExtra("message", msg);
                    intentCall.putExtra("sender",phone);
                    context.startActivity(intentCall);
                }

            }
        }
    }


}