package com.ecs.google.maps.v2.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtils {

	public static final void writeToFile(String fileName, String txt,Context context) {
		try {
			
			txt += "\n";
			
		    FileOutputStream fos = context.openFileOutput(fileName,
		            Context.MODE_APPEND | Context.MODE_WORLD_READABLE);
		    fos.write(txt.toString().getBytes());
		    fos.close();
		 
		    String storageState = Environment.getExternalStorageState();
		    
		    if (storageState.equals(Environment.MEDIA_MOUNTED)) {
		        File file = new File(context.getExternalFilesDir(null),
		                fileName);
		        FileOutputStream fos2 = new FileOutputStream(file,true);
		        fos2.write(txt.toString().getBytes());
		        fos2.close();
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}		
	
}
