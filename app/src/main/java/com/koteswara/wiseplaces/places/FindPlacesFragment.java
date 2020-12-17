package com.koteswara.wiseplaces.places;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koteswara.wiseplaces.R;
import com.koteswara.wiseplaces.util.CheckInternetConnection;
import com.koteswara.wiseplaces.util.InternetCheckDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class FindPlacesFragment extends Fragment implements LocationListener {
	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;
	String[] mPlaceType = null, mPlaceTypeName = null;
	static boolean maploaded = false;
	double mLatitude = 0, mLongitude = 0;
	CheckInternetConnection checkinternetconnection;
	private ProgressDialog pDialog;
	AlertDialog alertdialg;
	AdView mAdView;
	AdRequest adRequest;
	InterstitialAd interstitial;
	TelephonyManager tm;
	LocationManager locationManager;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootview = inflater.inflate(R.layout.findplaces, container, false);

		SharedPreferences settings = getActivity().getSharedPreferences("prefs", 0);
		boolean firstRun = settings.getBoolean("firstRun", false);
		if (firstRun) {

		}
		tm = (TelephonyManager) getActivity().getSystemService(
				Context.TELEPHONY_SERVICE);
		mAdView = (AdView) rootview.findViewById(R.id.adView);
		interstitial = new InterstitialAd(getContext());

		interstitial.setAdUnitId(getResources().getString(
				R.string.admobi_adUid_interstitial_add));
		tm = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return null;
		}
		String deviceid = tm.getDeviceId();
		adRequest = new AdRequest.Builder()
				//.addTestDevice(deviceid)
				.build();

		mAdView.loadAd(adRequest);

		//interstitial.loadAd(adRequest);
			
			/*interstitial.setAdListener(new AdListener() {
				public void onAdLoaded() {
					// Call displayInterstitial() function
					// interstitial.show();
					displayInterstitial();
				}

				private void displayInterstitial() {
					// TODO Auto-generated method stub
					if (interstitial.isLoaded()) {
						interstitial.show();
					}
				}
			});*/
		// Array of place types
		mPlaceType = getResources().getStringArray(R.array.place_type);

		// Array of place type names
		mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

		// Creating an array adapter with an array of Place types
		// to populate the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

		// Getting reference to the Spinner 
		mSprPlaceType = (Spinner) rootview.findViewById(R.id.spr_place_type);

		// Setting adapter on Spinner to set place types
		mSprPlaceType.setAdapter(adapter);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());


		if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment
			// Getting Google Map
        	/*pDialog = ProgressDialog.show(getActivity(),
                    null, "Wait Map Is Loading!");*/

			((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMap) {

				}
			});


			// Enabling MyLocation in Google Map
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return null ;
			}
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getCameraPosition();
        	/*if (checkinternetconnection.CheckInternet()) {
        		
			}*/

			// Getting LocationManager object from System Service LOCATION_SERVICE
			locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			LocationListener locListener = new ShareCurrentLocation();
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return null;
			}
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                    onLocationChanged(location);
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
 	    	mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
 	    	mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            locationManager.requestLocationUpdates(provider, 2000, 0, this);
	    	
            //spinner item selected listner
            mSprPlaceType.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					CheckInternetConnection intenercheck=new CheckInternetConnection(getActivity());
					if (!intenercheck.CheckInternet()) {
						InternetCheckDialog dialog=new InternetCheckDialog(getActivity());
						dialog.showDialog();
					}
					else{
						mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
					String spnItem = (String) mSprPlaceType.getItemAtPosition(mSprPlaceType.getSelectedItemPosition());
					if (spnItem.equalsIgnoreCase("Look Here For Places To Find ...")) {
						//Toast.makeText(getActivity(), "Look For Places From Drop Down", Toast.LENGTH_SHORT).show();
						//mGoogleMap.clear();
					}else{
					int selectedPosition = mSprPlaceType.getSelectedItemPosition();
					String type = mPlaceType[selectedPosition];
					
					StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
					sb.append("location="+mLatitude+","+mLongitude);
					sb.append("&radius=2500");
					sb.append("&types="+type);
					sb.append("&sensor=true");
					sb.append("&key=AIzaSyBPRpp3b7CBB8cwTLqPcFBTSKKu2Z3UWR4");
					
					
					// Creating a new non-ui thread task to download Google place json data 
			        PlacesTask placesTask = new PlacesTask();		        			        
			        
					// Invokes the "doInBackground()" method of the class PlaceTask
			        placesTask.execute(sb.toString());
					}
				}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
			});
	    	// Setting click event lister for the find button
			/*btnFind.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {	
					
					
					int selectedPosition = mSprPlaceType.getSelectedItemPosition();
					String type = mPlaceType[selectedPosition];
										
					
					StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
					sb.append("location="+mLatitude+","+mLongitude);
					sb.append("&radius=5000");
					sb.append("&types="+type);
					sb.append("&sensor=true");
					sb.append("&key=AIzaSyBPRpp3b7CBB8cwTLqPcFBTSKKu2Z3UWR4");
					
					
					// Creating a new non-ui thread task to download Google place json data 
			        PlacesTask placesTask = new PlacesTask();		        			        
			        
					// Invokes the "doInBackground()" method of the class PlaceTask
			        placesTask.execute(sb.toString());
					
					
				}
			});*/
	    	
        }	
       
    	return rootview;
 		
	}
	
	

	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);                
                

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();                

                // Connecting to url 
                urlConnection.connect();                

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }

                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("problem downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }

        return data;
    }         

	
	/** A class, to download Google Places */
	private class PlacesTask extends AsyncTask<String, Integer, String>{

		String data = null;
		
		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				 Log.d("Background Task",e.toString());
			}
			return data;
		}
		
		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result){
			if (result!=null) {
				pDialog.dismiss();
			}
			ParserTask parserTask = new ParserTask();
			
			// Start parsing the Google places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(pDialog == null){
				pDialog = new ProgressDialog(getActivity());
				
				//pDialog.setTitle("Serching Your Places Wait...");
				pDialog.setMessage("Serching Your Places Wait...");
			
				pDialog.setCancelable(false);
				//pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}
		
	}
	/*@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences settings=getActivity().getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);
        if(firstRun==false)//if running for first time
        //Splash will load for first time
        {
            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.commit();
            Intent i=new Intent(getActivity(),Second.class);
            startActivity(i);
           // getActivity().finish();
        }
        else
        {

            Intent a=new Intent(WisePlacesMain.this,Introflash.class);
            startActivity(a);
            finish();
        }
		
	}*/
	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		JSONObject jObject;
		
		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String,String>> doInBackground(String... jsonData) {
		
			List<HashMap<String, String>> places = null;			
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();
        
	        try{
	        	jObject = new JSONObject(jsonData[0]);
	        	Log.i("places object" , ""+jObject);
	            /** Getting the parsed data as a List construct */
	            places = placeJsonParser.parse(jObject);
	            Log.i("places " , ""+places);
	        }catch(Exception e){
	                Log.d("Exception",e.toString());
	        }
	        return places;
		}
		
		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){			
			
			// Clears all the existing markers 
			mGoogleMap.clear();
			
			for(int i=0;i<list.size();i++){
			
				// Creating a marker
	            MarkerOptions markerOptions = new MarkerOptions();
	            
	            // Getting a place from the places list
	            HashMap<String, String> hmPlace = list.get(i);
	              Log.i("hash map", ""+hmPlace);
	            // Getting latitude of the place
	            double lat = Double.parseDouble(hmPlace.get("lat"));	            
	            
	            // Getting longitude of the place
	            double lng = Double.parseDouble(hmPlace.get("lng"));
	            
	            // Getting name
	            String name = hmPlace.get("place_name");
	            
	            // Getting vicinity
	            String vicinity = hmPlace.get("vicinity");
	            
	            LatLng latLng = new LatLng(lat, lng);
	            
	            // Setting the position for the marker
	            markerOptions.position(latLng);
	
	            // Setting the title for the marker. 
	            //This will be displayed on taping the marker
	            markerOptions.title(name + " : " + vicinity);	            
	
	            // Placing a marker on the touched position
	            mGoogleMap.addMarker(markerOptions);            
            
			}		
			
		}
		
	}
	
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		/*if (checkinternetconnection.CheckInternet()) {
			try {
				
			} catch (NullPointerException e) {
				// TODO: handle exception
				Toast.makeText(getActivity(), "Internet need to be connected", Toast.LENGTH_SHORT).show();
			}
			
		}else{
			Toast.makeText(getActivity(), "Internet need to be connected", Toast.LENGTH_SHORT).show();
		}*/
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}
