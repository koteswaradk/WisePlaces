package com.koteswara.wiseplaces.util;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.ProgressBar;

public class WifiEnabledAsynchTaskCheck extends AsyncTask<Void, ProgressBar, Boolean>{

	Context context;
	ConnectivityManager connec;
	 ConnectivityManager connectivity;
	 NetworkInfo wifiInfo, mobileInfo;
	 WifiManager wifimanager;
	 AlertDialog connectdialog;
	 boolean condition;
	 Boolean mobil;
	 Boolean scucess=false;
	 ProgressDialog dialog;
	public WifiEnabledAsynchTaskCheck(Context c){
		context=c;
	 }
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		 dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(" Please Wait WiFi is Connecting.....");
            dialog.setMax(500);
            dialog.setCancelable(false);
            dialog.show();
            System.out.println(" progress dislaed");
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		 for(int i=0;i<20;i++){
			 //setProgress(5);

                try {
                	connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        		    wifiInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        		    wifimanager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        		    wifimanager.startScan();
        		    scucess=wifimanager.setWifiEnabled(true);
        		    
                
        		    if (scucess) {
        		    	 System.out.println("returned true"+scucess);
        		    	 return true;
        		    	
					}
                    Thread.sleep(1000);// the timing set to a large value
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
              
            }
             	            
		 }
		 System.out.println("returned false"+scucess);
		 return false;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		 System.out.println("onpost execute");
		dialog.dismiss();
	}
	

}
