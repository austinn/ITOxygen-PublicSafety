package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class MainActivity extends Activity {

	public List<String> item = null; 
	public List<String> path = null;
	public List<String> history = new ArrayList<String>(); //list of previously clicked on file paths 
	public Spinner historySpinner;
	LinearLayout layout;
	public String root;
	public ImageButton switchView, sortAlpha;
	boolean isSorted = true;
	// Access the default SharedPreferences
	SharedPreferences preferences;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		// Remove the standard action bar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_main_tile); 
		historySpinner = (Spinner)findViewById(R.id.historySpinner);
		sortAlpha = (ImageButton)findViewById(R.id.sortAlpha);
		sortAlpha.setBackgroundColor(Color.GRAY);
		root = Environment.getExternalStorageDirectory().getPath(); //gets the root path of SD card
		history.add("Clear History"); //adds a "button" to clear history
		if(root != null) { getDir(root); } 
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		isSorted = preferences.getBoolean("Alpha", isSorted); //gets the boolean from SharedPrefs
		checkSort();
		switchView = (ImageButton)findViewById(R.id.switchView);
		switchView.setBackgroundColor(Color.GRAY);
		

        
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivityList.class);
				finish();
				startActivity(intent);
			}	
		});

		sortAlpha.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				checkSort();
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

	protected void checkSort() {
		if(history.size() <= 1) {
			getDir(root);
		}
		else {
			getDir(history.get(history.size()-1).toString());
		}
		if (isSorted) {
			
			
			Collections.sort(item, String.CASE_INSENSITIVE_ORDER); //sorts the filenames
			Collections.sort(path, String.CASE_INSENSITIVE_ORDER); //sorts the spinner
			sortAlpha.setBackgroundColor(Color.DKGRAY);
			isSorted = false; //global boolean
			
			
		}//Alpha
		else {					
			Collections.sort(item, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the filenames
			Collections.sort(path, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the spinner
			sortAlpha.setBackgroundColor(Color.GRAY);
			isSorted = true; //global boolean
		}//reverse alpha
		
		SharedPreferences.Editor editor = preferences.edit(); // The SharedPreferences editor - must use commit() to submit changes
		editor.putBoolean("Alpha", isSorted); // Edit the saved preferences
		editor.commit();
		populate(); //puts buttons on screen
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
	
	public void goToParent(View v) {
		File file = new File(path.get(v.getId()));
		if (file.isDirectory()) {
			if(file.canRead()){
				getDir(path.get(v.getId()));
				if(isSorted) {
					Collections.sort(item, String.CASE_INSENSITIVE_ORDER);
					Collections.sort(path, String.CASE_INSENSITIVE_ORDER);
				}
				populate();
			}
			else{ 
				new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", null).show(); 
			}	
		} else {
			//this should not happen
		}
	}

	/**
	 * Helper method that sets up all the buttons
	 * Creates 5 rows
	 */
	private void populate() {

		layout = (LinearLayout) findViewById(R.id.lin);
		layout.removeAllViews();
		for (int i = 0; i < 5; i++) { //Columns
			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			Button btnTag = null;
			Button tv = null;
			for (int j = 0; j < 6; j++) { 
				if(j+(i*6) < item.size()) {
					btnTag = new Button(this);
					tv = new Button(this);
					tv.setText("");
					tv.setBackgroundDrawable(null);
					btnTag.setText(item.get(j + (i * 6)));
					btnTag.setTextSize(14);
					btnTag.setGravity(0);
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
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		historySpinner.setAdapter(historyList); //sets the history arraylist to populate the spinner
		historySpinner.setSelection(history.size()-1); //sets the spinner to display the history correctly

	}


	/**
	 * Class that is called when a button from the commandDialog is pressed
	 */
	class ClickListener implements OnClickListener {
		public void onClick(View v) {
			Log.v("Click Occurs", "This is a click");
			File file = new File(path.get(v.getId()));
			history.add(path.get(v.getId()));
			if (file.isDirectory()) {
				if(file.canRead()){
					getDir(path.get(v.getId()));
					if(isSorted) {
						Collections.sort(item, String.CASE_INSENSITIVE_ORDER);
						Collections.sort(path, String.CASE_INSENSITIVE_ORDER);
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
				//check file type
				Shared.openPdf(file, MainActivity.this);
				/*new AlertDialog.Builder(MainActivity.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "]")
				.setPositiveButton("OK", null).show();*/
			}
		}
	}
	

}

