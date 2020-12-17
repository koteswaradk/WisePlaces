package com.koteswara.wiseplaces.util;



import java.lang.reflect.Field;
import java.lang.reflect.Method;



import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MobileDataPackEabledAsynchTask extends AsyncTask<Void, ProgressBar, Boolean>{
Context context;
 ProgressDialog dialog;
 Boolean mobil;
 public MobileDataPackEabledAsynchTask(Context c){
	 context=c;
 }
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		  dialog = new ProgressDialog(context);
          dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
          dialog.setTitle(" Please Wait Data Pack is Connecting.....");
          dialog.setMax(500);
          dialog.setCancelable(false);
          dialog.show();
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		 for(int i=0;i<20;i++){
			// setProgress(5);

                try {
                	 mobil=setMobileDataEnabled(context,true);
                	
                    Thread.sleep(1000);// the timing set to a large value
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
              
            }
                if (mobil) {
                	dialog.dismiss();
                	return true;
				}else{
					/*LayoutInflater inflater = getLayoutInflater();
			    	 View layout = inflater.inflate(R.layout.customtoast,(ViewGroup) findViewById(R.id.toast_layout_root));

			    	 TextView text = (TextView) layout.findViewById(R.id.text);
			    	 text.setText("not connected Check your Data Pack");
			    	 text.setTextSize(20);
			    	 Toast toast = new Toast(getApplicationContext());
			    	 toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			    	 toast.setDuration(Toast.LENGTH_LONG);
			    	 toast.setView(layout);
			    	 toast.show();*/
					Toast.makeText(null, "not connected Check your Data Pack", Toast.LENGTH_SHORT).show();
					
				}
		 
            
		 }
		
		 return false;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
	}
	 private Boolean setMobileDataEnabled(Context context, boolean enabled) {
		    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    try {
		    	final Class conmanClass = Class.forName(conman.getClass().getName());
		 	    final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
		 	    iConnectivityManagerField.setAccessible(true);
		 	    final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		 	    final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
		 	    final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		 	    setMobileDataEnabledMethod.setAccessible(true);

		 	    setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
				
			} catch (Exception e) {
				// TODO: handle exception
				
				Toast.makeText(context, "SetUp Your Data Pack", Toast.LENGTH_SHORT).show();
		    	
			}
			return true;
		   
		}
}
