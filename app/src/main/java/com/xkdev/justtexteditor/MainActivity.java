package com.xkdev.justtexteditor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DIR_SD = "Editor/MyFiles";//Каталог, где будут храниться все созданные файлы
    public static final int PICK_FILE_CODE = 1;//Реквест код для выбора файла, при открытии из файлового менеджера
    Intent intent;

    private EditText mETFileName;//Имя при создании нового файла
    private String TAG = "MyLogs";
    Button btnCreate, btnOpen, btnExit, btnSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

      //  Intent iLogin = new Intent(this, LoginActivity.class);
      //    startActivity(iLogin);

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnExit = (Button) findViewById(R.id.btnExit);

        btnCreate.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        if (btnSendEmail != null) {
            btnSendEmail.setOnClickListener(this);
        }


        intent = new Intent();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnCreate:
                Log.d(TAG, "onClickCreate");
                createFileSD();
                break;
            case R.id.btnOpen:
                openFileManager();
                Log.d(TAG, "onClickOpen");
                    intent.setClass(this, EditActivity.class);
                break;
            case R.id.btnExit:
                this.finish();
                break;
            case R.id.btnSendEmail:               //Отправка сообщения по почте
                Intent intent = new Intent(this, SendEmailActivity.class);
                startActivity(intent);
                break;
        }
    }

    //Метод для создания нового файла
    public void createFileSD(){
        mETFileName = new EditText(MainActivity.this);
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

    //Метод для записи файла на sd card
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
            Log.d(TAG, "Файл " + sdFile.getName() + " - создан");
            intent.setClass(this, EditActivity.class);
            intent.putExtra("filepath", filePath);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Извините, файл не создался:(" + e.toString());
        }
    }

    //Метод для открытия файлового менеджера
    public void openFileManager()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICK_FILE_CODE);
    }

    //Открытие экрана редактирования при выборе файла, в файловом менеджере (txt)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== PICK_FILE_CODE){
            if(data!=null){
                intent.putExtra("filepath", data.getData().getPath());
                startActivity(intent);
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
