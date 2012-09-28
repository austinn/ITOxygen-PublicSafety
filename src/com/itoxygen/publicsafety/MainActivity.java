package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private List<String> item = null;
	private List<String> path = null;
	private List<String> history = new ArrayList<String>();
	private Spinner historySpinner;
	LinearLayout layout;
	private String root;
	//private TextView myPath;
	private ImageButton switchView, sortAlpha;
	boolean isSorted = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		historySpinner = (Spinner)findViewById(R.id.historySpinner);
		//myPath = (TextView)findViewById(R.id.path);
		root = Environment.getExternalStorageDirectory().getPath();
		if(root != null) { getDir(root); }
		populate();

		switchView = (ImageButton)findViewById(R.id.switchView);
		sortAlpha = (ImageButton)findViewById(R.id.sortAlpha);
		switchView.setBackgroundColor(Color.GRAY);
		sortAlpha.setBackgroundColor(Color.GRAY);
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivityList.class);
				finish();
				startActivity(intent);
			}	
		});

		sortAlpha.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if (isSorted) {
					getDir(history.get(history.size()-1).toString());
					populate();
					sortAlpha.setBackgroundColor(Color.GRAY);
					isSorted = false;
				}
				else {
					getDir(history.get(history.size()-1).toString());
					Collections.sort(item);
					Collections.sort(path);
					populate();
					sortAlpha.setBackgroundColor(Color.DKGRAY);
					isSorted = true;
				}
			}	
		});
		
		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				getDir(historySpinner.getItemAtPosition(pos).toString());
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});

	}


	private void getDir(String dirPath) {
		//myPath.setText(dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if(!dirPath.equals(root)) {

			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());	
		}

		for(int i=0; i < files.length; i++) {
			File file = files[i];

			if(!file.isHidden() && file.canRead()) { 
				path.add(file.getPath());
				if(file.isDirectory()) {
					item.add(file.getName() + "/");
				} else {
					item.add(file.getName());
				}
			}	
		}

	}


	private void populate() {

		layout = (LinearLayout) findViewById(R.id.lin);
		layout.removeAllViews();
		for (int i = 0; i < 5; i++) {
			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			Button btnTag = null;
			Button tv = null;
			for (int j = 0; j < 6; j++) {
				if(j+(i*6) < item.size()) {
					btnTag = new Button(this);
					tv = new Button(this);
					tv.setText("hashdfa;sdfa");
					tv.setBackgroundDrawable(null);
					btnTag.setText(item.get(j + (i * 6)));
					if(btnTag.getText().equals("../")) {btnTag.setText("Up...");}
					btnTag.setTextSize(14);
					btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					btnTag.setHeight(150);
					btnTag.setWidth(150);
					btnTag.setId(j + (i * 6));
					btnTag.setOnClickListener(new ClickListener());
					row.addView(btnTag);
					
				}
				else { }
			}
			layout.addView(row);
		}
		
		ArrayAdapter<String> historyList =
				new ArrayAdapter(this, android.R.layout.simple_spinner_item, history);
		historySpinner.setAdapter(historyList);
		historySpinner.setSelection(history.size()-1);
		
	}



	/**
	 * Class that is called when a button from the commandDialog is pressed
	 *
	 */
	class ClickListener implements OnClickListener {
		public void onClick(View v) {

			File file = new File(path.get(v.getId()));
			history.add(path.get(v.getId()));
			if (file.isDirectory()) {
				if(file.canRead()){
					getDir(path.get(v.getId()));
					if(isSorted) {
						Collections.sort(item);
						Collections.sort(path);
					}
					populate();
				}
				else{ }	
			}
			else { }
		}
	}
}

