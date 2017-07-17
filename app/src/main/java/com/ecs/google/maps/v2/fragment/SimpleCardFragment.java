package com.ecs.google.maps.v2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.util.ViewUtils;

public class SimpleCardFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;

	public static SimpleCardFragment newInstance(int position) {
		SimpleCardFragment f = new SimpleCardFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);

		TextView v = new TextView(getActivity());
		//params.setMargins(margin, margin, margin, margin);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
		v.setBackgroundResource(R.drawable.background_card);
		v.setText("CARD " + (position + 1));

		fl.addView(v);
		ViewUtils.initializeMargin(getActivity(), fl);
		
		return fl;
	}

}