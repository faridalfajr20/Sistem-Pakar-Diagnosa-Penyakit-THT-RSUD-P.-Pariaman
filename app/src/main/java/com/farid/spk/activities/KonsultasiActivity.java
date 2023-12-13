package com.farid.spk.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farid.spk.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import com.farid.spk.adapter.KonsultasiAdapter;
import com.farid.spk.database.DatabaseHelper;
import com.farid.spk.model.ModelKonsultasi;

public class KonsultasiActivity extends AppCompatActivity{

    SQLiteDatabase sqLiteDatabase;
    KonsultasiAdapter konsultasiAdapter;
    ArrayList<ModelKonsultasi> modelKonsultasiArrayList = new ArrayList<>();
    DatabaseHelper databaseHelper;
    Toolbar toolbar;
//    RecyclerView rvGejalaPenyakit;
    MaterialButton btnHasilDiagnosa;

    // Variabel global untuk menyimpan gejala yang dipilih
    ArrayList<String> gejalaTerpilihList = new ArrayList<>();

    private LinearLayout questionContainer;
    private int currentQuestionIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);

        setStatusBar();

        databaseHelper = new DatabaseHelper(this);
        if (databaseHelper.openDatabase())
            sqLiteDatabase = databaseHelper.getReadableDatabase();

        toolbar = findViewById(R.id.toolbar);
        btnHasilDiagnosa = findViewById(R.id.btnHasilDiagnosa);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

//        rvGejalaPenyakit.setLayoutManager(new LinearLayoutManager(this));
        konsultasiAdapter = new KonsultasiAdapter(this, modelKonsultasiArrayList);
//        rvGejalaPenyakit.setAdapter(konsultasiAdapter);
//        rvGejalaPenyakit.setHasFixedSize(true);

        btnHasilDiagnosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ModelKonsultasi> gejalaList = modelKonsultasiArrayList;
                for (int i = 0; i < gejalaList.size(); i++) {
                    ModelKonsultasi gejala = gejalaList.get(i);
                    if (gejala.isSelected()) {
                        gejalaTerpilihList.add(gejala.getStrGejala());
                    }
                }

                if (gejalaTerpilihList.isEmpty()) {
                    Toast.makeText(KonsultasiActivity.this, "Silahkan pilih gejala dahulu!", Toast.LENGTH_SHORT).show();

                } else {
                    // Tampilkan activity hasil diagnosa
                    Intent intent = new Intent(v.getContext(), HasilDiagnosaActivity.class);
                    intent.putStringArrayListExtra("HASIL", gejalaTerpilihList);
                    Log.d("SelectedGejala", "Selected Gejala: " + gejalaTerpilihList.toString());
                    startActivity(intent);
                }
            }
        });

        getListData();

        questionContainer = findViewById(R.id.questionContainer);

        // Tampilkan pertanyaan dan tombol "Ya" dan "Tidak" untuk pertama kali
        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int questionIndex) {
        // Bersihkan kontainer pertanyaan
        questionContainer.removeAllViews();

        if (questionIndex < modelKonsultasiArrayList.size()) {
            ModelKonsultasi gejala = modelKonsultasiArrayList.get(questionIndex);

            // Buat tata letak untuk pertanyaan dan tombol "Ya" dan "Tidak"
            View questionLayout = LayoutInflater.from(this).inflate(R.layout.question_layout, questionContainer, false);
            TextView tvQuestion = questionLayout.findViewById(R.id.tvQuestion);
            MaterialButton btnYes = questionLayout.findViewById(R.id.btnYes);
            MaterialButton btnNo = questionLayout.findViewById(R.id.btnNo);

            // Tampilkan pertanyaan
            tvQuestion.setText(gejala.getStrGejala());

            // Atur listener untuk tombol "Ya" dan "Tidak"
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAnswerSelected(true);
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAnswerSelected(false);
                }
            });

            // Tambahkan tata letak ke kontainer pertanyaan
            questionContainer.addView(questionLayout);
        }
//        else {
//            // Jika sudah mencapai pertanyaan terakhir, tampilkan tombol "Hasil Diagnosa"
//            questionContainer.addView(btnHasilDiagnosa);
//        }
    }

    private void onAnswerSelected(boolean answer) {
        // Simpan jawaban pengguna
        modelKonsultasiArrayList.get(currentQuestionIndex).setSelected(answer);

        // Pindah ke pertanyaan berikutnya
        currentQuestionIndex++;

        // Tampilkan pertanyaan berikutnya atau tombol "Hasil Diagnosa"
        displayQuestion(currentQuestionIndex);
    }

    private void getListData() {
        modelKonsultasiArrayList = databaseHelper.getDaftarGejala();

        modelKonsultasiArrayList = databaseHelper.getDaftarGejala();
        for (ModelKonsultasi gejala : modelKonsultasiArrayList) {
            Log.d("Gejala", "Gejala: " + gejala.getStrGejala() + ", Selected: " + gejala.isSelected());
        }

//        if (modelKonsultasiArrayList.size() == 0) {
//            rvGejalaPenyakit.setVisibility(View.GONE);
//        } else {
//            rvGejalaPenyakit.setVisibility(View.VISIBLE);
//            konsultasiAdapter = new KonsultasiAdapter(this, modelKonsultasiArrayList);
//            rvGejalaPenyakit.setAdapter(konsultasiAdapter);
//        }
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

    @Override
    protected void onResume() {
        super.onResume();
        getListData();
    }
}