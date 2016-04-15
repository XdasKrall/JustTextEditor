package com.xkdev.editor.login;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xkdev.editor.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dfomichev on 08.04.2016.
 */
public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Uri Провайдера
    final Uri URI_CONTENT = Uri.parse("content://MyDB/users");
    private static final String TAG = "MyLogs";

    private static final int CODE_EMAIL = 1;
    private static final int CODE_PASSWORD = 2;
    //Параметры аутентификации
    private AutoCompleteTextView mEmailView;
    private EditText mPassword;

    private UserTask user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylogin_layout);
        //Инициализация Loader
        getLoaderManager().initLoader(0, null, this);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.autoTextView);
        mPassword = (EditText) findViewById(R.id.etPassword);
        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        //Отправка введенных данных на проверку корретности ввода
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAttempt();
            }
        });
    }

    private boolean passwordIsValid(String password){
        return password.length() > 4;
    }

    private boolean emailIsValid(String email){
        return email.contains("@");
    }
//Проверка корректности ввода данных
    private void loginAttempt(){
        if(user != null){
            return;
        }
        String email = mEmailView.getText().toString();
        String password = mPassword.getText().toString();
        if(!emailIsValid(email)){
            Toast.makeText(this, R.string.incorrect_email, Toast.LENGTH_SHORT).show();
        }
        else if(!passwordIsValid(password)){
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
        }
        else{
            user = new UserTask(email, password, this);

        user.execute();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, URI_CONTENT, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Получение массива электронных адресов из БД
     List<String> emails = new ArrayList<>();
        data.moveToFirst();
        while(!data.isAfterLast()){
            emails.add(data.getString(CODE_EMAIL));
            Log.d(TAG, data.getString(CODE_EMAIL));
            Log.d(TAG, data.getString(CODE_PASSWORD));
            data.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }
//Автозаполнение формы для почты, при вводе первых символов
    private void addEmailsToAutoComplete(List<String> emails) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, emails);
        mEmailView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
//Аутентификация
    private class UserTask extends AsyncTask<Void, Void, Boolean>{

        private String mEmail;
        private  String mPassword;
        private Context context;
        boolean inCorrectPassword;

        private UserTask(String email, String password, Context context) {
            this.mEmail = email;
            this.mPassword = password;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            user = null;
            if(success){
                Log.d(TAG, "onPostExecute");
                finish();
            }
            //Если введеной почты нет в базе - предложение зарегистрировать
            else{if(!inCorrectPassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.registration_dialog_title);
                builder.setMessage(R.string.dialog_message_registration);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues cv = new ContentValues();
                        cv.put("Почта", mEmail);
                        cv.put("Пароль", mPassword);
                        getContentResolver().insert(URI_CONTENT, cv);
                        finish();
                    }
                }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, R.string.enter_correct
                                , Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
            //Если почта есть, но пароль не верный - вывод сообщения об этом
                else {
                Toast.makeText(context, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
            }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Проверка соответствия почты и пароля
            inCorrectPassword = false;
            Cursor cursor = getContentResolver().query(URI_CONTENT, null,
                    "email = '"
                            + mEmail + "'", null, null);;
            if(cursor != null && cursor.moveToFirst()){

                if(mPassword.equals(cursor.getString(1))){

                    return true;
            }
                else {
                    inCorrectPassword = true;
                    return false;
                }
            }
            return false;
        }
    }
}
