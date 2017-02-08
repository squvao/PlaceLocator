package com.example.kirill.placelocator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kirill.placelocator.LocationActivity.LocationActivity;
import com.example.kirill.placelocator.SettingsActivity.AboutUsActivity;
import com.example.kirill.placelocator.SettingsActivity.EditAccountSettingsActivity;
import com.example.kirill.placelocator.SettingsActivity.HelpActivity;
import com.example.kirill.placelocator.SettingsActivity.SettingsActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity{
    private Toolbar toolbar; //field characterising toolbar(panel containing a title, toggle and a menu)
    private DrawerLayout drawerLayout; //field characterising a drawer
    private Button buttonPlay; //field characterising a button
    private View headerNavigationView;
    private CircleImageView imageViewAvatar;
    private TextView textViewUserSecName;
    private TextView textViewUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start); //a method of connection between activity_main.xml and Java code
        initComponents(); //method of initialising the components
    }

    private void initComponents() {
        initToolbar(); //method of initialising the toolbar
        initNavigationView();//initialising the navigation menu
        initButtonPlay();//initialising the play button
    }

    private void initButtonPlay() {
        /*
        findViewById method provides a search of elements through their id
        returns a general object View, поэтому необходимо произвести операцию приведения типов
        Тем самым прировняв к переменной кнопки объект кнопку
         */
        buttonPlay = (Button) findViewById(R.id.content_main_button_play);
        /*
        setOnClickListener method installs a listener for that button
        В этот метод мы передаем анонимный объект анонимного внутреннего класса
         */
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                creating an Intent object. Its purpose is to open a new Activity. We pass on the
                context into it- MainActivity.this and a class of that Activity, which we want to start
                 */
                Intent intent = new Intent(StartActivity.this,LocationActivity.class);
                startActivity(intent); //Method of starting Activity through an intent object
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);//found toolbar through its id
        setSupportActionBar(toolbar);//Installed the found toolbar as the main one.
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*
        Created an object ActionBarDrawerToggle (3 lines in order to pull the menu out also called a 'Burger menu')
        Passed on the context into a constructor- current activity, drawerLayout, Toolbar, open the line,
        close the line.
         */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.action_bar_drawer_toggle_open,R.string.action_bar_drawer_toggle_close) ;
        /*
        Adding for DrawerLayout a listener for a drawer movement out
        (3 lines turn clockwise while the drawer is being pulled out)
        */
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // synchronising with NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        /*
        installing a listener for when an element NavigationView is pressed
        In method OnNavigationItemSelected depending on which menu button was pressed
        (is checked through id of a menu button) the action is performed.

        installing a listener which will only work when the method FindViewById
        could find NavigationView through its id and therefore NavigationView does not equal to null
         */
        if (navigationView != null) {
            headerNavigationView = navigationView.getHeaderView(0);
            imageViewAvatar = (CircleImageView) headerNavigationView.findViewById(R.id.profile_image);
            textViewUserSecName = (TextView) headerNavigationView.findViewById(R.id.content_settings_edit_text_user_sec_name);
            textViewUserName = (TextView) headerNavigationView.findViewById(R.id.content_settings_edit_text_user_name);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) { //listening method (takes place only when a user presed a button in the menu)
                    drawerLayout.closeDrawers(); // close the drawer
                    int id = item.getItemId();
                    switch (id){
                        case R.id.sub_menu_navigation_view_about_us:
                            Intent intent = new Intent(StartActivity.this,AboutUsActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.menu_navigation_view_help:
                            Intent intent2 = new Intent(StartActivity.this, HelpActivity.class);
                            startActivity(intent2);
                            break;
                        case R.id.menu_navigation_view_feedback:
                            /*
                            Launch of the activity to send an email
                             */
                            ShareCompat.IntentBuilder.from(StartActivity.this).setType("message/rfc822")
                                    .addEmailTo(getString(R.string.main_activity_navigation_item_feedback_email))//what email to send to
                                    .setSubject(getString(R.string.app_name))//subject of the mail
                                    .setText(getString(R.string.main_activity_navigation_item_feedback_message))//Messag epre-typed in the mail
                                    .setChooserTitle(getString(R.string.main_activity_navigation_item_feedback_title))//title
                                    .startChooser();
                            break;
                        case R.id.menu_navigation_view_settings:
                            Intent intent1 = new Intent(StartActivity.this, SettingsActivity.class);
                            startActivity(intent1);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /*
    Connection of menu_main.xml with the code
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    Listener of the menu buttons regarding their id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (headerNavigationView != null) {
            SharedPreferences mSettings = getSharedPreferences(EditAccountSettingsActivity.ACCOUNT_PREFERENCES, Context.MODE_PRIVATE);
            if (mSettings.contains(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_NAME)
                    && mSettings.contains(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_SEC_NAME)) {
                textViewUserName.setText(mSettings.getString(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_NAME, "Не выбрано"));
                textViewUserSecName.setText(mSettings.getString(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_SEC_NAME, "Не выбрано"));
            }
            if (mSettings.contains(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_AVATAR)) {
                try {
                    imageViewAvatar.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),
                            Uri.parse(mSettings.getString(EditAccountSettingsActivity.ACCOUNT_PREFERENCES_USER_AVATAR, ""))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}