package com.koteswara.wiseplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.koteswara.wiseplaces.places.SlidingTabLayout;
import com.koteswara.wiseplaces.places.ViewPagerAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class WisePlacesMain extends AppCompatActivity {
	Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    boolean isConnected;
    CharSequence Titles[]={"Find Places","Share Address"};
    int Numboftabs =2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	 //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.tab_host);
    	getSupportActionBar().hide();
    	
    	  // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
    	 /*toolbar = (Toolbar) findViewById(R.id.tool_bar);
         setSupportActionBar(toolbar);*/

    	SharedPreferences settings=WisePlacesMain.this.getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);
        if(firstRun==false)//if running for first time
        //Splash will load for first time
        {
            /*SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.commit();*/
            Intent i=new Intent(WisePlacesMain.this,Second.class);
            startActivity(i);
           // getActivity().finish();
        }
         // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
         adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

         // Assigning ViewPager View and setting the adapter
         pager = (ViewPager) findViewById(R.id.pager);
         pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
		
	}
    
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aboutapp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	switch (item.getItemId()) {
        case R.id.about:
        startActivity(new Intent(this, About.class));
       break;
        
    }
		return true;
    }	*/
    @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		/*SharedPreferences settings=WisePlacesMain.this.getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);
        if(firstRun==false)//if running for first time
        //Splash will load for first time
        {
            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.commit();
            Intent i=new Intent(WisePlacesMain.this,Second.class);
            startActivity(i);
           // getActivity().finish();
        }*/
       /* else
        {

            Intent a=new Intent(WisePlacesMain.this,Introflash.class);
            startActivity(a);
            finish();
        }*/
		
	} 
    
}
