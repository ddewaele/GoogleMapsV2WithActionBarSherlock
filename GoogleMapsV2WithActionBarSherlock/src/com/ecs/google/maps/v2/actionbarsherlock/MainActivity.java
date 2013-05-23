package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {
    
	public static int THEME = R.style.Theme_Sherlock;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GoogleMapsDemo");
        setContentView(R.layout.main_activity);
    }
    
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      //Used to put dark icons on light action bar
      boolean isLight = MainActivity.THEME == R.style.Theme_Sherlock_Light;
      
      menu.add("Save")
          .setIcon(isLight ? R.drawable.ic_compose_inverse : R.drawable.ic_compose)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

      menu.add("Search")
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

      menu.add("Refresh")
          .setIcon(isLight ? R.drawable.ic_refresh_inverse : R.drawable.ic_refresh)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

      return true;
  }    
}