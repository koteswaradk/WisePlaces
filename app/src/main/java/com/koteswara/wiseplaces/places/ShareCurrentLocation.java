package com.koteswara.wiseplaces.places;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.koteswara.wiseplaces.R;
import com.koteswara.wiseplaces.util.CheckInternetConnection;
import com.koteswara.wiseplaces.util.InternetCheckDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ShareCurrentLocation extends Fragment implements OnLocationChangedListener, LocationListener, OnClickListener, OnMapReadyCallback {
	AdView mAdView;
	AdRequest adRequest;
	private InterstitialAd interstitial;
	GoogleMap googleMap;
	Location location;
	private LocationManager locationManager;
	String CityName = "", StateName = "", CountryName = "", s_address, result = null, ContactNumber, name;
	double latitude, longitude;
	Bitmap _snapshot;
	ImageView snapshotHolder;
	byte[] temp_photo;
	ImageButton _watsapp_button, _facebook_button, _message_button, _share_button;
	boolean flag = false;
	TelephonyManager tm;
	SupportMapFragment fm;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		result = null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		result = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootview = inflater.inflate(R.layout.sharecurrentlocation, container, false);
		// Getting reference to the SupportMapFragment of activity_main.xml
		fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		mAdView = (AdView) rootview.findViewById(R.id.adView);
		interstitial = new InterstitialAd(getContext());
		_watsapp_button = (ImageButton) rootview.findViewById(R.id.watsapp_button);
		//_facebook_button=(ImageButton) rootview.findViewById(R.id.facebook_button);
		_message_button = (ImageButton) rootview.findViewById(R.id.message_button);
		_share_button = (ImageButton) rootview.findViewById(R.id.share_button);
		/*snapshotHolder=(ImageView)rootview.findViewById(R.id.screenimage);*/
		_watsapp_button.setOnClickListener(this);
		_message_button.setOnClickListener(this);
		//_facebook_button.setOnClickListener(this);
		tm = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		interstitial.setAdUnitId(getResources().getString(
				R.string.admobi_adUid_interstitial_add));
		tm = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		// TODO: Consider calling
		//    ActivityCompat#requestPermissions
		// here to request the missing permissions, and then overriding
		//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
		//                                          int[] grantResults)
		// to handle the case where the user grants the permission. See the documentation
		// for ActivityCompat#requestPermissions for more details.
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
			tm.getDeviceId();
		adRequest = new AdRequest.Builder()
				//.addTestDevice(deviceid)
				.build();
		mAdView.loadAd(adRequest);
		callLocationUpdate();
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
		//snapshotHolder=new ImageView(getActivity());
     		
     		
     		/*RotateAnimation animation = (RotateAnimation)AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
     		_watsapp_button.startAnimation(animation);
     		_facebook_button.startAnimation(animation);
     		_message_button.startAnimation(animation);
     		_watsapp_button.startAnimation(animation);*/

		// Getting GoogleMap object from the fragment


		return rootview;
	}

	void callLocationUpdate() {
		fm.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				googleMap = googleMap;
			}
		});

		// Enabling MyLocation Layer of Google Map
		googleMap.getMyLocation();

		CheckInternetConnection intenercheck = new CheckInternetConnection(getActivity());
		if (!intenercheck.CheckInternet()) {
			InternetCheckDialog dialog = new InternetCheckDialog(getActivity());
			dialog.showDialog();
		} else {
			// Getting LocationManager object from System Service LOCATION_SERVICE
			locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			LocationListener locListener = new ShareCurrentLocation();

			//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);
			//locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, 0, locListener);
			// Getting Current Location
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.

			}

			location = locationManager.getLastKnownLocation(provider);

         

         if(location!=null){
                 onLocationChanged(location);
         }

         locationManager.requestLocationUpdates(provider, 100, 0, this);
		}
  		_share_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getActivity(), "share_button", Toast.LENGTH_SHORT).show();
				if (!flag) {
					TranslateAnimation slide = new TranslateAnimation(0, 0, 100,0 );
					 slide.setDuration(1000);
					 slide.setFillAfter(true); 
					
					Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.bottomtotop);
					_message_button.startAnimation(anim);
					_watsapp_button.startAnimation(anim);
					//_facebook_button.startAnimation(anim);
					
					_message_button.setVisibility(View.VISIBLE);
					//_facebook_button.setVisibility(View.VISIBLE );
					_watsapp_button.setVisibility(View.VISIBLE);
					flag=true;
				}else {
					Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.toptobottom);
					_message_button.startAnimation(anim);
					_watsapp_button.startAnimation(anim);
					//_facebook_button.startAnimation(anim);
					_message_button.setVisibility(View.INVISIBLE);
					//_facebook_button.setVisibility(View.INVISIBLE );
					_watsapp_button.setVisibility(View.INVISIBLE);
					flag=false;
					
				}
				
			}
		});
    }
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	// Insert the Ad Unit ID
    	callLocationUpdate();
    	
    			
    	
    }
    @Override
	public void onLocationChanged(Location location) {
		
		//TextView tvLocation = new TextView(getActivity());
				//(TextView) findViewById(R.id.tv_location);
    	
    	//location.getAccuracy();
		// Getting latitude of the current location
		 latitude = location.getLatitude();
		
		// Getting longitude of the current location
		 longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		// Showing the current location in Google Map
		try{
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
		
		getLocationAddress(latitude,longitude);
		}catch(NullPointerException e){
			
		}
		}		
	
    private void getLocationAddress( double latitude,  double longitude){  
    	
        
                          
                           try {
                        	   Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                               List<Address> addressList = geocoder.getFromLocation(
                                       latitude, longitude, 1);
                               if (addressList != null && addressList.size() > 0) {
                                   Address address = addressList.get(0);
                                   StringBuilder sb = new StringBuilder();
                                   for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                       sb.append(address.getAddressLine(i)).append("\n");
                                   }
                                  /* sb.append(address.getLocality()).append("\n");
                                   sb.append(address.getPostalCode()).append("\n");*/
                                   sb.append(address.getCountryName());
                                   result = sb.toString();
                                  // s_address=result;
                                   Log.i("address", ""+result);
                                   Log.i("getting latlang", ""+latitude+""+longitude);
                               }
                           } catch (IOException e) {
                              // Log.e("GetLocation Address", "Unable connect to Geocoder", e);
                           }catch (NullPointerException e) {
							// TODO: handle exception
                        	   Toast.makeText(getActivity(), "some problem with google server", Toast.LENGTH_SHORT).show();
						}
       }
  /*public  String getMyCurrentLocation(double Lat,double Lang,Location location) {
       
        try
        {
        		// Getting address from found locations.
        Geocoder geocoder;
       
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
         addresses = geocoder.getFromLocation(Lat, Lang, 1);
			System.out.println("latitide"+Lat);
			System.out.println("langitude"+Lang);
        StateName= addresses.get(0).getAdminArea();
        CityName = addresses.get(0).getLocality();
        CountryName = addresses.get(0).getCountryName();
        addresses.get(0).getPostalCode();
        addresses.get(0).getPremises();
        addresses.get(0).getSubAdminArea();
        addresses.get(0).getAddressLine(0);
        addresses.get(0).getFeatureName();
        addresses.get(0).getSubLocality();
       Log.i(""+StateName+""+CityName+""+CountryName+CountryName+addresses.get(0).getPostalCode()+"\n"+ addresses.get(0).getPremises()+
    		   addresses.get(0).getSubAdminArea()+addresses.get(0).getAddressLine(0)+  addresses.get(0).getFeatureName()+ addresses.get(0).getSubLocality()
    		   +"\n", "detail adreesss");
        // you can get more details other than this . like country code, state code, etc.
       
       
        System.out.println(" StateName " + StateName);
        System.out.println("\n"+" CityName " + CityName);
        System.out.println("\n"+" CountryName " + CountryName);
        
        System.out.println(" StateName " +  addresses.get(0).getPostalCode());
        System.out.println("\n"+" CityName " +  addresses.get(0).getPremises());
        System.out.println("\n"+" CountryName " +  addresses.get(0).getSubAdminArea());
        
        System.out.println(" Street Name " +   addresses.get(0).getAddressLine(0));
        System.out.println(" CityName " + addresses.get(0).getFeatureName());
        System.out.println(" Area Name " +  addresses.get(0).getSubLocality());
        System.out.println(" premisis " +  addresses.get(0).getPremises());
        System.out.println(" phone " +  addresses.get(0).getPhone());
        System.out.println(" postal code " +  addresses.get(0).getPostalCode());
        System.out.println(" Sub Area " +  addresses.get(0).getSubAdminArea());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
     
        Toast.makeText(getActivity(), " StateName " + StateName +" CityName " + CityName +" CountryName " + CountryName, Toast.LENGTH_SHORT).show();
        String returnaddress=StateName+"\n"+CityName+"\n"+CountryName;
		return returnaddress;
       
    }*/
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.watsapp_button:
			//takeSnapshot();
			//Uri imgUri=Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+temp_photo);
			// File tmpBitmap = getActivity().getFileStreamPath("tempfile_wip");
			//address = _gettext.getText().toString();
		
			//String uri = Uri.Builder().scheme("geo").appendPath(latitude +","+ longitude).appendQueryParameter("q", result).build();
			//String uri = "http://maps.google.com/maps?saddr=" +latitude+","+longitude;
			
			/*String uri = "geo:" + latitude + ","
					+longitude + "?q=" + latitude
                    + "," + longitude;*/
			//Uri imgUri=Uri.parse("android.resource://my.package.name/"+tmpBitmap);
		//	Uri imgUri=Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+tmpBitmap);
			/*String uri = 
            "http://maps.google.com/maps?q=" + latitude + "," + latitude + "&iwloc=A";
			Uri myUri = Uri.parse(uri);*/
			CheckInternetConnection intenercheck=new CheckInternetConnection(getActivity());
			if (!intenercheck.CheckInternet()) {
				InternetCheckDialog dialog=new InternetCheckDialog(getActivity());
				dialog.showDialog();
			}
			else{
				if(result!=null){
				    Intent shareIntent = new Intent();
				    
				    shareIntent.setAction(Intent.ACTION_SEND);
				    //Target whatsapp:
				    shareIntent.setPackage("com.whatsapp");
				    //Add text and then Image URI
				    shareIntent.putExtra(Intent.EXTRA_TEXT, result);
				 
				   // System.out.println(result);
				   // System.out.println(uri);
				    //for sending image as location 
				   /* shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
				    shareIntent.setType("image/*");*/
				    shareIntent.setType("text/plain");
				    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				    	
				    try {
				        startActivity(shareIntent);
				    } catch (android.content.ActivityNotFoundException ex) {
				        //ToastHelper.MakeShortText("Whatsapp have not been installed.");
				    }
				}else{
					callLocationUpdate();
					Toast.makeText(getActivity(), "please wait loaction nedd to be get updated", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		/*case R.id.facebook_button:
					
			break;*/
		case R.id.message_button:
			
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);  
			startActivityForResult(intent, 1); 
			
			
	       
			break;

		
		    }
		
		
	}
	 private void sendSMS(String phoneNumber, String message) {
	        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
	        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
	        PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0,
	                new Intent(getActivity(), SmsSentReceiver.class), 0);
	        PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity(), 0,
	                new Intent(getActivity(), SmsDeliveredReceiver.class), 0);
	        try {
	            SmsManager sms = SmsManager.getDefault();
	            ArrayList<String> mSMSMessage = sms.divideMessage(message);
	            for (int i = 0; i < mSMSMessage.size(); i++) {
	                sentPendingIntents.add(i, sentPI);
	                deliveredPendingIntents.add(i, deliveredPI);
	            }
	            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
	                    sentPendingIntents, deliveredPendingIntents);

	        } catch (Exception e) {

	            e.printStackTrace();
	            Toast.makeText(getContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
	        }

	    }
	 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		 switch (requestCode) {
		    case (1) :
		     /* if (resultCode == Activity.RESULT_OK) {
		        Uri contactData = data.getData();
		        Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
		        if (c.moveToFirst()) {
		          String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		          String number=c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		          Log.d("", ""+name);
		          Log.d("number", ""+number);
		          // TODO Fetch other Contact details as you want to use

		        }
		      }*/
		    	if (resultCode == Activity.RESULT_OK) {

		    	     Uri contactData = data.getData();
		    	     Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
		    	     if (c.moveToFirst()) {


		    	         String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

		    	         String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		    	           if (hasPhone.equalsIgnoreCase("1")) {
		    	          Cursor phones = getActivity().getContentResolver().query( 
		    	                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
		    	                       ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
		    	                       null, null);
		    	             phones.moveToFirst();
		    	             ContactNumber = phones.getString(phones.getColumnIndex("data1"));
		    	             System.out.println("number is:"+ContactNumber);
		    	           }
		    	          name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		    	         System.out.println("name is:"+name);
		    	         if(result!=null){
		    	         if ((ContactNumber!=null)&&(name!=null)) {
		    	        	  sendSMS(ContactNumber, result);
		    				}else {
		    					Toast.makeText(getActivity(), "need to select contact", Toast.LENGTH_SHORT).show();
		    				}
		    	         }else{
		    	        	 Toast.makeText(getActivity(), "Please wait loaction need to be get updated", Toast.LENGTH_SHORT).show(); 
		    	         }

		    	     }
		    	   }
		      break;
		  }
	}
	 public void onScreenshot(View view) {
	        takeSnapshot();
	    }
	/* private void initShareIntent(String type,String _text){
	     File filePath = getFileStreamPath("shareimage.jpg");  //optional //internal storage
	     Intent shareIntent = new Intent();
	     shareIntent.setAction(Intent.ACTION_SEND);
	     shareIntent.putExtra(Intent.EXTRA_TEXT, _text);
	     shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(filePath)));  //optional//use this when you want to send an image
	     shareIntent.setType("image/jpeg");
	     shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	     startActivity(Intent.createChooser(shareIntent, "send"));
	}*/
	 private void takeSnapshot() {
	        if (googleMap == null) {
	            return;
	        }

	        

	        final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
	            @Override
	            public void onSnapshotReady(Bitmap snapshot) {
	                // Callback is called from the main thread, so we can modify the ImageView safely.
	            	_snapshot=snapshot;
	                snapshotHolder.setImageBitmap(snapshot);
	                ByteArrayOutputStream stream=new ByteArrayOutputStream();
	                snapshot.compress(Bitmap.CompressFormat.JPEG, 90, stream);
	                temp_photo=stream.toByteArray();
	                System.out.println(snapshotHolder+"snap shot ");
	                String fileName = "tempfile_wip";

	                try {
	                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	                    snapshot.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	                    FileOutputStream fo = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
	                    fo.write(bytes.toByteArray());
	                    fo.close();

	                   /* Intent i = new Intent(getApplicationContext(), apply_effects.class);
	                    startActivity(i);*/

	                } catch (Exception e) {
	                    Toast.makeText(getActivity(), "Error 010", Toast.LENGTH_SHORT).show();
	                }
	            }
	        };

	        /*if (mWaitForMapLoadCheckBox.isChecked()) {
	        	googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
	                @Override
	                public void onMapLoaded() {
	                    mMap.snapshot(callback);
	                }
	            });
	        } else {*/
	        	googleMap.snapshot(callback);
	        //}
	    
	 }
	 
	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		googleMap=map;
		
	}

	
}