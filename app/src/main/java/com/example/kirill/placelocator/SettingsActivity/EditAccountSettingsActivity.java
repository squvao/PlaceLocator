package com.example.kirill.placelocator.SettingsActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kirill.placelocator.R;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

/*
This activity installs user account settings.
 */
public class EditAccountSettingsActivity extends AppCompatActivity {
    //this constant represents file name with preferences.
    //A constant in programming is a variable that is given a distinct value once which cannot be
    //changed in the process of execution.
    public static final String ACCOUNT_PREFERENCES = "account";
    //this constant represents login settings.
    //username ivanov
    //pol 1
    //address ghjkj
    public static final String ACCOUNT_PREFERENCES_USER_NAME = "username";
    public static final String ACCOUNT_PREFERENCES_USER_SEC_NAME = "usersecname";
    public static final String ACCOUNT_PREFERENCES_USER_POL = "pol";
    public static final String ACCOUNT_PREFERENCES_ADDRESS = "address";
    public static final String ACCOUNT_PREFERENCES_EMAIL = "email";
    public static final String ACCOUNT_PREFERENCES_DATE_BORN_YEAR = "datebornyear";
    public static final String ACCOUNT_PREFERENCES_DATE_BORN_MONTH = "datebornmonth";
    public static final String ACCOUNT_PREFERENCES_DATE_BORN_DAY = "datebornday";
    public static final String ACCOUNT_PREFERENCES_USER_AVATAR = "useravatar";
    // A constant that represents a request to pick a profile picture from gallery
    private static final int REQUEST_AVATAR = 1;

    //Variables which will be linked with layout
    private EditText textViewUserName;
    private EditText textViewUserSecName;
    private TextView textViewDateBorn;
    private TextView textViewAddress;
    private TextView textViewEMail;
    private RadioGroup radioGroupUserPol;
    private CircleImageView viewCircleImage;

