package com.xkdev.editor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.xkdev.editor.settings.SettingsActivity;
import com.xkdev.editor.util.Util;

/**
 * Created by user on 06.04.2016.
 */
public class ReadActivity extends AppCompatActivity {

    private static final int PICK_FILE_CODE = 1;
    TextView mRead;
    String filePath;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_layout);

        mContext = getApplicationContext();

        mRead = (TextView) findViewById(R.id.tvReadView);

        filePath = getIntent().getStringExtra("filepath");
        Util.openFileReadSD(filePath, mContext, mRead);
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
                filePath = data.getData().getPath();
                Util.openFileReadSD(filePath, mContext, mRead);
            }
            else return;
        }
        else
            return;
        if(resultCode == RESULT_CANCELED){
            return;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Проверка - открывать ли файл при запуске
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        //Смена размера шрифта
        float fSize = Float.parseFloat(sp.getString(getString(R.string.pref_size), "20"));
        mRead.setTextSize(fSize);

        //Смена стиля шрифта
        String regular = sp.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;

        if(regular.contains(getString(R.string.pref_style_bold))){
            typeface += Typeface.BOLD;
        }
        if(regular.contains(getString(R.string.pref_style_italic))){
            typeface += Typeface.ITALIC;
        }
        mRead.setTypeface(null, typeface);

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
        mRead.setTextColor(textColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_open:
                openFileManager();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }
}
