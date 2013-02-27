package com.itoxygen.publicsafety;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String PREFS_NAME = "MyPrefsFile";
	public List<String> item = null; 
	public List<String> path = null;
	public List<String> historyPath = new ArrayList<String>();
	public List<String> historyItem = new ArrayList<String>();//list of previously clicked on file paths
	LinearLayout layout;
	public String root, parentOfLastPressed, top;
	public ImageButton switchView,historyButton,upDir,rootButton, sortAlpha;
	boolean isSorted = false, isTile, isHistory = false;
	int indexOfLastPressed;

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
		File l = new File(root);
		top = l.getParent();
		parentOfLastPressed = top;
		
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
		checkSort(path,item);

		//button calls
		//when the up button is pressed
		upDir.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) 
			{
				Log.e("isHistory",isHistory+"");
				if(isHistory){
					isHistory = false;
					getDir(root);
					checkSort(path,item);
				}
				else {
					if(!parentOfLastPressed.equals(top)){
						File file = new File(parentOfLastPressed);
						//history.add(path.get(v.getId()));			
						if (file.isDirectory()) {
							if(file.canRead()){
								getDir(file.getPath());//updates the "path" and "item" lists					
								if(isSorted) {
									Collections.sort(item, String.CASE_INSENSITIVE_ORDER);
									Collections.sort(path, String.CASE_INSENSITIVE_ORDER);
								}
								populate(path, item);
							}
							else{ 
								new AlertDialog.Builder(MainActivity.this)
								.setIcon(R.drawable.ic_launcher)
								.setTitle("[" + file.getName() + "] folder can't be read!")
								.setPositiveButton("OK", null).show();
							}	
						}
						else { //opens the file if it isn't a directory
							//check file type
							Shared.openPdf(file, MainActivity.this);				
						}
						isHistory = false;
						parentOfLastPressed = file.getParent();
					}
				}
			}
		});
		//when the root button is pressed
		rootButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				getDir(root);
				checkSort(path,item);
			}	
		});
		//when the history button is pressed
		historyButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				//populate according to whats in the history
				Log.e("root","History button is pushed");
				if(historyItem.size() != 0){
					isHistory = true;
					HashSet hs = new HashSet();
					hs.addAll(historyItem);
					historyItem.clear();
					historyItem.addAll(hs);
					populate(historyPath,historyItem);
				} else {
					Toast.makeText(getApplicationContext(), "Nothing In History", Toast.LENGTH_SHORT).show();
				}

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
				if(isHistory)
					checkSort(historyPath, historyItem);
				else
					checkSort(path, item);
			}	
		});
	}

	/**
	 * When physical Back Button is pressed
	 */
	public void onBackPressed() {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Are You Sure?");
		dialog.setCancelable(true);
		Button confirm = (Button)dialog.findViewById(R.id.confirmExit);
		Button decline = (Button)dialog.findViewById(R.id.declineExit);
		confirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		decline.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.hide();
			}
		});

		dialog.show();
		return;
	}

	/**
	 * Does the ascending and descending sorting and
	 * changes the color of the button/text
	 * @param names 
	 * @param paths 
	 */
	protected void checkSort(List<String> paths, List<String> names) {
		if (isSorted) {
			//Alpha Sort
			Collections.sort(names, String.CASE_INSENSITIVE_ORDER); //sorts the filenames
			Collections.sort(paths, String.CASE_INSENSITIVE_ORDER); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.az_sort);
		}
		else {	
			//Reverse Alpha Sort
			Collections.sort(names, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the filenames
			Collections.sort(paths, Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)); //sorts the spinner
			sortAlpha.setImageResource(R.drawable.za_sort);
		}
		populate(paths, names); //puts buttons on screen
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

		SharedPreferences historyPathPrefs = getApplicationContext().getSharedPreferences("historyPath", Context.MODE_PRIVATE);
		SharedPreferences historyItemPrefs = getApplicationContext().getSharedPreferences("historyItem", Context.MODE_PRIVATE);
		historyPath.clear();
		historyPath.clear();

		int spiritsPathSize = historyPathPrefs.getInt("History_size", 0);
		int spiritsItemZize = historyItemPrefs.getInt("History_size", 0);

		for(int i = 0; i < historyPath.size(); i++) {
			historyPath.add(historyPathPrefs.getString("pathHistory_" + i, null));
			historyItem.add(historyItemPrefs.getString("itemHistory_" + i, null));
		}
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

		for(int i=0; i < files.length; i++) { //iterate thru the files
			File file = files[i];
			if(!file.isHidden() && file.canRead()) { 
				path.add(file.getPath()); 
				item.add(file.getName());
			}	
		}//end for loop

	}//end of getDir method

	/**
	 * Helper method that sets up all the buttons
	 * Creates 5 rows
	 * @param list 
	 * @param  
	 */
	private void populate(List<String> paths, List<String> names ) {
		layout = (LinearLayout) findViewById(R.id.lin);
		layout.removeAllViews();

		int rotation = display.getRotation();
		int columNum = 0;

		columNum = width/180;
		int rowNum = names.size()/columNum;
		if(names.size()%columNum != 0)
			rowNum++;

		for(int i = 0; i < rowNum; i++){		
			LinearLayout imgRow = new LinearLayout(getApplicationContext());
			imgRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			LinearLayout textRow = new LinearLayout(getApplicationContext());
			textRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			for(int j = 0; j < columNum; j++){
				if(j+(i*columNum) < names.size()){
					ImageButton imgBtn = new ImageButton(this);
					imgBtn.setBackgroundDrawable(null);
					imgBtn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					imgBtn.setMinimumWidth(width/columNum);
					imgBtn.setMinimumHeight(width/columNum);
					imgBtn.setId(j + (i * columNum));
					imgBtn.setTag(names.get(j+(i*columNum)));

					File file = new File(paths.get(j+(i*columNum)));// converts the string in the list back to a file
					if(file.isDirectory()) {
						imgBtn.setImageResource(R.drawable.psafety_folder);
					}
					else {
						if (file.getName().contains(".mp3"))
							imgBtn.setImageResource(R.drawable.psafety_mp3);
						else if (file.getName().contains(".pdf"))
							imgBtn.setImageResource(R.drawable.psafety_file);
						else {
							imgBtn.setImageResource(R.drawable.psafety_file);
						}

					}
					imgBtn.setOnClickListener(new ClickListener());
					TextView btn = new TextView(this);
					btn.setText(names.get(j+(i*columNum)));//sets the text displayed with the button to the corresponding location in item
					btn.setTextSize(14);
					btn.setGravity(Gravity.CENTER_HORIZONTAL);
					btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					btn.setWidth(width/columNum);
					btn.setId(j + (i * columNum));//sets the button ID to the correspondind spot in the array list
					btn.setOnClickListener(new ClickListener());
					imgRow.addView(imgBtn);
					textRow.addView(btn);
				}
			}
			layout.addView(imgRow);
			layout.addView(textRow);
		}
	}


	/**
	 * Class that is called when a folder is clicked
	 */
	class ClickListener implements OnClickListener {
		public void onClick(View v) {

			if(v.getTag().equals("Forms")) {
				Intent intent = new Intent(getApplicationContext(), Forms.class);
				finish();
				startActivity(intent);
			} else {
				File file = new File(path.get(v.getId()));
				parentOfLastPressed = file.getParent();		
				if (file.isDirectory()) {
					if(file.canRead()){
						getDir(path.get(v.getId()));//updates the "path" and "item" lists
						if(isSorted) {
							Collections.sort(item, String.CASE_INSENSITIVE_ORDER);
							Collections.sort(path, String.CASE_INSENSITIVE_ORDER);
						}
						isHistory = false;
						populate(path, item);
					}
					else{ 
						new AlertDialog.Builder(MainActivity.this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle("[" + file.getName() + "] folder can't be read!")
						.setPositiveButton("OK", null).show();
					}	
				}
				else { //opens the file if it isn't a directory
					historyPath.add(file.getPath());
					historyItem.add(file.getName());


					//Save SharedPrefs
					SharedPreferences historyPathFiles = v.getContext().getSharedPreferences("historyPath", Context.MODE_PRIVATE);
					SharedPreferences historyItemFiles = v.getContext().getSharedPreferences("historyItem", Context.MODE_PRIVATE);

					SharedPreferences.Editor pathEditor = historyPathFiles.edit();
					SharedPreferences.Editor itemEditor = historyItemFiles.edit();

					pathEditor.putInt("historyPathSize", historyPath.size());
					itemEditor.putInt("historyItemSize", historyItem.size());

					for(int i = 0; i < historyPath.size(); i ++) {

						pathEditor.remove("pathHistory_" + i);
						pathEditor.putString("pathHistory_" + i, historyPath.get(i));

						itemEditor.remove("itemHistory_" + i);
						itemEditor.putString("itemHistory_" + i, historyItem.get(i));

					}
					pathEditor.commit();
					itemEditor.commit();
					Shared.openPdf(file, MainActivity.this);
				}
			}
		}//end on Click method
	}//End ClickListner Class
}

