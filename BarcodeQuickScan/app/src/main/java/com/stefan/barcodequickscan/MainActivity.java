package com.stefan.barcodequickscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarcodeListAdapter adapter;
    private FilterListAdapter fAdapter;
    private List<String[]> barcodeList;
    private List<String[]> filterList;
    private RecyclerView recyclerView, filterRecyclerView;
    private LinearLayoutManager layoutManager, filterLayoutManager;
    private DividerItemDecoration decoration, filterDecoration;
    private Button ok, add;
    private ImageView deleteAll, email;
    private EditText barcodeInput, weightInput, prefixInput, contentInput,
                     suffixInput, descriptionInput;
    private BarcodeListAdapter.BarcodeOnClickListener bOnClickListener;
    private MediaPlayer correct, doh;
    private LinearLayout filterLayout, filterContent;
    private Animation fadeInAnimation, fadeOutAnimation;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        filterRecyclerView = (RecyclerView)findViewById(R.id.filterRecyclerView);
        barcodeInput = (EditText)findViewById(R.id.barcodeInput);
        weightInput = (EditText)findViewById(R.id.weightInput);
        prefixInput = (EditText)findViewById(R.id.prefixInput);
        contentInput = (EditText)findViewById(R.id.contentInput);
        suffixInput = (EditText)findViewById(R.id.suffixInput);
        descriptionInput = (EditText)findViewById(R.id.descriptionInput);
        deleteAll = (ImageView) findViewById(R.id.delete_all);
        email = (ImageView) findViewById(R.id.email);
        ok = (Button)findViewById(R.id.btn_ok);
        add = (Button)findViewById(R.id.btn_add);
        filterLayout = (LinearLayout)findViewById(R.id.filter_layout);
        filterContent = (LinearLayout)findViewById(R.id.filterContent);
        fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        bOnClickListener = new BarcodeListAdapter.BarcodeOnClickListener() {
            @Override
            public void onBarcodeClick(View view, final String[] s) {
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add Weight")
                        .setMessage(("Guess you might wanna add or change weight for this barcode. "
                                + "If so, you could input weight below. "))
                        .setView(input)
                        .setPositiveButton("Add or Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                if (barcodeList.size() > 0){
                                    for (int i = 0; i < barcodeList.size(); i++){
                                        if (barcodeList.get(i)[0].equals(s[0])){
                                            if (input.getText().toString().equals("")){
                                                barcodeList.get(i)[1] = "";
                                            }
                                            else{
                                                try{
                                                    Float float_barcode = Float.parseFloat(input.getText().toString());
                                                    if (float_barcode < 0 || float_barcode > 20){
                                                        String message = "Please input correct weight from 0 - 20!";
                                                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        barcodeList.get(i)[1] = input.getText().toString() + "kg";
                                                    }
                                                }
                                                catch (NumberFormatException e){

                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        };

        sp = getSharedPreferences("FILTER", MODE_PRIVATE);
        filterList = new ArrayList<>();
        if (sp.getAll().size() > 0){
            for (int i = 0; i < sp.getAll().size(); i++){
                filterList.add(sp.getString(String.valueOf(i), "null, null, null, null").split(","));
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFilter();
            }
        });

        correct = MediaPlayer.create(this, R.raw.correct);
        doh = MediaPlayer.create(this, R.raw.doh);
        barcodeList = new ArrayList<>();
        decoration = new DividerItemDecoration(recyclerView.getContext(), 1);
        filterDecoration = new DividerItemDecoration(filterRecyclerView.getContext(), 1);
        Drawable verticalDivider = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.line_divider);
        decoration.setDrawable(verticalDivider);
        filterDecoration.setDrawable(verticalDivider);
        recyclerView.addItemDecoration(decoration);
        filterRecyclerView.addItemDecoration(filterDecoration);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BarcodeListAdapter(barcodeList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setBarcodeOnClickListener(bOnClickListener);
        filterLayoutManager = new LinearLayoutManager(getApplicationContext());
        filterLayoutManager.setStackFromEnd(true);
        filterLayoutManager.setReverseLayout(true);
        filterRecyclerView.setLayoutManager(filterLayoutManager);
        fAdapter = new FilterListAdapter(filterList, this, sp);
        filterRecyclerView.setAdapter(fAdapter);
        fAdapter.notifyDataSetChanged();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBarcode();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, email);
                popup.getMenuInflater().inflate(R.menu.dropdown_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Save")){
                            if (barcodeList.size() > 0){
                                saveBarcode(barcodeList);
                                String message = "Successfully saved in path /ScannerData/.";
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = "There is no barcode to be saved.";
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (item.getTitle().equals("Load")){
                            final ListPopupWindow pw = new ListPopupWindow(getApplicationContext());
                            final String[] fileNames = getFileNames(getFiles());
                            ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                                    R.layout.listpopup_item, fileNames);
                            pw.setAdapter(arrayAdapter);
                            pw.setHeight(300);
                            pw.setWidth(300);
                            pw.setModal(false);  //if set true it will respond physical key
                            pw.setAnchorView(v);
                            pw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    if (pos > 0){
                                        File root = Environment.getExternalStorageDirectory();
                                        String path = root.getAbsolutePath() + "/ScannerData/" + fileNames[pos];
                                        try{
                                            CSVReader reader = new CSVReader(new FileReader(path));
                                            barcodeList = reader.readAll();
                                            adapter = new BarcodeListAdapter(barcodeList, MainActivity.this);
                                            recyclerView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            adapter.setBarcodeOnClickListener(bOnClickListener);
                                        }
                                        catch (Exception e){

                                        }
                                    }
                                    pw.dismiss();
                                }
                            });
                            pw.show();
                        }
                        else if (item.getTitle().equals("Send to")){
                            if (barcodeList.size() > 0){
                                File file = saveBarcode(barcodeList);
                                Uri u;
                                u = Uri.fromFile(file);
                                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"terence@ntglobal.cc"});
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, file.getName());
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, barcodeList.size() + " Barcodes.");
                                intent.putExtra(android.content.Intent.EXTRA_STREAM, u);
                                startActivity(Intent.createChooser(intent, "email to"));
                            }
                            else{
                                String message = "There is no barcode to be sent.";
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (item.getTitle().equals("Filter")){
                            filterLayout.startAnimation(fadeInAnimation);
                            filterLayout.setVisibility(View.VISIBLE);
                            barcodeInput.setEnabled(false);
                            weightInput.setEnabled(false);
                            filterLayout.setClickable(true);
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayout.startAnimation(fadeOutAnimation);
                filterLayout.setVisibility(View.GONE);
                barcodeInput.setEnabled(true);
                weightInput.setEnabled(true);
                filterLayout.setClickable(false);
            }
        });

        filterContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcodeList.size() > 0){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete All Barcodes")
                            .setMessage("Do you really want to delete all barcodes?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which){
                                    setBarcodeList(new ArrayList<String[]>());
                                    adapter = new BarcodeListAdapter(barcodeList, MainActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    adapter.setBarcodeOnClickListener(bOnClickListener);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });

        barcodeInput.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    weightInput.requestFocus();
                    return true;
                }
                return false;
            }
        });

        weightInput.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    submitBarcode();
                    return true;
                }
                return false;
            }
        });
    }

    public void setBarcodeList(List<String []> barcodeList){
        this.barcodeList = barcodeList;
    }

    public void setFilterList(List<String []> filterList) { this.filterList = filterList; }

    public void submitBarcode(){
        boolean rightOrWrong = false;
        boolean duplication = false;
        String[] barcode = new String[2];
        if (!barcodeInput.getText().toString().matches("")){
            barcode[0] = barcodeInput.getText().toString();
            if (!weightInput.getText().toString().matches("")){
                try{
                    barcode[1] = weightInput.getText().toString();
                    Float float_barcode = Float.parseFloat(barcode[1]);
                    if (float_barcode < 0 || float_barcode > 20){
                        barcode[1] = "wrong_weight";
                    }
                }
                catch (NumberFormatException e){
                    barcode[1] = "wrong_weight";
                }
                if (barcode[1].equals("wrong_weight")){
                    String message = "Please input correct weight from 0 - 20!";
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    doh.start();
                }
                else{
                    barcode[1] += "kg";
                    barcodeList.add(barcode);
                    rightOrWrong = true;
                }
            }
            else{
                barcode[1] = "";
                barcodeList.add(barcode);
                rightOrWrong = true;
            }
            if (barcodeList.size() > 1 && !barcode[1].equals("wrong_weight")){
                if (!detectBarcode(barcode[0]).equals(detectBarcode(barcodeList.get(barcodeList.size() - 2)[0]))
                        && !detectBarcode(barcode[0]).equals("Unknown Barcode")){
                    String message = "Different barcode detected: " + detectBarcode(barcode[0]);
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    doh.start();
                    barcodeList.remove(barcodeList.size() - 1);
                    rightOrWrong = false;
                }
                for (int i = 0; i < barcodeList.size() - 1; i++){
                    if (barcodeList.get(i)[0].equals(barcodeInput.getText().toString())){
                        duplication = true;
                        String message = "Duplicate barcode detected: " + (i + 1);
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        doh.start();
                        barcodeList.remove(barcodeList.size() - 1);
                        rightOrWrong = false;
                    }
                }
            }
            if (detectBarcode(barcode[0]).equals("Unknown Barcode")
                    && !barcode[1].equals("wrong_weight") && !duplication){
                doh.start();
                rightOrWrong = false;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Unknown barcode detected")
                        .setMessage("Do you really want to add this barcode in?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                barcodeList.remove(barcodeList.size() - 1);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                correct.start();
                                dialog.dismiss();
                            }
                        }).show();
            }
            if (rightOrWrong){
                correct.start();
            }
        }
        barcodeInput.setText("");
        barcodeInput.requestFocus();
        weightInput.setText("");
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void addFilter(){
        String[] s = new String[4];
        s[0] = prefixInput.getText().toString();
        s[1] = contentInput.getText().toString();
        s[2] = suffixInput.getText().toString();
        s[3] = descriptionInput.getText().toString();
        if (s[1].matches("") || s[3].matches("")){
            String message = "A filter content & a description are REQUIRED!";
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        }
        else{
            filterList.add(s);
            sp.edit().putString(String.valueOf(sp.getAll().size()),
                    s[0] + "," + s[1] + "," + s[2] + "," + s[3]).apply();
            if (filterList.size() > 1){
                for (int i = 0; i < filterList.size() - 1; i++){
                    if (filterList.get(i)[0].equals(s[0])
                            && filterList.get(i)[1].equals(s[1])
                            && filterList.get(i)[2].equals(s[2])){
                        String message = "Duplicate filter detected: " + (i + 1);
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        filterList.remove(filterList.size() - 1);
                        sp.edit().remove(String.valueOf(sp.getAll().size() - 1)).apply();
                    }
                }
            }
        }
        prefixInput.setText("");
        prefixInput.requestFocus();
        contentInput.setText("");
        suffixInput.setText("");
        descriptionInput.setText("");
        fAdapter.notifyDataSetChanged();
    }

    public File saveBarcode(List<String[]> barcodeList){
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy HH.mm.ss");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        String fileName = time + "_" + detectBarcode(barcodeList.get(0)[0])
                + "_" + barcodeList.size() + "_Barcodes.csv";
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/ScannerData");
        if (!dir.isDirectory()){
            dir.mkdirs();  // if directory doesn't exist, create one
        }
        File file = new File(dir, fileName);
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            CSVWriter writer = new CSVWriter(out);
            writer.writeAll(barcodeList);
            out.close();
        }
        catch (IOException e){

        }
        return file;
    }

    public String detectBarcode(String barcode){
        /*
        if ((barcode.length() == 12 || barcode.length() == 9)
                && barcode.substring(0, 3).equals("BNT")){
            return "EWE";
        }
        else if (barcode.length() == 13 && (barcode.substring(0, 2).equals("CJ") ||
        barcode.substring(0, 2).equals("CT")) && barcode.substring(11).equals("AU")){
            return "ChangJiang";
        }
        else if (barcode.length() == 10 && barcode.substring(0, 2).equals("KF") &&
                barcode.substring(8).equals("AU")){
            return "KF";
        }
        else if (barcode.length() == 9 &&
                (barcode.substring(0, 3).equals("BSE") || barcode.substring(0, 3).equals("LNT"))){
            return "BSE";
        }
        else if (barcode.length() == 11 && barcode.substring(0, 2).equals("FG")){
            return "FG";
        }
        else if (barcode.length() >= 3 && barcode.substring(0, 2).equals("R#")){
            return "Returned";
        }*/
        /*
        if (!filterList.isEmpty()){
            for (Map.Entry<String, String> entry : filterMap.entrySet()) {
                for (String s : entry.getValue().split("/")){
                    // if lengths are equal
                    if (s.replace(",", "").length() == barcode.length()){
                        String[] ss = s.split(",");
                        // if prefixes are equal
                        if (barcode.substring(0, ss[0].length()).equals(ss[0])){
                            return entry.getKey();
                        }
                    }
                }
            }
        }*/
        if (!filterList.isEmpty()){
            for (int i = 0; i < filterList.size(); i++){
                // if lengths are equal
                if (filterList.get(i)[0].length() + filterList.get(i)[1].length()
                        + filterList.get(i)[2].length() == barcode.length()){
                    // if prefixes are equal
                    if (barcode.substring(0, filterList.get(i)[0].length())
                            .equals(filterList.get(i)[0])){
                        return filterList.get(i)[3];
                    }
                }
            }
        }
        return "Unknown Barcode";
    }

    public File[] getFiles(){
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/ScannerData");
        if (!dir.isDirectory()){
            dir.mkdirs();  // if directory doesn't exist, create one
        }
        File[] file = dir.listFiles();
        return file;
    }

    public String[] getFileNames(File[] file){
        List<String> fileNames = new ArrayList<>();
        fileNames.add("Return");
        if (file.length == 0){
            return fileNames.toArray(new String[0]);
        }
        else{
            for (int i = 0; i < file.length; i++)
            fileNames.add(file[i].getName());
            return fileNames.toArray(new String[0]);
        }
    }
}
