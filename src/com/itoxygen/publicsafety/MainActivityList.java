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
import android.view.View;
import android.view.View.OnClickListener;
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
	public ImageButton switchView;
	public Button sortAlpha;
	boolean isSorted = true;
	public Spinner historySpinner;
	public ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_list);

		list = (ListView)findViewById(R.id.list);
		historySpinner = (Spinner)findViewById(R.id.historySpinner);
		sortAlpha = (Button)findViewById(R.id.sortAlpha);
		switchView = (ImageButton)findViewById(R.id.switchView);
		switchView.setBackgroundColor(Color.GRAY);
		loadSharedPrefs();
		root = Environment.getExternalStorageDirectory().getPath(); //gets the root of the SD card or Internal Storage
		history.add("Clear History"); //adds a clear history "button"
		if(root != null) { getDir(root); }
		checkSort();


		//when the list views button is pushed
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				finish();
				startActivity(intent);
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

		ArrayAdapter<String> historyList =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		historySpinner.setAdapter(historyList);
		historySpinner.setSelection(history.size()-1);

		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				//getDir(historySpinner.getItemAtPosition(pos).toString());
				if(historySpinner.getItemAtPosition(pos).toString().equals("Clear History")) {
					history.clear();
					history.add("Clear History");
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
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
			sortAlpha.setText("A > Z");
			sortAlpha.setBackgroundColor(Color.BLACK);
			sortAlpha.setTextColor(Color.YELLOW);
		}
		else {	
			//Reverse Alpha Sort
			Collections.sort(item, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the filenames
			Collections.sort(path, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the spinner
			sortAlpha.setBackgroundColor(Color.YELLOW);
			sortAlpha.setText("Z > A");
			sortAlpha.setTextColor(Color.BLACK);
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
		editor.putBoolean(name, isSorted);
		editor.commit();
	}

	/**
	 * Loads from SharedPreferences
	 */
	public void loadSharedPrefs() {
		SharedPreferences loadPrefs = getSharedPreferences(PREFS_NAME, 0);
		isSorted = loadPrefs.getBoolean("Alpha", true);
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