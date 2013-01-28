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
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	public List<String> item = null; 
	public List<String> path = null;
	public List<String> history = new ArrayList<String>(); //list of previously clicked on file paths 
	//public Spinner historySpinner;
	LinearLayout layout;
	public String root;
	public ImageButton switchView,historyButton,upDir,rootButton, sortAlpha;
	boolean isSorted, isTile, isHistory;

	//screen
	int width,height;
	Display display;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_tile);
		//screen
		display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		//historySpinner = (Spinner)findViewById(R.id.historySpinner);

		//buttons
		sortAlpha = (ImageButton)findViewById(R.id.sortAlpha);
		switchView = (ImageButton)findViewById(R.id.switchView);
		historyButton = (ImageButton)findViewById(R.id.historyButton);
		upDir = (ImageButton)findViewById(R.id.upButton);
		rootButton=(ImageButton)findViewById(R.id.rootButton);


		sortAlpha.setMinimumWidth(width/5);
		switchView.setMinimumWidth(width/5);
		historyButton.setMinimumWidth(width/5);
		upDir.setMinimumWidth(width/5);
		rootButton.setMinimumWidth(width/5);

		root = Environment.getExternalStorageDirectory().getPath(); //gets the root path of SD card

		loadSharedPrefs();
		if(!isTile) {
			saveSharedPrefs("Activity");
			Intent intent = new Intent(getApplicationContext(), MainActivityList.class);
			finish();
			startActivity(intent);
		}
		else {
			isTile = true;
		}

		if(root != null) { 
			getDir(root); 
		} 
		checkSort();
		
		Log.e("LOOK", root);


		//button calls
		//when the up button is pressed
		upDir.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {


				//Check to see if you are at the root
				if(root !=null){
					getDir(history.get(history.size()-1));
				}
				else
					Log.e(root, "Made it here");

				checkSort();
			}	
		});

		//when the root button is pressed
		rootButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {

				getDir(root);
				checkSort();
			}	
		});


		//when the history button is pressed
		historyButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//populate according to whats in the history
				Log.e("root","History button is pushed");
			}	
		});

		//switches from tile view to list view
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				isTile = false;
				saveSharedPrefs("Activity");
				Intent intent = new Intent(getApplicationContext(), MainActivityList.class);
				finish();
				startActivity(intent);
			}	
		});

		//switches between ascending and descending sort
		sortAlpha.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(isSorted) {
					isSorted = false; //global boolean
				}
				else {
					isSorted = true; //global boolean
				}
				saveSharedPrefs("Alpha");
				checkSort();
			}	
		});

		//		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		//			public void onItemSelected(AdapterView<?> arg0, View arg1,
		//					int pos, long arg3) {
		//				//getDir(historySpinner.getItemAtPosition(pos).toString());
		//				if(historySpinner.getItemAtPosition(pos).toString().equals("Clear History")) {
		//					history.clear(); //if clear history "button" is pressed, clear the spinner
		//					history.add("Clear History"); //re-adds the "button"
		//				}
		//			}
		//			public void onNothingSelected(AdapterView<?> arg0) { }
		//		});

	}

	/**
	 * Does the ascending and descending sorting and
	 * changes the color of the button/text
	 */
	protected void checkSort() {

		if (isSorted) {
			//Alpha Sort
			Collections.sort(item, String.CASE_INSENSITIVE_ORDER); //sorts the filenames
			Collections.sort(path, String.CASE_INSENSITIVE_ORDER); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.ic_media_next);
		}
		else {	
			//Reverse Alpha Sort
			Collections.sort(item, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the filenames
			Collections.sort(path, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.ic_media_previous);
		}
		populate(); //puts buttons on screen
	}
	/**
	 * Saves SharedPreferences
	 * @param name - name of the key in sharedPrefs
	 */
	public void saveSharedPrefs(String name) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (name.equals("Alpha")) {
			editor.putBoolean(name, isSorted);
		}
		else if (name.equals("Activity")){
			editor.putBoolean(name, isTile);
		}
		else if (name.equals("History")){
			editor.putBoolean(name,isHistory);
		}
		editor.commit();
	}

	/**
	 * Loads from SharedPreferences
	 */
	public void loadSharedPrefs() {
		SharedPreferences loadPrefs = getSharedPreferences(PREFS_NAME, 0);
		isSorted = loadPrefs.getBoolean("Alpha", true);
		isTile = loadPrefs.getBoolean("Activity", false);
		isHistory = loadPrefs.getBoolean("History",false);
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
			
			//the two items need to be removed			
			
			//item.add(root); //adds the root to the file directory
			//path.add(root); //adds the root to the history spinner
			//item.add("../"); //adds an "up" button to go up one folder
			//path.add(f.getParent()); 	
		}

		for(int i=0; i < files.length; i++) { //iterate thru the files
			File file = files[i];
			if(!file.isHidden() && file.canRead()) { 
				path.add(file.getPath()); 
				if(file.isDirectory()) {
					item.add(file.getName() + "/"); //if the item is a folder
					//boolean for if it is a folder or not, used to
					//know when to display a folder icon or file icon
				} 
				else {
					item.add(file.getName()); //if the item is a file
				}
			}	
		}//end for loop

	}//end of get Dir method

	public void goToParent(View v) {
		File file = new File(path.get(v.getId()));
		if (file.isDirectory()) {

			if(file.canRead()){
				getDir(path.get(v.getId()));
				checkSort();
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

		int rotation = display.getRotation();
		int columNum = 0;

		columNum = width/180;
		int rowNum = item.size()/columNum;
		if(item.size()%columNum != 0)
			rowNum++;

		for(int i = 0; i < rowNum; i++){		
			LinearLayout imgRow = new LinearLayout(getApplicationContext());
			imgRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			LinearLayout textRow = new LinearLayout(getApplicationContext());
			textRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			for(int j = 0; j < columNum; j++){
				if(j+(i*columNum) < item.size()){
					ImageButton imgBtn = new ImageButton(this);

					imgBtn.setBackgroundDrawable(null);
					imgBtn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					imgBtn.setMinimumWidth(width/columNum);
					imgBtn.setMinimumHeight(width/columNum);
					imgBtn.setId(j + (i * columNum));

					File file = new File(path.get(j+(i*columNum)));
					if(file.isDirectory()) {
						imgBtn.setImageResource(R.drawable.psafety_folder);
					}
					else {
						if (file.getName().contains(".mp3"))
							imgBtn.setImageResource(R.drawable.psafety_mp3);
						else
							imgBtn.setImageResource(R.drawable.psafety_file);
						//						if(file.getName().contains(".pdf")) { imgBtn.setImageResource(R.drawable.pdf); }
						//						else if(file.getName().contains(".mp3")) { imgBtn.setImageResource(R.drawable.mp3); }
						//						else if(file.getName().contains(".apk")) { imgBtn.setImageResource(R.drawable.apk); }
					}


					imgBtn.setOnClickListener(new ClickListener());
					TextView btn = new TextView(this);
					btn.setText(item.get(j+(i*columNum)));
					btn.setTextSize(14);

					btn.setGravity(Gravity.CENTER_HORIZONTAL);

					btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					btn.setWidth(width/columNum);
					btn.setId(j + (i * columNum));
					btn.setOnClickListener(new ClickListener());

					imgRow.addView(imgBtn);
					textRow.addView(btn);
				}
			}
			layout.addView(imgRow);
			layout.addView(textRow);
		}

		ArrayAdapter<String> historyList =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		//		historySpinner.setAdapter(historyList); //sets the history arraylist to populate the spinner
		//		historySpinner.setSelection(history.size()-1); //sets the spinner to display the history correctly

	}


	/**
	 * Class that is called when a folder is clicked
	 */
	class ClickListener implements OnClickListener {
		public void onClick(View v) {
			Log.e(root, "here");

			File file = new File(path.get(v.getId()));
			Log.e("File Extension:", file.getName());
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
		}//end on Click method
	}//End ClickListner Class


}

