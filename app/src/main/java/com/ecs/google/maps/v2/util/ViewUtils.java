package com.ecs.google.maps.v2.util;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ViewUtils {

	private static final int DEFAULT_DIMENSION = 16;
	
	public static void initializeMargin(Context ctx, View root) {
		initializeMargin(ctx, root, DEFAULT_DIMENSION);
	}
	public static void initializeMargin(Context ctx, View root, float dimension) {
		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension, ctx.getResources().getDisplayMetrics());

		root.setPadding(margin, margin, margin, margin);
		
		 FrameLayout frameLayout = new FrameLayout(ctx);
		    frameLayout.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
		    ((ViewGroup) root).addView(frameLayout,
		        new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
}
