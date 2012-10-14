package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

public class MainActivityList extends ListActivity {

	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private List<String> history = new ArrayList<String>(); //list of previously clicked on file paths 
	private ImageButton switchView, sortAlpha;
	boolean isSorted = false;
	private Spinner historySpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		historySpinner = (Spinner)findViewById(R.id.historySpinner);
		root = Environment.getExternalStorageDirectory().getPath(); //gets the root of the SD card or Internal Storage
		history.add("Clear History"); //adds a clear history "button"
		if(root != null) { getDir(root); }

		switchView = (ImageButton)findViewById(R.id.switchView);
		sortAlpha = (ImageButton)findViewById(R.id.sortAlpha);
		switchView.setBackgroundColor(Color.GRAY);
		sortAlpha.setBackgroundColor(Color.GRAY);
		switchView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
		
		ArrayAdapter<String> historyList =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, history);
		historySpinner.setAdapter(historyList);
		historySpinner.setSelection(history.size()-1);

		historySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				//getDir(historySpinner.getItemAtPosition(pos).toString());
				if(historySpinner.getItemAtPosition(pos).toString().equals("Clear History")) {
					history.clear();
					history.add("Clear History");
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
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
		setListAdapter(fileList); 
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(path.get(position));
		history.add(path.get(position));
		if (file.isDirectory())
		{ 
			if(file.canRead()){
				getDir(path.get(position));
			} else{
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", null).show(); 
			} 
		} else {
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("[" + file.getName() + "]")
			.setPositiveButton("OK", null).show();
		}
	}

}