    private SharedPreferences mSettings;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //linking layout with code.
        setContentView(R.layout.activity_edit_account_settings);
        initComponents();
    }

    private void initComponents() {
        //receiving settings from account file
        this.mSettings = getSharedPreferences(ACCOUNT_PREFERENCES, Context.MODE_PRIVATE);
        initToolbar();
        //searching for an element of CircleImageView through id and linking it to the code.
        viewCircleImage = (CircleImageView) this.findViewById(R.id.profile_image);
        //installing listener for the CircleImageView being pressed
        viewCircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //an action of opening gallery in order to search for profile image.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //launching gallery with a request code of 1.
                startActivityForResult(intent, REQUEST_AVATAR);
            }
        });
        initInputForms();
    }

    private void initInputForms() {
        initEditText();
        //next two lines will link text components to the code
        //they are text fields for address and email.
        textViewAddress = (TextView) this.findViewById(R.id.text_view_settings_address);
        textViewEMail = (TextView) this.findViewById(R.id.text_view_settings_mail);
        //installing the value for text fields from preferences file
        //if the preference file does not contain the information about these text fields
        //then  they take a value 'not selected'
        textViewAddress.setText(mSettings.getString(ACCOUNT_PREFERENCES_ADDRESS,
                getResources().getString(R.string.content_settings_text_no_changed)));
        textViewEMail.setText(mSettings.getString(ACCOUNT_PREFERENCES_EMAIL,
                getResources().getString(R.string.content_settings_text_no_changed)));
        //linking radio group layout with the java code.
        //radio group includes 2 radio buttons eg. in our case male/female
        //therefore the state of a radio button we can receive through radio group
        radioGroupUserPol = (RadioGroup) findViewById(R.id.content_settings_radio_group);
        //installing a listener for the choice of a radio group element.
        radioGroupUserPol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = mSettings.edit();
                //if we press one of the buttons a value is recorded into preference file
                editor.putInt(ACCOUNT_PREFERENCES_USER_POL, checkedId);
                editor.apply();
            }
        });
        //linking the fields with their fields in preference file.
        //if the value is not found in preference file then an empty string is placed into text fields.
        textViewUserName.setText(mSettings.getString(ACCOUNT_PREFERENCES_USER_NAME, ""));
        textViewUserSecName.setText(mSettings.getString(ACCOUNT_PREFERENCES_USER_SEC_NAME, ""));
        //receiving DoB values from preference file. Default dates viewed will be 26/08/1989
        mYear = mSettings.getInt(ACCOUNT_PREFERENCES_DATE_BORN_YEAR, 1989);
        mMonth = mSettings.getInt(ACCOUNT_PREFERENCES_DATE_BORN_MONTH, 8);
        mDay = mSettings.getInt(ACCOUNT_PREFERENCES_DATE_BORN_DAY, 26);
        textViewDateBorn = (TextView) this.findViewById(R.id.text_view_settings_date_born);
        //sets a format for DoB in order to store it in a text file.
        textViewDateBorn.setText(String.format("%d.%d.%d", mDay, mMonth, mYear));
        //if a preference file contains information about gender
        //then we set this value into the radio group
        if(mSettings.contains(ACCOUNT_PREFERENCES_USER_POL))
            radioGroupUserPol.check(mSettings.getInt(ACCOUNT_PREFERENCES_USER_POL, 0));
        //if a preference file contains information about a profile picture
        //then we install a picture through its address into the CircleImageView
        if(mSettings.contains(ACCOUNT_PREFERENCES_USER_AVATAR)) {
            //while parsing the link an error might take place. A program cannot detect an address.
            //we process this mistake in a 'catch' part
            try {
                //parsing the link from a preference file and receiving image from gallery
                Uri uri = Uri.parse(mSettings.getString(ACCOUNT_PREFERENCES_USER_AVATAR,""));
                //installing the profile picture into CircleImageView
                viewCircleImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initEditText() {
        //linking textViewUserName layout with java code.
        textViewUserName = (EditText) this.findViewById(R.id.content_settings_edit_text_user_name);
        //adding a listener to change text in the text field.
        //when username is entered it will automatically save it into the file.
        textViewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            //this method is responsible for saving the username as it is typed into a preference file.
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = mSettings.edit();
                //after text has been changed its uploaded into preference file.
                editor.putString(ACCOUNT_PREFERENCES_USER_NAME,
                        textViewUserName.getText().toString());
                editor.apply();
            }
        });
        textViewUserSecName = (EditText) this.findViewById(R.id.content_settings_edit_text_user_secname);
        textViewUserSecName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //editor is an object through which we enter information into the preferences file
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(ACCOUNT_PREFERENCES_USER_SEC_NAME,
                        textViewUserSecName.getText().toString());
                //'apply' method approves preference changes to be saved into a preference file.
                editor.apply();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    //a listener method for data input forms(what DoB, address and etc. are pressed).
    public void LinearLayoutListener(View view) {
        //receiving an id of an element pressed.
        int id = view.getId();
        //if an DoB element is pressed then the following code takes place.
        if(id == R.id.content_settings_ll_date_born) {
            //creating an object of a dialogue to choose date for DoB.
            //straight away we install a listener for date input
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //installing values received from a listener into the inner variables/fields
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;
                    //installing new values into a date text field taking set format into account
                    textViewDateBorn.setText(String.format("%d.%d.%d", mDay, mMonth, mYear));
                    //receiving a object which we will use to input values into a preference file
                    SharedPreferences.Editor editor = mSettings.edit();
                    //code inputting date values into preference file
                    editor.putInt(ACCOUNT_PREFERENCES_DATE_BORN_YEAR, mYear);
                    editor.putInt(ACCOUNT_PREFERENCES_DATE_BORN_MONTH, mMonth);
                    editor.putInt(ACCOUNT_PREFERENCES_DATE_BORN_DAY, mDay);
                    //method that applies the changes
                    editor.apply();
                }
            //setting date values which the user has already picked into dialogue window
            }, mYear, mMonth-1, mDay);
            //popping up the dialogue window
            dialog.show();
            //after the code above has taken place the listener shuts down
            return;
        }
        //creating an object for building a dialogue window for setting
        // values for fields that have nothing to do with date.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //variable responsible for the title and message shown in the dialogue window
        int title = 0;
        int message = 0;
        switch (id){
            case R.id.content_settings_ll_address:
                title = R.string.content_settings_text_address;
                message = R.string.content_settings_text_input_address;
                break;
            case R.id.content_settings_ll_mail:
                title = R.string.content_settings_button_mail;
                message = R.string.content_settings_text_input_mail;
                break;
        }
        //setting title and message for the dialogue
        // window with reference to what user inputs
        builder.setTitle(title);
        builder.setMessage(message);
        //dynamic linking dialogue window layout with java code
        //receiving a parent element of the layout and linking it to the java code.
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.content_settings_alert_dialog_layout, null);
        //dynamic linking parent element (layout) with a dialogue window
        //as a layout of dialogue window we have a parent element (LinearLayout)
        builder.setView(linearLayout);
        //linking child elements with java code
        //this variable (TextView) is responsible for a text field
        //with information which has been entered by a user.
        //We can receive information (read) about user input by using this variable.
        final TextView textView = (TextView) linearLayout
                .findViewById(R.id.content_settings_alert_dialog_text_view);
        //this variable (TextViewIndicator) is responsible for a text field
        //which cannot be seen by a user and which is used to install
        // the id by using which we can distinguish what user has entered.
        final TextView textViewIndicator = (TextView) linearLayout
                .findViewById(R.id.content_settings_alert_dialog_text_indicator);
        //installing id into
        textViewIndicator.setText(id + "");
        //installing an 'ok' button for a dialogue window.
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            //listener for the 'ok' button in the dialogue window.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //receiving an id of a button which a user
                // has pressed from the text field (either 'email' or 'city')
                //id defines a button which we have a listener for.
                int idLl = Integer.valueOf(textViewIndicator.getText().toString());
                SharedPreferences.Editor editor = mSettings.edit();
                //receiving information entered by user from a text field.
                String str = textView.getText().toString();
                //if the user didn't enter anything than we quit the listener.
                if(str.equals(""))
                    return;
                //depending on which button id we have we give a certain name to
                // information which is to be put into preferences.
                switch (idLl){
                    case R.id.content_settings_ll_address:
                        textViewAddress.setText(str);
                        editor.putString(ACCOUNT_PREFERENCES_ADDRESS, textViewAddress.getText().toString());
                        break;
                    case R.id.content_settings_ll_mail:
                        textViewEMail.setText(str);
                        editor.putString(ACCOUNT_PREFERENCES_EMAIL, textViewEMail.getText().toString());
                        break;
                }
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", null);
        //show dialogue window on the screen
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if respond is positive(user pressed 'ok' button)
        if(resultCode == RESULT_OK)
            //if respond code is 1 then
            if(requestCode == REQUEST_AVATAR){
                //then we save a link for image into account file with an id of 'user_avatar'
                //and then we install this profile image into CircleImageView
                Uri uri = data.getData();
                SharedPreferences.Editor editor = mSettings.edit();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    viewCircleImage.setImageBitmap(bitmap);
                    editor.putString(ACCOUNT_PREFERENCES_USER_AVATAR, uri.toString());
                    editor.apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
