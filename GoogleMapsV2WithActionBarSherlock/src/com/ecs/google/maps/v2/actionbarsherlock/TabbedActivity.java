package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Window;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.ecs.google.maps.v2.fragment.AnimatingMarkersFragment;
import com.ecs.google.maps.v2.fragment.PlayingWithMarkersFragment;
import com.ecs.google.maps.v2.fragment.SimpleCardFragment;
import com.ecs.google.maps.v2.fragment.SupportMapFragmentWithMenu;

public class TabbedActivity extends SherlockFragmentActivity { 

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_tabs);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		
		pager.setAdapter(adapter);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getSupportMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("currentColor", "currentColor");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String currentColor = savedInstanceState.getString("currentColor");
		//changeColor(currentColor);
		System.out.println("Found color " + currentColor);
	}	

	private static String makeFragmentName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { 	"Simple Map", 
											"Playing With Markers", 
											"Animation", 
											"Maps Utils Library"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			if (position==0) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);
				
				SupportMapFragmentWithMenu f = new SupportMapFragmentWithMenu();
				//SimpleSupportMapFragment f = new SimpleSupportMapFragment();
				//MapFragment f = new MapFragment();
				Bundle b = new Bundle();
				b.putInt("position", position);
				f.setArguments(b);
				return f;
			} else if (position==1) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);

				
				PlayingWithMarkersFragment f = new PlayingWithMarkersFragment();
				//f.setHasOptionsMenu(true); // Important ... do not forget this.
				Bundle b = new Bundle();
				b.putInt("position", position);
				f.setArguments(b);
				return f;
			} else if (position==2) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);

				
				AnimatingMarkersFragment f = new AnimatingMarkersFragment();
				//f.setHasOptionsMenu(true); // Important ... do not forget this.
				Bundle b = new Bundle();
				b.putInt("position", position);
				f.setArguments(b);
				return f;
			} else {
				return SimpleCardFragment.newInstance(position);
			}
		}

	}


}