package com.farid.spk.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.farid.spk.model.ModelDaftarPenyakit;
import com.farid.spk.model.ModelKonsultasi;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "db_sp_penyakit_tht_rev11-test4.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase sqLiteDatabase;
    private final Context ctx;
    private boolean needUpdate = false;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/"; //path database
        this.ctx = context;

        copyDatabase();

        this.getReadableDatabase();
    }

    //fungsi untuk update BD
    public void updateDatabase() throws IOException {
        if (needUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDatabase();

            needUpdate = true;
        }
    }

    //fungsi untuk membuka koneksi ke DB
    public boolean openDatabase() throws SQLException {
        sqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return sqLiteDatabase != null;
    }

    //fungsi untuk close koneksi ke DB
    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }


    //fungsi untuk cek apakah file DB sudah ada atau tidak
    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    //fungsi untuk copy DB yang sudah dibuat sebelumnya
    private void copyDatabase() {
        if (!checkDatabase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException e) {
                throw new Error("ErrorCopyingDatabase");
            }
        }
    }

    //fungsi untuk copy DB dari folder assets
    private void copyDBFile() throws IOException {
        InputStream inputStream = ctx.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] bBuffer = new byte[1024];
        int iLength;
        while ((iLength = inputStream.read(bBuffer)) > 0)
            outputStream.write(bBuffer, 0, iLength);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    //jika versi DB lebih baru maka perlu diupdate
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            needUpdate = true;
    }

    //get list daftar penyakit
    public ArrayList<ModelDaftarPenyakit> getDaftarPenyakit() {
        ArrayList<ModelDaftarPenyakit> draftOffline = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT kode_penyakit, nama_penyakit FROM penyakit ORDER BY kode_penyakit";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ModelDaftarPenyakit modelDraftOffline = new ModelDaftarPenyakit();
                modelDraftOffline.setStrKode(cursor.getString(0));
                modelDraftOffline.setStrDaftarPenyakit(cursor.getString(1));
                draftOffline.add(modelDraftOffline);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return draftOffline;
    }

    //get list gejala
    public ArrayList<ModelKonsultasi> getDaftarGejala() {
        ArrayList<ModelKonsultasi> draftOffline = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT nama_gejala FROM gejala ORDER BY kode_gejala";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ModelKonsultasi modelDraftOffline = new ModelKonsultasi();
                modelDraftOffline.setStrGejala(cursor.getString(0));
                draftOffline.add(modelDraftOffline);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return draftOffline;
    }

}