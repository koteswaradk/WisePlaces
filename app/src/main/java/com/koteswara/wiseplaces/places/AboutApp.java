package com.koteswara.wiseplaces.places;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koteswara.wiseplaces.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutApp extends Fragment {
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootview = inflater.inflate(R.layout.about,container,false);
		return rootview;
	}

}
