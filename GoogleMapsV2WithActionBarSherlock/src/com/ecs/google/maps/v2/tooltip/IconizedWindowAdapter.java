package com.ecs.google.maps.v2.tooltip;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class IconizedWindowAdapter implements InfoWindowAdapter {
  LayoutInflater inflater=null;

  public IconizedWindowAdapter(LayoutInflater inflater) {
    this.inflater=inflater;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return(null);
  }

  @Override
  public View getInfoContents(Marker marker) {
    View popup=inflater.inflate(R.layout.marker_tooltip, null);

    TextView tv=(TextView)popup.findViewById(R.id.title);

    tv.setText(marker.getTitle());
    tv=(TextView)popup.findViewById(R.id.snippet);
    tv.setText(marker.getSnippet());

    return(popup);
  }
}