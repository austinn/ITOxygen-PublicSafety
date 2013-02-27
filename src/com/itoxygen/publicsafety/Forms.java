package com.itoxygen.publicsafety;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class Forms extends Activity{

	ImageButton crimescene;
	ImageButton home;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.forms);
		
		home = (ImageButton)findViewById(R.id.rootButton);
		home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				finish();
				startActivity(intent);
			}
		});
		
		
		
		crimescene = (ImageButton)findViewById(R.id.crimescene_form);
		crimescene.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CrimeSceneForm.class);
				finish();
				startActivity(intent);
			}
		});
		
		
	}

}
