package com.xkdev.justtexteditor;

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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkdev.justtexteditor.settings.SettingsActivity;
import com.xkdev.justtexteditor.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 06.04.2016.
 */
public class EditActivity extends AppCompatActivity {

    private final static String DIR_SD = "Editor/MyFiles"; //Каталог, где будут храниться все созданные файлы
    private static final String TAG = "MyLogs";
    final static int PICK_FILE_CODE = 1;//Реквест код для выбора файла, при открытии из файлового менеджера

    final static int CONTEXT_MENU_REMOVE = 101;//Id пункта удаления в контекстном меню

    String mFilePath;//Глобальная переменная пути к файлу
    String titleActBar;//Заголовок ActionBar

    EditText mETFileName;//Ввод имени, при создании нового файла
    TextView mRead;//Отображение в режиме чтения
    private EditText mEditText;//Редактирование текста

    Context mContext;

    File[] filePaths;
    String[] fileNames;

    ListView listDrawer;
    DrawerLayout drawerLayout;

    SharedPreferences sp;

    boolean readMode;//Режим чтения
    boolean exitFromRemove;//Выход из активити, при удалении последнего оставшегося файла

    ImageView imgText;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.listDrawer:
                menu.add(0, CONTEXT_MENU_REMOVE, 0, "Удалить");
                break;
            default:
                break;
        }
    }

    //Метод для удаления файлов
    private void removeFile(int position) {

        //Получение пути к файлу, который надо удалить (по позиции в listDrawer)
        String filePath = filePaths[position].getAbsolutePath();
        //Удаление файла
        File file = new File(filePath);
        Boolean removed = file.delete();
        Log.d(TAG, "Файл: " + filePath + " удален - " + removed.toString());
        //Если удален файл, который открыт
        //Запись нового пути в глобальную переменную
        if (mFilePath.equals(filePath)) {
            if (position > 0 && filePaths[position - 1] != null) {
                mFilePath = filePaths[position - 1].getAbsolutePath();
            } else if (position > 0 && filePaths[position + 1] != null) {
                mFilePath = filePaths[position + 1].getAbsolutePath();
            } else {              //Если файлов больше нет, выход из активити
                exitFromRemove = true;
                finish();
            }
        }
        if (!exitFromRemove) {

            //Если файлы есть,
            // обновление имен файлов в LayoutDrawer, Открытие другого файла, Изменение заголовка
            getFilesNames();
            Util.openFileEditSD(mFilePath, mContext, mEditText);
            titleActBar = mFilePath.replaceAll("/storage/emulated/0/Editor/MyFiles/", "");
            int pos = titleActBar.lastIndexOf(".");
            if(pos > 0) {
                titleActBar = titleActBar.substring(0, pos);
            }
            getSupportActionBar().setTitle(titleActBar);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position;

        switch (item.getItemId()) {
            case CONTEXT_MENU_REMOVE:

                //Получение позиции, нажатого элемента listDrawer
                position = mi.position;
                removeFile(position);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_layout);
        mContext = getApplicationContext();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listDrawer = (ListView) findViewById(R.id.listDrawer);
        listDrawer.setOnItemClickListener(new DrawerItemClickListener());
        registerForContextMenu(listDrawer);
        exitFromRemove = false;

        getFilesNames();

        sp = getPreferences(MODE_PRIVATE);

        imgText = (ImageView) findViewById(R.id.imgText);
        imgText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
        mRead = (TextView) findViewById(R.id.tvRead);
        readMode = sp.getBoolean("read mode", false);
        Log.d(TAG, "onCreate: " + readMode);

        mEditText = (EditText) findViewById(R.id.etText);
        //Получение пути к файлу из MainActivity
        mFilePath = getIntent().getStringExtra("filepath");

        titleActBar = mFilePath.replaceAll("/storage/emulated/0/Editor/MyFiles/", "");
        int pos = titleActBar.lastIndexOf(".");
        if(pos > 0) {
            titleActBar = titleActBar.substring(0, pos);
        }
        getSupportActionBar().setTitle(titleActBar);
        Log.d(TAG, "Filepath: " + mFilePath);
        //Открытие файла
        Util.openFileEditSD(mFilePath, mContext, mEditText);

        //Открытие файла в режиме чтения или редактирования,
        // в зависимости от того, какой режим был при прошлом выходе
        if (readMode) {
            mRead.setText(mEditText.getText().toString());
            mEditText.setVisibility(View.GONE);
            mRead.setVisibility(View.VISIBLE);
        } else {
            mRead.setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
            mEditText.setFocusable(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        //Смена размера шрифта
        float fSize = Float.parseFloat(sp.getString(getString(R.string.pref_size), "20"));
        mEditText.setTextSize(fSize);
        mRead.setTextSize(fSize);

        //Смена стиля шрифта
        String regular = sp.getString(getString(R.string.pref_style), "");
        int typeface = Typeface.NORMAL;

        if (regular.contains(getString(R.string.pref_style_bold))) {
            typeface += Typeface.BOLD;
        }
        if (regular.contains(getString(R.string.pref_style_italic))) {
            typeface += Typeface.ITALIC;
        }
        mEditText.setTypeface(null, typeface);
        mRead.setTypeface(null, typeface);

        //Смена цвета текста
        String sColor = sp.getString(getString(R.string.pref_color), "");
        int textColor = Color.BLACK;

        if (sColor.contains(getString(R.string.pref_color_black))) {
            textColor = Color.BLACK;
        } else if (sColor.contains(getString(R.string.pref_color_blue))) {
            textColor = Color.BLUE;
        } else if (sColor.contains(getString(R.string.pref_color_green))) {
            textColor = Color.GREEN;
        } else if (sColor.contains(getString(R.string.pref_color_red))) {
            textColor = Color.RED;
        }
        mEditText.setTextColor(textColor);
        mRead.setTextColor(textColor);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
                openFileManager();
                return true;
            case R.id.action_mode_read:     //Режим чтения
                if (!readMode) {
                    mRead.setText(mEditText.getText().toString());
                    mEditText.setVisibility(View.GONE);
                    mRead.setVisibility(View.VISIBLE);
                    readMode = true;
                } else {
                    mRead.setVisibility(View.GONE);
                    mEditText.setVisibility(View.VISIBLE);
                    mEditText.setFocusable(true);
                    readMode = false;
                }
                return true;
            case R.id.action_save:      //Сохранение файла
                if (mFilePath != null)
                    writeFileSD(mFilePath);
                return true;
            case R.id.action_create:     //Создание нового файла
                createFileSD();
                return true;
            case R.id.action_settings:    //Настройки
                Intent intent = new Intent(mContext, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    //Метод для записи файла на sdcard
    public void writeFileSD(String filePath) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File sdFile = new File(filePath);

        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(sdFile));
            bWriter.write(mEditText.getText().toString());
            bWriter.close();
            Toast.makeText(getApplicationContext(), "Файл " + sdFile.getName() + " успешно сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.error_write_SD + e.toString(), Toast.LENGTH_SHORT).show();
        }
        //Обновление информации в listDrawer
        getFilesNames();
    }

    @Override
    protected void onDestroy() {
        //Сохранение режима при выходе
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("read mode", readMode);
        ed.apply();
        Log.d(TAG, "onDestroy: " + readMode);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Сохранение файла, при сворачивании/закрытии приложения
        //Если файлов не осталось и был удален последний, без сохранения
        if (!exitFromRemove)
            writeFileSD(mFilePath);
    }

    //Метод для создания нового файла
    public void createFileSD() {
        mETFileName = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.create_text_file);
        builder.setView(mETFileName);
        builder.setCancelable(true);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                }

                //Если имя файла не введено, рекурсивный вызов
                else {

                    createFileSD();
                }
            }
        });

        builder.show();
    }

    //Метод для открытия файлового менеджера
    public void openFileManager() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICK_FILE_CODE);

    }

    //Метод для обработки выбора файла
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_CODE) {
            if (data != null) {
                mFilePath = data.getData().getPath();
                Util.openFileEditSD(mFilePath, mContext, mEditText);
            } else return;
        } else
            return;
        if (resultCode == RESULT_CANCELED) {
        }
    }

    //Метод для создания пустого файла на sdcard
    public void writeEmptyFileSD(String filePath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, getString(R.string.sd_not_available) + Environment.getExternalStorageState());
            return;
        }
        File sdFile = new File(filePath);

        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(sdFile));
            bWriter.write("");
            bWriter.close();
            Log.d(TAG, R.string.toast_create_text + sdFile.getName());

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, R.string.error_write_SD + e.toString());
        }

        getFilesNames();
        //Открытие нового пустого файла
        mFilePath = filePath;
        Util.openFileEditSD(mFilePath, mContext, mEditText);

        titleActBar = sdFile.getName();
        int pos = titleActBar.lastIndexOf(".");
        if(pos > 0) {
            titleActBar = titleActBar.substring(0, pos);
        }
        getSupportActionBar().setTitle(titleActBar);
    }

    //Метод для получения имен файлов и вывода их в listDrawer
    private void getFilesNames() {
        //Получение списка файлов в директории
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIR_SD);
        filePaths = file.listFiles();


        fileNames = new String[filePaths.length];
        //Наполнение массива именами файлов
        for (int i = 0; i < filePaths.length; i++) {

            fileNames[i] = filePaths[i].getName();
            int pos = fileNames[i].lastIndexOf(".");
            if (pos > 0) {
                fileNames[i] = fileNames[i].substring(0, pos);
            }
        }

        //Обновление имен файлов в адаптере
        listDrawer.setAdapter(new ArrayAdapter<>(mContext, R.layout.drawer_list_item, fileNames));

    }

    //Обработка нажатий на элементы listDrawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            writeFileSD(mFilePath);
            mFilePath = filePaths[position].getPath();
            Util.openFileEditSD(filePaths[position].getPath(), mContext, mEditText);

            int pos = fileNames[position].lastIndexOf(".");
            if (pos > 0) {
                fileNames[position] = fileNames[position].substring(0, pos);
            }
                getSupportActionBar().setTitle(fileNames[position]);
                drawerLayout.closeDrawer(listDrawer);
                Log.d(TAG, "OnItemClickListener");

        }
    }
}
