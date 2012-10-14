package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
	private List<String> item = null; 
	private List<String> path = null;
	private List<String> history = new ArrayList<String>(); //list of previously clicked on file paths 
	private Spinner historySpinner;
	LinearLayout layout;
	private String root;
	private ImageButton switchView, sortAlpha;
	boolean isSorted = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		historySpinner = (Spinner)findViewById(R.id.historySpinner);
		root = Environment.getExternalStorageDirectory().getPath(); //gets the root path of SD card
		history.add("Clear History"); //adds a "button" to clear history
		if(root != null) { getDir(root); } 
		populate(); //puts buttons on screen

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
				if(history.size() <= 1) {
					getDir(root);
				}
				else {
					getDir(history.get(history.size()-1).toString());
				}
				if (isSorted) {
					populate(); //puts buttons on screen
					sortAlpha.setBackgroundColor(Color.GRAY);
					isSorted = false; //global boolean
				}
				else {
					Collections.sort(item); //sorts the filenames
					Collections.sort(path); //sorts the spinner
					populate(); //puts buttons on screen
					sortAlpha.setBackgroundColor(Color.DKGRAY);
					isSorted = true; //global boolean
				}
			}	
		});

		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				//getDir(historySpinner.getItemAtPosition(pos).toString());
				if(historySpinner.getItemAtPosition(pos).toString().equals("Clear History")) {
					history.clear(); //if clear history "button" is pressed, clear the spinner
					history.add("Clear History"); //re-adds the "button"
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});

	}

	/**
	 * Method that gets the directory of the given path
	 * @param dirPath - string of the specified path
	 */
	private void getDir(String dirPath) {
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if(!dirPath.equals(root)) {
			item.add(root); //adds the root to the file directory
			path.add(root); //adds the root to the history spinner
			item.add("../"); //adds an "up" button to go up one folder
			path.add(f.getParent()); 	
		}

		for(int i=0; i < files.length; i++) { //iterate thru the files
			File file = files[i];
			if(!file.isHidden() && file.canRead()) { 
				path.add(file.getPath()); 
				if(file.isDirectory()) {
					item.add(file.getName() + "/"); //if the item is a folder
				} else {
					item.add(file.getName()); //if the item is a file
				}
			}	
		}

	}

	/**
	 * Helper method that sets up all the buttons
	 * Creates 5 rows
	 */
	private void populate() {
		layout = (LinearLayout) findViewById(R.id.lin);
		layout.removeAllViews();
		// finds the width and height of the screen in pixels 
		// files.length = # files
		int lWidth = 0;
		int lHeight = 0;
		Display display = getWindowManager().getDefaultDisplay();
		try{
			Point size = new Point();
			display.getSize(size); // Suppressed for API 13+
			lWidth = size.x;
			lHeight = size.y;
		}catch( NoSuchMethodError nsmError ){
			lWidth = display.getWidth();
			lHeight = display.getHeight();
		}
		lWidth -= 150; // HardCoded menu bar size
		int lIconsX = lWidth / 150;  // HardCoded estimation
		
		// adjust
		for ( int lNumInCol = 0; lNumInCol < 150; lNumInCol++ ) { // 150 is an arbitrary large number
			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			Button btnTag = null;
			Button tv = null;
			for ( int lNumInRow = 0; lNumInRow < lIconsX; lNumInRow++ ) { 
				if( lNumInRow + ( lNumInCol * lIconsX ) < item.size() ) {
					btnTag = new Button(this);
					tv = new Button(this);
					tv.setText("");
					tv.setBackgroundDrawable(null);
					btnTag.setText(item.get(lNumInRow + (lNumInCol * lIconsX)));
					btnTag.setTextSize(14);
					btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					btnTag.setHeight(150);
					btnTag.setWidth(150);
					btnTag.setId(lNumInRow + (lNumInCol * 6));
					btnTag.setOnClickListener(new ClickListener());
					row.addView(btnTag);
				}
				else { }
			}
			layout.addView(row);
		}

		ArrayAdapter<String> historyList =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		historySpinner.setAdapter(historyList); //sets the history arraylist to populate the spinner
		historySpinner.setSelection(history.size()-1); //sets the spinner to display the history correctly

	}
	
	/**
	 * Class that is called when a button from the commandDialog is pressed
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
				else{ 
					new AlertDialog.Builder(MainActivity.this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] folder can't be read!")
					.setPositiveButton("OK", null).show(); 
				}	
			}
			else { 
				new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "]")
				.setPositiveButton("OK", null).show();
			}
		}
	}
}

