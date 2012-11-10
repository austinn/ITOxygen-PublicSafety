package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class Main extends Activity {
	/** Called when the activity is first created. */

	EditText alarmLabel;
	ArrayList<String> item = new ArrayList<String>();
	Dialog alarmDialog;
	ArrayAdapter<String> listAdapter;
	ListView list;
	private List<String> path = null;
	private String root;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getApplication().setTheme(R.style.LightTheme);
		setContentView(R.layout.activity_main);

		root = Environment.getExternalStorageDirectory().getPath(); //gets the root path of SD card
		if(root != null) { getDir(root); } 

		MyPagerAdapter adapter = new MyPagerAdapter();
		ViewPager myPager = (ViewPager) findViewById(R.id.threepageviewer);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
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

	public void populateList(View v) {		
		list.setAdapter(listAdapter);
	}

	////////////////////////////////////////SWIPE NAVIGATION STUFF///////////////////////////////////////////////////////////////
	/**
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		/**
		 * Returns how many pages on the main Activity
		 */
		public int getCount() {
			return 2; //increment this if adding pages
		}

		public Object instantiateItem(View collection, int position) {

			LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			/*
			 * Add your layouts here
			 */
			int resId = 0;
			switch (position) {
			case 0:
				resId = R.layout.activity_main_list;
				break;
			case 1:
				resId = R.layout.activity_main_tile;
				break;
			}

			View view = inflater.inflate(resId, null);
			((ViewPager) collection).addView(view, 0);

			list = (ListView)findViewById(R.id.list);
			listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, item);


			return view;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) { ((ViewPager) arg0).removeView((View) arg2); }
		public void finishUpdate(View arg0) { }
		public boolean isViewFromObject(View arg0, Object arg1) { return arg0 == ((View) arg1); }
		public void restoreState(Parcelable arg0, ClassLoader arg1) { }
		public Parcelable saveState() { return null; }
		public void startUpdate(View arg0) { }

	}

}