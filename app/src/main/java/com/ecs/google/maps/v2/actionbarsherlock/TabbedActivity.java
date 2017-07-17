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
import com.ecs.google.maps.v2.fragment.DirectionsMapFragment;
import com.ecs.google.maps.v2.fragment.PlayingWithMarkersFragment;
import com.ecs.google.maps.v2.fragment.SimpleAnimatingMarkersFragment;
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

	private static String makeFragmentName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { 	"Simple Map", 
											"Playing With Markers", 
											"Simple Animation",
											"Animation",
											"Directions API", 
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
				return SupportMapFragmentWithMenu.newInstance(position, "Fragment with menu");
				
			} else if (position==1) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);
				return PlayingWithMarkersFragment.newInstance(position,"Playing with markers");
				
			}  else if (position==2) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);
				return SimpleAnimatingMarkersFragment.newInstance(position,"Simple Animations");
				
			} else if (position==3) {
				
				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);
				return AnimatingMarkersFragment.newInstance(position,"Animating Markers");
				
			} else if (position==4) {

				Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager, position));
				System.out.println("*********** fragmentByTag = " + fragmentByTag);
				return DirectionsMapFragment.newInstance(position,"Directions");
				
			} else {
				return SimpleCardFragment.newInstance(position);
			}
		}

	}


}