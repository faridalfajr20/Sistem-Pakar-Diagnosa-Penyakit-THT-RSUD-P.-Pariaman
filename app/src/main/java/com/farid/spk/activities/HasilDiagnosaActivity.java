package com.farid.spk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.farid.spk.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.farid.spk.database.DatabaseHelper;

public class HasilDiagnosaActivity extends AppCompatActivity {

    SQLiteDatabase sqLiteDatabase;
    Toolbar toolbar;
    TextView tvGejala,tvNamaPenyakit;
    MaterialButton btnDiagnosaUlang, btnDaftarPenyakit;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_diagnosa);

        setStatusBar();

        databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.openDatabase())
            sqLiteDatabase = databaseHelper.getReadableDatabase();

        toolbar = findViewById(R.id.toolbar);
        tvGejala = findViewById(R.id.tvGejala);
        tvNamaPenyakit = findViewById(R.id.tvNamaPenyakit);
        btnDiagnosaUlang = findViewById(R.id.btnDiagnosaUlang);
        btnDaftarPenyakit = findViewById(R.id.btnDaftarPenyakit);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ArrayList<String> receivedGejala = getIntent().getStringArrayListExtra("HASIL");
        Log.d("ReceivedGejala", "Received Gejala: " + receivedGejala);

        HashMap<String, Integer> mapHasil = new HashMap<>();

        String query_penyakit = "SELECT kode_penyakit FROM penyakit order by kode_penyakit";
        Cursor cursor_penyakit = sqLiteDatabase.rawQuery(query_penyakit, null);

        while (cursor_penyakit.moveToNext()) {
            int gejala_terpilih_count = 0;

            String query_rule = "SELECT kode_gejala FROM rule where kode_penyakit = '" + cursor_penyakit.getString(0) + "'";
            Cursor cursor_rule = sqLiteDatabase.rawQuery(query_rule, null);

            while (cursor_rule.moveToNext()) {
                for (String s_gejala_terpilih : receivedGejala) {
                    String query_gejala = "SELECT kode_gejala FROM gejala where nama_gejala = '" + s_gejala_terpilih + "'";
                    Cursor cursor_gejala = sqLiteDatabase.rawQuery(query_gejala, null);

                    if (cursor_gejala.moveToFirst() && cursor_rule.getString(0).equals(cursor_gejala.getString(0))) {
//                        bobot_gabungan += 1; // Misalnya, sementara ini hanya menambahkan 1 untuk setiap kesamaan
                        gejala_terpilih_count++;
                        cursor_gejala.close();
                        break; // Keluar dari loop setelah menemukan kesamaan
                    }

                    cursor_gejala.close();
                }
            }
            cursor_rule.close();

            mapHasil.put(cursor_penyakit.getString(0), gejala_terpilih_count);
        }
        cursor_penyakit.close();

        StringBuffer output_gejala_terpilih = new StringBuffer();
        int no = 1;
        for (String s_gejala_terpilih : receivedGejala) {
            output_gejala_terpilih.append(no++)
                    .append(". ")
                    .append(s_gejala_terpilih)
                    .append("\n");
        }

        tvGejala.setText(output_gejala_terpilih);

        Map<String, Integer> sortedHasil = SortByValue(mapHasil);

        Map.Entry<String, Integer> entry = sortedHasil.entrySet().iterator().next();
        String kode_penyakit = entry.getKey();
        int gejala_terpilih_count = entry.getValue();

        String query_penyakit_hasil = "SELECT nama_penyakit FROM penyakit where kode_penyakit='" + kode_penyakit + "'";
        Cursor cursor_hasil = sqLiteDatabase.rawQuery(query_penyakit_hasil, null);
        cursor_hasil.moveToFirst();

        tvNamaPenyakit.setText(cursor_hasil.getString(0));

        cursor_hasil.close();

        btnDiagnosaUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receivedGejala.clear();

                // Reset diagnosa
                Intent intent = new Intent(HasilDiagnosaActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });


        btnDaftarPenyakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HasilDiagnosaActivity.this, DaftarPenyakitActivity.class);
                startActivity(intent);
            }
        });
    }

    public static HashMap<String, Integer> SortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}