package com.farid.spk.activities;
import static android.accounts.AccountManager.KEY_PASSWORD;
import static com.farid.spk.activities.LoginActivity.KEY_EMAIL;
import static com.farid.spk.activities.LoginActivity.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.farid.spk.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity  extends AppCompatActivity{
    MaterialCardView mcDiagnosa, mcKonsultasi, mcTentang, mcJadwal,
            mc_FB, mc_IG, mcWeb, mcPhone, mcLocation,
            mcLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBar();

        mcDiagnosa = findViewById(R.id.mcDiagnosa);
        mcKonsultasi = findViewById(R.id.mcKonsultasi);
        mcTentang = findViewById(R.id.mcTentang);
        mcJadwal = findViewById(R.id.mcJadwal);

        mc_FB = findViewById(R.id.mc_FB);
        mc_IG = findViewById(R.id.mc_IG);
        mcWeb = findViewById(R.id.mcWeB);
        mcPhone = findViewById(R.id.mcPhone);
        mcLocation = findViewById(R.id.mcLocation);

        mcLogout = findViewById(R.id.mcLogout);

        mcDiagnosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaftarPenyakitActivity.class);
                startActivity(intent);
            }
        });

        mcKonsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KonsultasiActivity.class);
                startActivity(intent);
            }
        });


        mcTentang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        mcJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JadwalActivity.class);
                startActivity(intent);
            }
        });


        mc_FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Buka halaman Facebook
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.facebook.com/rsud.pdgprm.14"));
                startActivity(intent);
            }
        });

        mc_IG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka halaman Instagram
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/rsud_padang_pariaman/"));
                startActivity(intent);
            }
        });

        mcWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka halaman Instagram
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://rsud.padangpariamankab.go.id/"));
                startActivity(intent);
            }
        });

        mcPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat intent untuk melakukan panggilan
                Intent intent = new Intent(Intent.ACTION_DIAL);

                // Set nomor telepon yang akan dipanggil
                intent.setData(Uri.parse("tel:0751676951"));

                // Mulai panggilan
                startActivity(intent);
            }
        });

        mcLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat intent untuk membuka Google Maps
                Intent intent = new Intent(Intent.ACTION_VIEW);

                // Set lokasi yang akan dibuka
                intent.setData(Uri.parse("geo:0,0?q=RSUD Padang Pariaman"));

                // Mulai Google Maps
                startActivity(intent);
            }
        });

        mcLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user from Firebase
                FirebaseAuth.getInstance().signOut();

                // Clear saved credentials
                clearSavedCredentials();

                // Redirect to the login page
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the MainActivity
            }
        });

    }

    private void clearSavedCredentials() {
        // Clear saved credentials using SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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

}