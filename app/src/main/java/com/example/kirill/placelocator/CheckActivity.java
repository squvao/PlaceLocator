package com.example.kirill.placelocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kirill.placelocator.APIUtil.Network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If there is no network a toast will pop up
        if(!Network.hasConnection(this)){
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show();
            return;
        }
        //creating a constant variable preferences which will be
        // associated with a local setting file in the app, which are present in the file PREFERENCE_AUTHORISATION
        //from this preference file we will be receiving login and authorisation hash
        final SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES_AUTHORISATION, Context.MODE_PRIVATE);
        //if authorisation hash is present in preferences we will send the request to the server
        // for identification of this authorisation hash
        if(preferences.contains(MainActivity.PREFERENCE_HASH)){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    //try-catch method is used to process the errors
                    //if an error pops up then the control goes to 'catch'
                    //'finally' means that the task will be performed in any possible outcome
                    try {
                        Log.i("chat", "+ FoneService --------------- ОТКРОЕМ СОЕДИНЕНИЕ");
                        //receiving password and authorisation hash from preferences
                        String login  = preferences.getString(MainActivity.PREFERENCE_LOGIN, "");
                        String hash_str = preferences.getString(MainActivity.PREFERENCE_HASH, "");
                        Log.i("chat", hash_str);
                        //server address string
                        String server_name = "http://vhost8260.cpsite.ru";
                        //server address + request string including action(checking authorisation hash and login)
                        String lnk = server_name + "/?action=auth_hash&login=" + login + "&hash_str=" + hash_str;
                        //establishing connection according to the address provided taking into account the request
                        conn = (HttpURLConnection) new URL(lnk)
                                .openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        //using 'post' method of sending request 'скрытная отправка (get method)
                        conn.setRequestMethod("POST");
                        //устанавливаем имитацию, что мы обращаемся к серверу из агента Mozilla 5.0
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                        conn.setDoInput(true);
                        conn.connect();

                    } catch (Exception e) {
                        Log.i("chat", "+ FoneService ошибка: " + e.getMessage());
                    }

                    // waiting for an answer ---------------------------------->
                    try {
                        //opening a stream for data reading from server's respond
                        InputStream is = conn.getInputStream();
                        //buffered stream
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"));
                        //reading a respond(true/false) from the stream
                        String ans = br.readLine();
                        //String anHash = br.readLine();

                        /*Log.i("chat", "+ FoneService - полный ответ сервера:\n"
                                + ans + anHash);*/
                        // if the server gave a positive respond, we move to StartActivity otherwise back to authorisation page (MainActivity)
                        if(ans.contains("true")) {
                            Intent intent = new Intent(CheckActivity.this, StartActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(CheckActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        is.close(); // close stream
                        br.close(); // close buffer

                    } catch (Exception e) {
                        Log.i("chat", "+ FoneService ошибка: " + e.getMessage());
                    } finally {
                        conn.disconnect(); //disconnect from server
                        Log.i("chat", "+ FoneService --------------- ЗАКРОЕМ СОЕДИНЕНИЕ");
                    }
                }
            });
            //everything written above start in a new stream. This code takes place in the background
            thread.start();
        }
        //if authorisation hash is absent, we move to MainActivity
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
