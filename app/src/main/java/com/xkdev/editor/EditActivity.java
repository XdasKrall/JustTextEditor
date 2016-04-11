package com.xkdev.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkdev.editor.settings.SettingsActivity;
import com.xkdev.editor.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 06.04.2016.
 */
public class EditActivity extends AppCompatActivity {
    private final static String DIR_SD = "Editor/MyFiles";
    private static final String TAG = "MyLogs";
    String path;
    String mFilePath;
    String titleActBar;
    private EditText mEditText;
    final static int PICK_FILE_CODE = 1;
//    SharedPreferences sPref;
    EditText mETFileName;
    TextView mRead;

    Context mContext;

    File file;
    File[] filePaths;
    String[] fileNames;

    ListView listDrawer;

    DrawerLayout drawerLayout;

    final String Logs = "MyLogs";
    SharedPreferences sp;

    boolean readMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        mContext = getApplicationContext();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);


        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIR_SD;
        file = new File(path);
        filePaths = file.listFiles();
        fileNames = new String[file.listFiles().length];

        for(int i = 0; i < filePaths.length; i++){

            fileNames[i] = filePaths[i].getName();
        }

        listDrawer = (ListView) findViewById(R.id.listDrawer);

        listDrawer.setAdapter(new ArrayAdapter<>(mContext, R.layout.drawer_list_item, fileNames));

        listDrawer.setOnItemClickListener(new DrawerItemClickListener());

        sp = getPreferences(MODE_PRIVATE);

        mRead = (TextView) findViewById(R.id.tvRead);
        mRead.setVisibility(View.GONE);
        readMode = sp.getBoolean("read mode", false);
        Log.d(TAG, "onCreate: " + readMode);

        mEditText = (EditText) findViewById(R.id.etText);
        mFilePath = getIntent().getStringExtra("filepath");
        titleActBar = mFilePath;

        getSupportActionBar().setTitle(titleActBar.replaceAll("/storage/emulated/0/Editor/MyFiles/", ""));
        Log.d(TAG, "Filepath: " + mFilePath);

        Util.openFileEditSD(mFilePath, mContext, mEditText);
        if(readMode){
            mRead.setText(mEditText.getText().toString());
            mEditText.setVisibility(View.GONE);
            mRead.setVisibility(View.VISIBLE);
        }
        else{
            mRead.setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
         sp = PreferenceManager.getDefaultSharedPreferences(this);

        //Смена размера шрифта
        float fSize = Float.parseFloat(sp.getString(getString(R.string.pref_size), "20"));
        mEditText.setTextSize(fSize);

        //Смена стиля шрифта
        String regular = sp.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;

        if(regular.contains(getString(R.string.pref_style_bold))){
            typeface += Typeface.BOLD;
        }
        if(regular.contains(getString(R.string.pref_style_italic))){
            typeface += Typeface.ITALIC;
        }
        mEditText.setTypeface(null, typeface);

        //Смена цвета текста
        String sColor = sp.getString(getString(R.string.pref_color), "");
        int textColor = Color.BLACK;

        if(sColor.contains(getString(R.string.pref_color_black))){
            textColor = Color.BLACK;
        }
        else if(sColor.contains(getString(R.string.pref_color_blue))){
            textColor = Color.BLUE;
        }
        else if(sColor.contains(getString(R.string.pref_color_green))){
            textColor = Color.GREEN;
        }
        else if(sColor.contains(getString(R.string.pref_color_red))){
            textColor = Color.RED;
        }
        mEditText.setTextColor(textColor);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_open:
                openFileManager();
                return true;
            case R.id.action_mode_read:
                if(!readMode){
                    mRead.setText(mEditText.getText().toString());
                    mEditText.setVisibility(View.GONE);
                    mRead.setVisibility(View.VISIBLE);
                    readMode = true;
                }
                else{
                    mRead.setVisibility(View.GONE);
                    mEditText.setVisibility(View.VISIBLE);
                    readMode = false;
                }
                return true;
            case R.id.action_save:
                if(mFilePath != null)
                    writeFileSD(mFilePath);
                return true;
            case R.id.action_create:
                createFileSD();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }


    //Метод для записи файла на sd card
    public void writeFileSD(String filePath){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(Logs, "SD карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File sdFile = new File(filePath);

        try{
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(sdFile));
            bWriter.write(mEditText.getText().toString());
            bWriter.close();
            Toast.makeText(mContext, R.string.succes_write_SD + sdFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_write_SD + e.toString(), Toast.LENGTH_SHORT).show();
        }
        filePaths = file.listFiles();
        fileNames = new String[file.listFiles().length];

        for(int i = 0; i < filePaths.length; i++){

            fileNames[i] = filePaths[i].getName();
        }

        listDrawer = (ListView) findViewById(R.id.listDrawer);

        listDrawer.setAdapter(new ArrayAdapter<>(mContext, R.layout.drawer_list_item, fileNames));
    }

    @Override
    protected void onDestroy() {
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("read mode", readMode);
        ed.commit();
        Log.d(TAG, "onDestroy: " + readMode);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        writeFileSD(mFilePath);
    }

    //Метод для создания нового файла
    public void createFileSD(){
        mETFileName = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.create_text_file);
        builder.setView(mETFileName);
        builder.setCancelable(true);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mETFileName != null) {
                    String fileName = mETFileName.getText().toString() + ".txt";
                    File sdPath = Environment.getExternalStorageDirectory();
                    sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
                    sdPath.mkdirs();
                    writeEmptyFileSD(sdPath + "/" + fileName);
                } else {
                    Toast
                            .makeText(getApplicationContext(), R.string.create_text_file, Toast.LENGTH_SHORT)
                            .show();
                    createFileSD();
                }
            }
        });

        builder.show();
    }
    //Метод для открытия файлового менеджера
    public void openFileManager()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICK_FILE_CODE);

    }
    //Метод для обработки выбора файла
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== PICK_FILE_CODE){
            if(data!=null){
                mFilePath = data.getData().getPath();
                Util.openFileEditSD(mFilePath, mContext, mEditText);
            }
            else return;
        }
        else
            return;
        if(resultCode == RESULT_CANCELED){
            return;
        }
    }

    public void writeEmptyFileSD(String filePath){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(TAG, getString(R.string.sd_not_available) + Environment.getExternalStorageState());
            return;
        }
        File sdFile = new File(filePath);

        try{
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(sdFile));
            bWriter.write("");
            bWriter.close();
            Toast.makeText(getApplicationContext(), R.string.succes_write_SD + sdFile.getPath(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_write_SD + e.toString(), Toast.LENGTH_SHORT).show();
        }
        filePaths = file.listFiles();
        fileNames = new String[file.listFiles().length];

        for(int i = 0; i < filePaths.length; i++){

            fileNames[i] = filePaths[i].getName();
        }

        listDrawer = (ListView) findViewById(R.id.listDrawer);
        mFilePath = filePath;
        titleActBar = filePath;
        listDrawer.setAdapter(new ArrayAdapter<>(mContext, R.layout.drawer_list_item, fileNames));
        Util.openFileEditSD(mFilePath, mContext, mEditText);
        getSupportActionBar().setTitle(titleActBar.replaceAll("/storage/emulated/0/Editor/MyFiles/", ""));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            writeFileSD(mFilePath);
            mFilePath = filePaths[position].getPath();
            Util.openFileEditSD(filePaths[position].getPath(), mContext, mEditText);
            getSupportActionBar().setTitle(filePaths[position].getName());
            drawerLayout.closeDrawer(listDrawer);
        }
    }
}
