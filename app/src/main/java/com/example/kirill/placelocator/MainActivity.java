package com.example.kirill.placelocator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirill.placelocator.APIUtil.MD5Crypt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /*
    Bellow are the 3 identificators with the name of the settings, logins and hashes.
    PREFERENCE_LOGIN is a login identificator in PREFERENCES_AUTHORISATION
    PREFERENCE_HASH is a hash identificator in PREFERENCES_AUTHORISATION
    PREFERENCES_AUTHORISATION is a variable with a name of file with settings
     */
    public static final String PREFERENCES_AUTHORISATION = "authorisation";
    public static final String PREFERENCE_LOGIN = "login";
    public static final String PREFERENCE_HASH = "hash";
    private Button buttonEnter;
    private TextView textViewLogin;
    private TextView textViewPassword;
    private TextView textViewSignIn;
    private TextView textViewFail;
    private SharedPreferences preferences;
    /*
    Current object is used for in case of mistake (wrong login or password)
    we could pop up the message about the mistake.
    Current object is used for receiving messages from a multi-streaming code.
    Because in multi-stream we cannot just use a 'if' with a toast.
    We can only use them in the main stream.
    Because of that we will be sending messages from the secondary stream into the main one.
    While in the main stream using the identification of the sent message we will be showing
    a mistake in case it's needed.
     */
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(MainActivity.this,"Incorrect login/password", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //calling for the method onCreate from the parent class
        //in the parent onCreate method developers produced standard mechanisms
        super.onCreate(savedInstanceState);
        //setContentView joins file with layout with Java code
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //found toolbar using its id. As a toolbar we installed the found one.
        setSupportActionBar(toolbar);
        this.initComponents();
    }

    private void initComponents() {
        //connecting preferences from a file containing them
        this.preferences = getSharedPreferences(PREFERENCES_AUTHORISATION, Context.MODE_PRIVATE);
        this.initButtonEnter();
        this.initTextViews();
    }

    private void initTextViews() {
        this.textViewLogin = (TextView) this.findViewById(R.id.content_authorisation_login);
        this.textViewPassword = (TextView) this.findViewById(R.id.content_authorisation_password);
        this.textViewSignIn = (TextView) this.findViewById(R.id.content_authorisation_sign_in);
        this.textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        this.textViewFail = (TextView) this.findViewById(R.id.content_authorisation_fail);
    }

    private void initButtonEnter() {
        this.buttonEnter = (Button) this.findViewById(R.id.content_authorisation_button_enter);
        this.buttonEnter.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit...");
        builder.setMessage("Do you really want to quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    //if a enter button is pressed the control is passed on into this code
    @Override
    public void onClick(View view) {
        //creating a new secondary thread conecction with internet since it cannot be done in the main thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String login = null;
                HttpURLConnection conn = null;
                try {
                    Log.i("chat", "+ FoneService --------------- initialising connection");

                    //receiving a login from the form
                    login  = textViewLogin.getText().toString();
                    //receiving a password from a form
                    String password  = textViewPassword.getText().toString();
                    //encrypting the password using MD5 algorithm
                    password = MD5Crypt.md5(password);

                    String server_name = "http://vhost8260.cpsite.ru";
                    String lnk = server_name + "/?action=auth&login=" + login + "&password=" + password;
                    conn = (HttpURLConnection) new URL(lnk)
                            .openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setDoInput(true);
                    conn.connect();

                } catch (Exception e) {
                    Log.i("chat", "+ FoneService error: " + e.getMessage());
                }

                // receiving answer ---------------------------------->
                try {
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));

                    //receiving server answer about the correction of login and password
                    String ans = br.readLine();
                    //receiving authorisation hash
                    String anHash = br.readLine();

                    Log.i("chat", "+ FoneService - full server's reply:\n"
                            + ans + anHash);
                    SharedPreferences.Editor editor = preferences.edit();
                    /*
                    If server's respond is not 'null' and contains 'true'
                    then we will proceed with the following statements.
                     */
                    if(ans != null && ans.contains("true")) {
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent);
                        //into the preference file login and authorisation hash is written down
                        editor.putString(PREFERENCE_LOGIN, login);
                        //'split' divides login and authorisation hash from each other as they have a + in between
                        editor.putString(PREFERENCE_HASH, ans.split("\\+")[1]);
                        editor.apply();
                        finish();
                    }
                    else{
                        /*
                        if the server's respond is negative, we send a message to the main stream
                        with an identificator = 1
                         */
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }

                    is.close(); // close stream
                    br.close(); // close buffer

                } catch (Exception e) {
                    Log.i("chat", "+ FoneService error: " + e.getMessage());
                } finally {
                    conn.disconnect();
                    Log.i("chat", "+ FoneService --------------- close connection");
                }
            }
        });
        //start the thread we created
        thread.start();
    }
}
