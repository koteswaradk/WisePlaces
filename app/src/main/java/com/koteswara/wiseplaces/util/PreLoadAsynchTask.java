package com.koteswara.wiseplaces.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;

public class PreLoadAsynchTask extends AsyncTask<Void, ProgressBar, Boolean>{

	 ProgressDialog dialog;
	 Context context;
	 private boolean activity_status=true;
		private int splash_time=2000;
	public PreLoadAsynchTask(Context c){
		 context =c;
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
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
Thread splash= new Thread(new Runnable() {
			
			
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					int wait_id=0;
					
					while(activity_status && wait_id<splash_time)
					{	
					Thread.sleep(1000);
						if(activity_status)
						{
							wait_id+=100;
						}
					} 
					}
				
				catch (Exception e) 
				{
					// TODO: handle exception
				}
				finally
				{
					dialog.dismiss();
					/*finish();
					startActivity(new Intent("com.stratvave.biketracker.main.MainBikeTrackerActivity"));*/
				}
		};
		
		});
		splash.start();
		return null;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.dismiss();
	}

}
