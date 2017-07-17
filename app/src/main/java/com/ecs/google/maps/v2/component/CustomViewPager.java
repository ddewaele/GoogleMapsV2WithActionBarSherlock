package com.ecs.google.maps.v2.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {

    	// When using Maps V1    	
//        if(v instanceof MapView){
//            return true;
//        }
//        return super.canScroll(v, checkV, dx, x, y);
    	
    	// When using Maps V2
    	if (v !=null && v.getClass()!=null || v.getClass().getPackage().getName().startsWith("maps.")) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);    	
    }

}