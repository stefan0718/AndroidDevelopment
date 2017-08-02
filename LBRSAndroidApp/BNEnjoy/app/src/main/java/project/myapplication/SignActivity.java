package project.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SignActivity extends AppCompatActivity {

    private ImageView img_list;
    private ImageView img_discover;
    private TextView backLogin;
    private TextView subtitle;
    private Button btn_signup;
    private RelativeLayout signup_layout;
    private EditText signup_name;
    private EditText signup_email;
    private EditText signup_password;
    private EditText signup_repassword;
    private TextInputLayout nameWrapper;
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    private TextInputLayout repasswordWrapper;
    private String name;
    private String email;
    private String password;
    private String repassword;
    private DataProcessor dp;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        img_list = (ImageView)findViewById(R.id.list);
        img_discover = (ImageView)findViewById(R.id.discover);
        backLogin = (TextView)findViewById(R.id.backLogin);
        subtitle = (TextView)findViewById(R.id.signupSubtitle);
        btn_signup = (Button)findViewById(R.id.btn_signup);
        signup_layout = (RelativeLayout)findViewById(R.id.main_signuplayout);
        signup_name = (EditText)findViewById(R.id.signup_name);
        signup_email = (EditText)findViewById(R.id.signup_email);
        signup_password = (EditText)findViewById(R.id.signup_password);
        signup_repassword = (EditText)findViewById(R.id.signup_repassword);
        dp = new DataProcessor(this);

        //link to main layout
        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to list layout
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, ListActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to login layout
        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, LoginActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //set a new font
        Typeface john_handy = Typeface.createFromAsset(getAssets(), "fonts/john_handy.ttf");
        subtitle.setTypeface(john_handy);

        //sign up
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        //verify text input when click anywhere
        signup_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(true);
                verifySignupName();
                verifySignupEmail();
                verifySignupPassword();
                verifySignupRepassword();
            }
        });

        //this method must exist so that the user cannot go next if he input an invalid name
        signup_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(true);
                verifySignupName();
                verifySignupEmail();
                verifySignupPassword();
                verifySignupRepassword();
            }
        });

        //this method must exist so that the user cannot go next if he input an invalid email
        signup_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(true);
                verifySignupName();
                verifySignupEmail();
                verifySignupPassword();
                verifySignupRepassword();
            }
        });

        //this method must exist so that the user cannot go next if he input an invalid password
        signup_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(true);
                verifySignupName();
                verifySignupEmail();
                verifySignupPassword();
                verifySignupRepassword();
            }
        });

        //this method must exist so that the user cannot go next if he re-input an invalid password
        signup_repassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signup.setEnabled(true);
                verifySignupName();
                verifySignupEmail();
                verifySignupPassword();
                verifySignupRepassword();
            }
        });

        signup_name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //login_email will get focus if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifySignupName())) {
                    signup_email.requestFocus();  //focus to next textview
                    return true;
                }
                return false;
            }
        });

        signup_email.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //login_password will get focus if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifySignupEmail())) {
                    signup_password.requestFocus();  //focus to next textview
                    return true;
                }
                return false;
            }
        });

        signup_password.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //login_repassword will get focus if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifySignupPassword())) {
                    signup_repassword.requestFocus();  //focus to next textview
                    return true;
                }
                return false;
            }
        });

        signup_repassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //hide keyboard and signup if the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && (verifySignupRepassword())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);  //hide keyboard
                    signup();
                    return true;
                }
                return false;
            }
        });

        //set password and repassword invisible
        signup_password.setTransformationMethod(new PasswordTransformationMethod());
        signup_repassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    //proceed signup
    public void signup(){
        if (!verifySignupName() || !verifySignupEmail() || !verifySignupPassword()
                || !verifySignupRepassword()) {
            btn_signup.setEnabled(false);
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(SignActivity.this, R.style.Dialog);
        dialog.setIndeterminate(true);  //set animation that means signing up
        dialog.setMessage("Creating account...");
        dialog.show();
        //put network connection into a new thread
        new Thread(new Runnable(){
            public void run() {
                try { Thread.sleep(3000); }  //delay 3 seconds before dismissing the dialog
                catch(InterruptedException e) { e.getMessage(); }
                dialog.dismiss();
                Looper.prepare();  //solve RuntimeException
                onSignup();
                Looper.loop();  //solve RuntimeException
            }
        }).start();
        btn_signup.post(new Runnable(){
            public void run(){
                btn_signup.setEnabled(false);  //set button non-clickable when signing
            }
        });
        btn_signup.setText("Creating account, please wait...");  //change button text after clicked
    }

    //show if signup is succeeded or failed
    public void onSignup() {
        name = signup_name.getText().toString();
        email = signup_email.getText().toString();
        password = signup_password.getText().toString();
        repassword = signup_repassword.getText().toString();
        String parameters = "signup_name=" + name + "&signup_email=" + email + "&signup_password="
                + password + "&signup_datetime=" + dp.getDateTime();
        String parameters_2 = "login_email=" + email + "&login_password=" + password
                + "&login_datetime=" + dp.getDateTime();
        String url_signup = "http://10.89.208.155/MyProjectBNEnjoy/signup.php";
        String url_login_2 = "http://10.89.208.155/MyProjectBNEnjoy/login_record.php";
        dp.postDataToPhp(parameters, url_signup);
        String result = dp.getData();
        user = dp.parseJson(result);
        String message_1 = "Sign up succeeded!\nWelcome, ";
        String message_2 = "Sign up failed: This email has already been registered. ";
        String message_3 = "Sign up time out: Network error. ";
        //cannot use !user.equals(null) here, otherwise it will occur app crash
        if (user != null && user.getEmail() != null && user.getPassword() != null){
            //just double check email and password do match
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                Toast.makeText(getBaseContext(), message_1 + user.getName(), Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(user);  //parse user to string
                sp.edit().putString("user", json).apply();  //store user information
                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                startActivity(intent);
                dp.postDataToPhp(parameters_2, url_login_2);  //record login data to mysql via php post
            }
        }
        else if (result.equals("fail")){  //get string "fail" from php
            Toast.makeText(getBaseContext(), message_2, Toast.LENGTH_LONG).show();
        }
        else {  //get nothing from php, which means cannot connect to php
            Toast.makeText(getBaseContext(), message_3 + result, Toast.LENGTH_LONG).show();
        }
        btn_signup.post(new Runnable(){
            public void run(){
                btn_signup.setText("Sign up");  //recover button text after signing up
                btn_signup.setEnabled(true);  //recover button as clickable
            }
        });
    }

    public boolean verifySignupName() {
        name = signup_name.getText().toString();
        nameWrapper = (TextInputLayout) findViewById(R.id.SignupNameWrapper);
        if (name.isEmpty()) {
            nameWrapper.setError("Your name cannot be empty! ");
            return false;
        }
        else if (name.length() > 15) {
            nameWrapper.setError("Your name must be created within 15 alphanumeric characters! ");
            return false;
        }
        else{
            nameWrapper.setError(null);
            btn_signup.setEnabled(true);
        }
        return true;
    }

    public boolean verifySignupEmail() {
        signup_email = (EditText)findViewById(R.id.signup_email);
        email = signup_email.getText().toString();
        emailWrapper = (TextInputLayout) findViewById(R.id.SignupEmailWrapper);
        if (email.isEmpty()) {
            emailWrapper.setError("Email address cannot be empty! ");
            return false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailWrapper.setError("Please input a valid email address! ");
            return false;
        }
        else{
            emailWrapper.setError(null);
            btn_signup.setEnabled(true);
        }
        return true;
    }

    public boolean verifySignupPassword() {
        signup_password = (EditText)findViewById(R.id.signup_password);
        password = signup_password.getText().toString();
        passwordWrapper = (TextInputLayout) findViewById(R.id.SignupPasswordWrapper);
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
            btn_signup.setEnabled(true);
        }
        return true;
    }

    public boolean verifySignupRepassword() {
        signup_repassword = (EditText)findViewById(R.id.signup_repassword);
        repassword = signup_repassword.getText().toString();
        repasswordWrapper = (TextInputLayout) findViewById(R.id.SignupRepasswordWrapper);
        if (repassword.isEmpty()) {
            repasswordWrapper.setError("Password cannot be empty! ");
            return false;
        }
        else if (repassword.length() < 4 || repassword.length() > 15) {
            repasswordWrapper.setError("Password must consist of 4 to 15 alphanumeric characters! ");
            return false;
        }
        else if (!repassword.equals(password)) {
            repasswordWrapper.setError("The two passwords that you entered does not match! ");
            return false;
        }
        else{
            repasswordWrapper.setError(null);
            btn_signup.setEnabled(true);
        }
        return true;
    }
}