package com.itoxygen.publicsafety;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;

public class ActionBar extends LinearLayout implements OnClickListener {

	public ActionBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ActionBar(Context context, AttributeSet attrs) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	protected void inFinishInflate() {
		super.onFinishInflate();
		findViewById(R.id.action_bar_button_up).setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.action_bar_button_up:
			//do something
			Log.d("On click for action bar", "Action Bar Click Occured");
			break;
			default:
			break;
		}
	}
	
	

}
