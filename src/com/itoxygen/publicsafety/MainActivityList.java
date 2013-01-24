package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivityList extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	public List<String> item = null;
	public List<String> path = null;
	public String root;
	public List<String> history = new ArrayList<String>(); //list of previously clicked on file paths 
	public ImageButton switchView,historyButton,upDir,rootButton, sortAlpha;
	boolean isSorted, isTile,isHistory;
	//public Spinner historySpinner;
	public ListView list;

	//screen
	int width,height;
	Display display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_list);

		//screen
		display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		list = (ListView)findViewById(R.id.list);
		//historySpinner = (Spinner)findViewById(R.id.historySpinner);
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

		switchView.setBackgroundColor(Color.GRAY);
		loadSharedPrefs();

		//root = Environment.getExternalStorageDirectory().getPath(); //gets the root path of SD card
		//root = Environment.getExternalStorageDirectory().getPath() + "/root_psafety"; //gets the root path of SD card
		root = Environment.getRootDirectory().getPath();

		history.add("Clear History"); //adds a clear history "button"
		if(root != null) { getDir(root); }
		checkSort();


		//when the up button is pressed
		upDir.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//go up one level
				Log.e("root","Go up one level button pushed");
			}	
		});

		//when the root button is pressed
		rootButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//go up one level
				Log.e("root","Root button pushed");
			}	
		});


		//when the history button is pressed
		historyButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//populate according to whats in the history
				Log.e("root","History button pushed");
			}	
		});

		//when the sorting button is pressed
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


		//when the list views button is pushed
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				isTile = true;
				saveSharedPrefs("Activity");
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				finish();
				startActivity(intent);
			}	
		});

		ArrayAdapter<String> historyList =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		//		historySpinner.setAdapter(historyList);
		//		historySpinner.setSelection(history.size()-1);

		//		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		//			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		//				//getDir(historySpinner.getItemAtPosition(pos).toString());
		//				if(historySpinner.getItemAtPosition(pos).toString().equals("Clear History")) {
		//					history.clear();
		//					history.add("Clear History");
		//				}
		//			}
		//			public void onNothingSelected(AdapterView<?> arg0) { }
		//		});

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				File file = new File(path.get(position));
				history.add(path.get(position));
				if (file.isDirectory())
				{ 
					if(file.canRead()){
						getDir(path.get(position));
					} else{

					} 
				} else {
					Shared.openPdf(file, MainActivityList.this);
				}
				checkSort();
			}
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
			//Alpha Sort
			Collections.sort(item, String.CASE_INSENSITIVE_ORDER); //sorts the filenames
			Collections.sort(path, String.CASE_INSENSITIVE_ORDER); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.up);
		}
		else {	
			//Reverse Alpha Sort
			Collections.sort(item, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the filenames
			Collections.sort(path, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.down);
		}
		populate();
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
		editor.commit();
	}

	/**
	 * Loads from SharedPreferences
	 */
	public void loadSharedPrefs() {
		SharedPreferences loadPrefs = getSharedPreferences(PREFS_NAME, 0);
		isSorted = loadPrefs.getBoolean("Alpha", true);
		isTile = loadPrefs.getBoolean("Activity", true);
	}

	private void getDir(String dirPath)
	{
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if(!dirPath.equals(root))
		{
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent()); 
		}

		for(int i=0; i < files.length; i++)
		{
			File file = files[i];

			if(!file.isHidden() && file.canRead()){
				path.add(file.getPath());
				if(file.isDirectory()){
					item.add(file.getName() + "/");
				}else{
					item.add(file.getName());
				}
			} 
		}

		populate();
	}

	/**
	 * 
	 */
	private void populate() {
		ArrayAdapter<String> fileList =
				new ArrayAdapter<String>(this, R.layout.row, item);
		list.setAdapter(fileList); 
	}

}