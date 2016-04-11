package com.xkdev.editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 06.04.2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DIR_SD = "Editor/MyFiles";
    private static final int PICKFILE_CODE = 1;
    CheckBox chbRead;
    Intent intent;
    private EditText mETFileName;
    private SharedPreferences sPref;
    private String Logs;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        chbRead = (CheckBox) findViewById(R.id.chbRead);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreate:
                createFileSD();
                break;
            case R.id.btnOpen:
                openFileManager();
                if(!chbRead.isChecked()){
                    intent = new Intent(this, EditActivity.class);
                    intent.putExtra("filepath", filePath);
                    startActivity(intent);
                }
                else{
                    intent = new Intent(this, ReadActivity.class);
                    intent.putExtra("filepath", filePath);
                    startActivity(intent);
                }
                break;

            case R.id.btnExit:
                this.finish();
                break;
        }
    }
    //Метод для создания нового файла
    public void createFileSD(){
        mETFileName = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Введите имя файла");
        builder.setView(mETFileName);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mETFileName != null){
                    String fileName = mETFileName.getText().toString() + ".txt";
                    File sdPath = Environment.getExternalStorageDirectory();
                    sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
                    writeEmptyFileSD(sdPath + "/" + fileName);

                }
            }
        });
        builder.setCancelable(true);
        builder.show();
    }
    //Метод для записи файла на sd card
    public void writeEmptyFileSD(String filePath){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(Logs, "SD карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File sdFile = new File(filePath);

        try{
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(sdFile));
            bWriter.write("");
            bWriter.close();
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("file",filePath);
            ed.commit();
            Toast.makeText(getApplicationContext(), R.string.succes_write_SD + sdFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_write_SD + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    //Метод для открытия файлового менеджера
    public void openFileManager()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICKFILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICKFILE_CODE){
            if(data!=null){
                filePath = data.getData().getPath();
            }
            else return;
        }
        else
            return;
        if(resultCode == RESULT_CANCELED){
            return;
        }
    }
}
