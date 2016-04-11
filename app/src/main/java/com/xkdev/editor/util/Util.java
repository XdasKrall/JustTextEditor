package com.xkdev.editor.util;


import android.content.Context;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dfomichev on 11.04.2016.
 */
public class Util {


    //Метод для открытия файла в режиме редактирования
    public static void openFileEditSD(String filePath, Context context, EditText editText){

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return;
        }

        File sdFile = new File(filePath);

        try{
            BufferedReader bfReader = new BufferedReader(new FileReader(sdFile));
            String str;
            StringBuilder sBuilder = new StringBuilder();
            while((str = bfReader.readLine()) != null){
                sBuilder.append(str + "\n");
            }
            editText.setText(sBuilder.toString());

            Toast.makeText(context, "Открыто: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
