package com.itoxygen.publicsafety;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivityList extends ListActivity {

	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	private ImageButton switchView, sortAlpha;
	boolean isSorted = false;

	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		myPath = (TextView)findViewById(R.id.path);
		root = Environment.getExternalStorageDirectory().getPath();
		getDir(root);

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
					getDir(myPath.getText().toString());
					populate();
					sortAlpha.setBackgroundColor(Color.GRAY);
					isSorted = false;
				}
				else {
					getDir(myPath.getText().toString());
					Collections.sort(item);
					Collections.sort(path);
					populate();
					sortAlpha.setBackgroundColor(Color.DKGRAY);
					isSorted = true;
				}
			}	
		});

	}

	private void getDir(String dirPath)
	{
		myPath.setText(dirPath);
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

	private void populate() {
		ArrayAdapter<String> fileList =
				new ArrayAdapter<String>(this, R.layout.row, item);
		setListAdapter(fileList); 
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		File file = new File(path.get(position));

		if (file.isDirectory())
		{
			//if(isNetworkAvailable()) { new DownloadFilesTask().execute("GPS", null, null); } 

			if(file.canRead()){
				getDir(path.get(position));
			}else{
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", null).show(); 
			} 
		}else {
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("[" + file.getName() + "]")
			.setPositiveButton("OK", null).show();

		}
	}

}