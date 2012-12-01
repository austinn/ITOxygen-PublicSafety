package com.itoxygen.publicsafety;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Shared extends Activity {
	public static void openPdf(File file, Activity activity) {
		Uri targetUri = Uri.fromFile(file);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(targetUri, "application/pdf");
		activity.startActivity(intent);
	}
}
