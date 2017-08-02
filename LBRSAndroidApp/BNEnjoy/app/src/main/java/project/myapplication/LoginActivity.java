package project.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TextInputLayout;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private ImageView img_list;
    private ImageView img_discover;
    private TextView createAccount;
    private TextView subtitle;
    private Button btn_login;
    private RelativeLayout login_layout;
    private EditText login_email;
    private EditText login_password;
    private String email;
    private String password;
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    private DataProcessor dp;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        img_list = (ImageView)findViewById(R.id.list);
        img_discover = (ImageView)findViewById(R.id.discover);
        createAccount = (TextView)findViewById(R.id.createAccount);
        subtitle = (TextView)findViewById(R.id.loginSubtitle);
        btn_login = (Button)findViewById(R.id.btn_login);
        login_layout = (RelativeLayout)findViewById(R.id.main_loginlayout);
        login_email = (EditText)findViewById(R.id.login_email);
        login_password = (EditText)findViewById(R.id.login_password);
        dp = new DataProcessor(this);

        //link to main layout
        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to list layout
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to signup layout
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //set a new font
        Typeface john_handy = Typeface.createFromAsset(getAssets(), "fonts/john_handy.ttf");
        subtitle.setTypeface(john_handy);

        //login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //verify text input when click anywhere
        login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLoginEmail();
                verifyLoginPassword();
            }
        });

        //this method must exist so that the user cannot go next if he input an invalid email
        login_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLoginEmail();
                verifyLoginPassword();
            }
        });

        //this method must exist so that the user cannot go next if he input an invalid password
        login_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLoginEmail();
                verifyLoginPassword();
            }
        });

        login_email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //login_password will get focus if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifyLoginEmail())) {
                    login_password.requestFocus();  //focus to next textview
                    return true;
                }
                return false;
            }
        });

        login_password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //hide keyboard and login if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifyLoginPassword())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);  //hide keyboard
                    login();
                    return true;
                }
                return false;
            }
        });

        //set password invisible
        login_password.setTransformationMethod(new PasswordTransformationMethod());
    }

    //proceed login
    public void login(){
        if (!verifyLoginEmail() || !verifyLoginPassword()) {
            btn_login.setEnabled(false);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this, R.style.Dialog);
        dialog.setIndeterminate(true);  //set animation that means logging in
        dialog.setMessage("Logging in...");
        dialog.show();
        //put network connection into a new thread
        new Thread(new Runnable(){
            public void run() {
                try { Thread.sleep(3000); }  //delay 3 seconds before dismissing the dialog
                catch(InterruptedException e) { e.getMessage(); }
                dialog.dismiss();
                Looper.prepare();  //solve RuntimeException
                onLogin();
                Looper.loop();  //solve RuntimeException
            }
        }).start();
        btn_login.post(new Runnable(){
            public void run(){
                btn_login.setEnabled(false);  //set button non-clickable when logging
            }
        });
        btn_login.setText("Logging in, please wait...");  //change button text after clicked
    }

    //show if login is succeeded or failed
    public void onLogin() {
        email = login_email.getText().toString();
        password = login_password.getText().toString();
        String parameters = "login_email=" + email + "&login_password=" + password;
        String parameters_2 = parameters + "&login_datetime=" + dp.getDateTime();
        String url_login = "http://10.89.208.155/MyProjectBNEnjoy/login.php";
        String url_login_2 = "http://10.89.208.155/MyProjectBNEnjoy/login_record.php";
        dp.postDataToPhp(parameters, url_login);  //send login request with entered email and password
        String result = dp.getData();  //get returned data from php
        user = dp.parseJson(result);
        String message_1 = "Login succeeded!\nWelcome, ";
        String message_2 = "Login failed: Your email and password do not match. ";
        String message_3 = "Login timed out: Network error. ";
        //cannot use !user.equals(null) here, otherwise it will occur app crash
        if (user != null && user.getEmail() != null && user.getPassword() != null) {
            //just double check email and password do match
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                Toast.makeText(getBaseContext(), message_1 + user.getName(), Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(user);  //parse user to string
                sp.edit().putString("user", json).apply();  //store user information
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                dp.postDataToPhp(parameters_2, url_login_2);  //record login data to mysql via php post
            }
        }
        else if (result.equals("fail")){  //get string "fail" from php
            Toast.makeText(getBaseContext(), message_2, Toast.LENGTH_LONG).show();
        }
        else {  //get nothing from php, which means cannot connect to php
            Toast.makeText(getBaseContext(), message_3, Toast.LENGTH_LONG).show();
        }
        btn_login.post(new Runnable(){
            public void run(){
                btn_login.setText("Login");  //recover button text after logging
                btn_login.setEnabled(true);  //recover button as clickable
            }
        });
    }

    //make sure the user has entered a valid email
    public boolean verifyLoginEmail() {
        email = login_email.getText().toString();
        emailWrapper = (TextInputLayout) findViewById(R.id.LoginEmailWrapper);
        if (email.isEmpty()) {
            emailWrapper.setError("Email address cannot be empty! ");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailWrapper.setError("Please input a valid email address! ");
            return false;
        }
        else{
            emailWrapper.setError(null);
            btn_login.setEnabled(true);
        }
        return true;
    }

    //make sure the user has entered a valid password
    public boolean verifyLoginPassword() {
        password = login_password.getText().toString();
        passwordWrapper = (TextInputLayout) findViewById(R.id.LoginPasswordWrapper);
        if (password.isEmpty()) {
            passwordWrapper.setError("Password cannot be empty! ");
            return false;
        }
        else if (password.length() < 4 || password.length() > 15) {
            passwordWrapper.setError("Password must consist of 4 to 15 alphanumeric characters! ");
            return false;
        }
        else{
            passwordWrapper.setError(null);
            btn_login.setEnabled(true);
        }
        return true;
    }
}
