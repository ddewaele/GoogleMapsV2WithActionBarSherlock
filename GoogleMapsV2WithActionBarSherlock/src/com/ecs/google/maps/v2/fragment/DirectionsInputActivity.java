package com.ecs.google.maps.v2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ecs.google.maps.v2.actionbarsherlock.R;

public class DirectionsInputActivity extends FragmentActivity {

	protected static final int RESULT_CODE = 123;
	private EditText from;
	private EditText to;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.directions_input);
		
		from = (EditText) findViewById(R.id.from);
		to = (EditText) findViewById(R.id.to);
		Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
		
		from.setText("Brussels, Belgium");
		to.setText("Antwerp, Belgium");
		btnLoadDirections.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("from", from.getText().toString());
				data.putExtra("to", to.getText().toString());
				DirectionsInputActivity.this.setResult(RESULT_CODE, data);
				DirectionsInputActivity.this.finish();
			}
		});
	}
	
	
}
