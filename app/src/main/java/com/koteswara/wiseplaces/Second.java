package com.koteswara.wiseplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;

import androidx.annotation.Nullable;


public class Second extends AppIntro2{

	@Override
	public void init(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        	addSlide(SampleSlide.newInstance(R.layout.intro));
		 	addSlide(SampleSlide.newInstance(R.layout.intro1));
	        addSlide(SampleSlide.newInstance(R.layout.intro2));
	        addSlide(SampleSlide.newInstance(R.layout.intro3));
	        this.getSupportActionBar().hide();
	}

	/*@Override
	public void onSkipPressed() {
		// TODO Auto-generated method stub
		 finish();
	}*/

	@Override
	public void onNextPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDonePressed() {
		// TODO Auto-generated method stub
		/* Intent i=new Intent(Second.this,WisePlacesMain.class);
         startActivity(i);*/
		SharedPreferences settings=Second.this.getSharedPreferences("prefs",0);

		SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("firstRun",true);
        editor.commit();
         finish();
	}

	@Override
	public void onSlideChanged() {
		// TODO Auto-generated method stub
		
	}

}
