package com.xkdev.justtexteditor.util;


import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Util {

    final static String TAG = "MyLogs";

    //Метод для открытия файла в режиме редактирования
    public static void openFileEditSD(String filePath, EditText editText){



        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }

        File sdFile = new File(filePath);

        try{
            BufferedReader bfReader = new BufferedReader(new FileReader(sdFile));
            String str;
            StringBuilder sBuilder = new StringBuilder();
            while((str = bfReader.readLine()) != null){
                sBuilder.append(str).append("\n");
            }
            editText.setText(sBuilder.toString());
            bfReader.close();

            Log.d(TAG, "Открыт файл : " + sdFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    public static String setTitleToActionBar(String filePath){

       String titleActBar = filePath.replaceAll("/storage/emulated/0/Editor/MyFiles/", "");
        int pos = titleActBar.lastIndexOf(".");
        if(pos > 0) {
            titleActBar = titleActBar.substring(0, pos);
        }
        return titleActBar;
    }
}
