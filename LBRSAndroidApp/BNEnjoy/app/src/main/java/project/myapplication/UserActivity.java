package project.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private ImageView img_discover, img_list;
    private User user;
    private Button edit, submit;
    private Spinner spinner;
    private TextView titleName, logout, email, dob, gender;
    private EditText name, phone, address;
    private LinearLayout profile, favorites, layoutProfile, layoutFavorites;
    private Animation fadeInAnimation, fadeOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        img_discover = (ImageView)findViewById(R.id.discover);
        img_list = (ImageView)findViewById(R.id.list);
        edit = (Button) findViewById(R.id.button_edit);
        submit = (Button) findViewById(R.id.button_submit);
        profile = (LinearLayout) findViewById(R.id.profile);
        favorites = (LinearLayout) findViewById(R.id.favorites);
        layoutProfile = (LinearLayout) findViewById(R.id.layout_profile);
        layoutFavorites = (LinearLayout) findViewById(R.id.layout_favorites);
        spinner = (Spinner) findViewById(R.id.user_gender_spinner);
        titleName = (TextView) findViewById(R.id.title_name);
        logout = (TextView) findViewById(R.id.log_out);
        email = (TextView) findViewById(R.id.user_email);
        dob = (TextView) findViewById(R.id.user_dob);
        gender = (TextView) findViewById(R.id.user_gender);
        name = (EditText) findViewById(R.id.user_name);
        phone = (EditText) findViewById(R.id.user_phone);
        address = (EditText) findViewById(R.id.user_address);
        fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("user", null);
        user = gson.fromJson(json, User.class);  //get user
        titleName.setText(user.getName());
        name.setInputType(InputType.TYPE_NULL);
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setInputType(InputType.TYPE_NULL);
        if (user.getPhone() == null){
            user.setPhone("");
        }
        phone.setText(user.getPhone());
        //final SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy");
        final SimpleDateFormat dobFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (user.getDateOfBirth() == null){
            dob.setText("");
        }
        else{
            dob.setText(dobFormat.format(user.getDateOfBirth()));
        }
        if (user.getGender() == null){
            user.setGender("");
        }
        gender.setText(user.getGender());
        address.setInputType(InputType.TYPE_NULL);
        if (user.getLocation() == null){
            user.setLocation("");
        }
        address.setText(user.getLocation());

        //click to logout user account
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserActivity.this)
                        .setTitle("Log out")
                        .setMessage("Do you want to log out this account?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                                SharedPreferences sp2
                                        = getSharedPreferences("FAVORITES", Context.MODE_PRIVATE);
                                sp.edit().clear().apply();
                                sp2.edit().clear().apply();
                                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){}
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //click to show profile layout
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFavorites.setVisibility(View.GONE);
                if (layoutProfile.getVisibility() == View.GONE){
                    layoutProfile.startAnimation(fadeInAnimation);
                    layoutProfile.setVisibility(View.VISIBLE);
                }
            }
        });

        //click to show favorites layout
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutProfile.setVisibility(View.GONE);
                if (layoutFavorites.getVisibility() == View.GONE){
                    layoutFavorites.startAnimation(fadeInAnimation);
                    layoutFavorites.setVisibility(View.VISIBLE);
                }
                List<FoursquarePlace> fps = new ArrayList<>();
                SharedPreferences sp = getSharedPreferences("FAVORITES", Context.MODE_PRIVATE);
                Map<String, ?> allEntries = sp.getAll();
                //get all fps
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Gson gson = new Gson();
                    String json = sp.getString(entry.getKey(), null);
                    FoursquarePlace fp = gson.fromJson(json, FoursquarePlace.class);
                    fps.add(fp);
                }
                //initialize adapter
                RecommendedViewAdapter adapter;
                GridLayoutManager gridLayoutManager
                        = new GridLayoutManager(getApplicationContext(), 2);
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(gridLayoutManager);
                adapter = new RecommendedViewAdapter(fps, UserActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();  //update new data
                recyclerView.startAnimation(fadeInAnimation);
                //set click listener to each recommended view
                adapter.setRecommendedViewOnClickListener(new RecommendedViewAdapter.
                        RecommendedViewOnClickListener() {
                    @Override
                    public void onCardClick(View view, FoursquarePlace fp) {
                        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = gson.toJson(fp);  //parse FoursquarePlace fp to json
                        //save fp as json format
                        sp.edit().putString("ChosenFoursquarePlace", json).apply();
                        sp.edit().putBoolean("UserAct", true).apply();
                        Intent intent = new Intent(UserActivity.this,
                                DetailedInformationActivity.class);
                        startActivity(intent);
                        //make activity transition effect fade in and fade out
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
            }
        });

        //click to edit user profile
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                name.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);  //set name editable
                name.setBackgroundResource(R.color.colorWhite2);
                phone.setInputType(InputType.TYPE_CLASS_PHONE);  //set phone editable
                phone.setBackgroundResource(R.color.colorWhite2);
                //set dob editable
                dob.setBackgroundResource(R.color.colorWhite2);
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dob.setText(dobFormat.format(calendar.getTime()));
                    }
                };
                dob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(UserActivity.this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                //set gender editable
                spinner.setVisibility(View.VISIBLE);
                spinner.setBackgroundResource(R.color.colorWhite2);
                gender.setVisibility(View.GONE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        getApplicationContext(),
                        R.array.gender_array, R.layout.spinner_item);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getPosition(user.getGender()));  //set selected item
                //set address editable
                address.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                address.setBackgroundResource(R.color.colorWhite2);
            }
        });

        //click to submit user profile
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if nothing changed
                if (name.getText().toString().equals(user.getName())
                        && phone.getText().toString().equals(user.getPhone())
                        && ((user.getDateOfBirth() != null
                        && dob.getText().toString().equals(dobFormat.format(user.getDateOfBirth())))
                        || (user.getDateOfBirth() == null && dob.getText().equals("")))
                        && spinner.getSelectedItem().equals(user.getGender())
                        && address.getText().toString().equals(user.getLocation())){
                    Toast.makeText(UserActivity.this, "Nothing Changed.", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (name.getText().toString().equals("")){
                        Toast.makeText(UserActivity.this,
                                "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(UserActivity.this,
                                "Successfully Edited!", Toast.LENGTH_SHORT).show();
                        user.setName(name.getText().toString());
                    }
                    name.setText(user.getName());
                    user.setPhone(phone.getText().toString());
                    phone.setText(user.getPhone());
                    try{
                        user.setDateOfBirth(dobFormat.parse(dob.getText().toString()));
                    }
                    catch (ParseException e){
                        e.getMessage();
                    }
                    if (user.getDateOfBirth() != null){
                        dob.setText(dobFormat.format(user.getDateOfBirth()));
                    }
                    else{
                        dob.setText("");
                    }
                    user.setGender(spinner.getSelectedItem().toString());
                    gender.setText(user.getGender());
                    user.setLocation(address.getText().toString());
                    address.setText(user.getLocation());
                }
                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(user);  //parse user to string
                sp.edit().putString("user", json).apply();  //update user information
                edit.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
                name.setInputType(InputType.TYPE_NULL);  //set name un-editable
                name.setBackgroundResource(R.color.colorTransparent);
                phone.setInputType(InputType.TYPE_NULL);  //set phone un-editable
                phone.setBackgroundResource(R.color.colorTransparent);
                dob.setClickable(false);  //set dob un-clickable
                dob.setBackgroundResource(R.color.colorTransparent);
                spinner.setVisibility(View.GONE);
                spinner.setBackgroundResource(R.color.colorTransparent);
                gender.setVisibility(View.VISIBLE);  //set gender un-editable
                gender.setBackgroundResource(R.color.colorTransparent);
                address.setInputType(InputType.TYPE_NULL);  //set address un-editable
                address.setBackgroundResource(R.color.colorTransparent);
            }
        });

        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ListActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
