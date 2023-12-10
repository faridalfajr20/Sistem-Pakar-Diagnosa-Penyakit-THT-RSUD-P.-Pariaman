package com.farid.spk.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
    TextView tvGejala,tvNamaPenyakit, tvListPenyakit;
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

        tvListPenyakit = findViewById(R.id.tvListPenyakit);

        btnDiagnosaUlang = findViewById(R.id.btnDiagnosaUlang);
        btnDaftarPenyakit = findViewById(R.id.btnDaftarPenyakit);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String str_hasil = getIntent().getStringExtra("HASIL");
        String[] gejala_terpilih = new String[0];
        if (str_hasil != null) {
            gejala_terpilih = str_hasil.split("#");
        }

        double bobot_gabungan;
        double bobot;
        HashMap<String, Double> mapHasil = new HashMap<>();

        String query_penyakit = "SELECT kode_penyakit FROM penyakit order by kode_penyakit";
        Cursor cursor_penyakit = sqLiteDatabase.rawQuery(query_penyakit, null);

        while (cursor_penyakit.moveToNext()) {
            bobot_gabungan = (double) 0;
            int i = 0;
            String query_rule = "SELECT nilai_bobot, kode_gejala FROM rule where kode_penyakit = '" + cursor_penyakit.getString(0) + "'";
            Cursor cursor_rule = sqLiteDatabase.rawQuery(query_rule, null);
            while (cursor_rule.moveToNext()) {
                bobot = cursor_rule.getDouble(0);
                for (String s_gejala_terpilih : gejala_terpilih) {
                    String query_gejala = "SELECT kode_gejala FROM gejala where nama_gejala = '" + s_gejala_terpilih + "'";
                    Cursor cursor_gejala = sqLiteDatabase.rawQuery(query_gejala, null);
                    cursor_gejala.moveToFirst();
                    if (cursor_rule.getString(1).equals(cursor_gejala.getString(0))) {
                        // Memeriksa apakah gejala dari aturan (rule) sama dengan gejala yang dimasukkan
                        if (i > 1) {
                            // Jika gejala sudah lebih dari satu (i > 1)
                            bobot_gabungan = bobot + (bobot_gabungan * (1 - bobot));
                            // Menghitung bobot gabungan dengan rumus
                            // bobot_gabungan = bobot + (bobot_gabungan * (1 - bobot))
                        } else if (i == 1) {
                            // Jika gejala baru satu (i == 1)
                            bobot_gabungan = bobot_gabungan + (bobot * (1 - bobot_gabungan));
                            // Menghitung bobot gabungan dengan rumus
                            // bobot_gabungan = bobot_gabungan + (bobot * (1 - bobot_gabungan))
                        } else {
                            // Jika ini gejala pertama (i == 0)
                            bobot_gabungan = bobot;
                            // Assign bobot ke bobot_gabungan
                        }
                        i++;
                        // Increment i untuk menandakan bahwa telah memproses satu gejala
                    }
                    cursor_gejala.close();
                }
            }
            cursor_rule.close();
            mapHasil.put(cursor_penyakit.getString(0), bobot_gabungan * 100);

        }
        cursor_penyakit.close();

        StringBuilder listPenyakit = new StringBuilder();
        for (Map.Entry<String, Double> entry : SortByValue(mapHasil).entrySet()) {
            String kode_penyakit = entry.getKey();
            double hasil_bobot = entry.getValue();
            int persentase = (int) hasil_bobot;

            String query_penyakit_hasil = "SELECT nama_penyakit FROM penyakit where kode_penyakit='" + kode_penyakit + "'";
            Cursor cursor_hasil = sqLiteDatabase.rawQuery(query_penyakit_hasil, null);
            cursor_hasil.moveToFirst();

            listPenyakit.append(cursor_hasil.getString(0)).append(": ").append(persentase).append("%\n");

            cursor_hasil.close();
        }

        tvListPenyakit.setText(listPenyakit.toString());

        StringBuffer output_gejala_terpilih = new StringBuffer();
        int no = 1;
        for (String s_gejala_terpilih : gejala_terpilih) {
            output_gejala_terpilih.append(no++)
                    .append(". ")
                    .append(s_gejala_terpilih)
                    .append("\n");
        }

        tvGejala.setText(output_gejala_terpilih);

        Map<String, Double> sortedHasil = SortByValue(mapHasil);

        Map.Entry<String, Double> entry = sortedHasil.entrySet().iterator().next();
        String kode_penyakit = entry.getKey();
        double hasil_bobot = entry.getValue();
        int persentase = (int) hasil_bobot;

        String query_penyakit_hasil = "SELECT nama_penyakit FROM penyakit where kode_penyakit='" + kode_penyakit + "'";
        Cursor cursor_hasil = sqLiteDatabase.rawQuery(query_penyakit_hasil, null);
        cursor_hasil.moveToFirst();

        tvNamaPenyakit.setText(cursor_hasil.getString(0) + " " + persentase + "%");

        cursor_hasil.close();

        btnDiagnosaUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public static HashMap<String, Double> SortByValue(HashMap<String, Double> hm) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
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
