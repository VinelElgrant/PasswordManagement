package com.example.password.management;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class SplashActivity extends AppCompatActivity {
    View mContentView;
    View content;
    Handler handler;

    Button submit;
    File passInfo;
    FileReader reader;
    BufferedReader bufferedReader;

    boolean isPassable;

    TextInputEditText getVss;

    StringManagement management=new StringManagement();

    String nordicOcean=null;

    byte[] bytePRK;
    byte[] bytePUK;

    String b64PRK,b64PUK;
    PrivateKey privateKey;
    PublicKey publicKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CheckPermission();


        String folderPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()+"/PwManagement";
        String path= getFilesDir().getPath()+"/passwords.txt";//folderPath+"/passwords.txt";


        passInfo=new File(path);
        //passInfo.delete();
        if(passInfo.exists()){
            isPassable=true;
        }else{
            isPassable=false;
            new File(folderPath).mkdirs();
            try {
                passInfo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "onCreate: "+isPassable);

        if (isPassable) {
            try {
                reader = new FileReader(passInfo);
                bufferedReader=new BufferedReader(reader);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                b64PUK=bufferedReader.readLine();
                b64PRK=bufferedReader.readLine();
                privateKey=StringManagement.getPrivateKeyFromBase64String(b64PRK);
                publicKey=StringManagement.getPublicKeyFromBase64String(b64PUK);
                nordicOcean = management.decodeString(bufferedReader.readLine(),privateKey);
                Log.d("TAG", "onCreate: "+nordicOcean.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        submit=findViewById(R.id.opening);
        content=findViewById(android.R.id.content);
        getVss=findViewById(R.id.input_pass);

        mContentView=findViewById(R.id.apaLogo);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        if(!isPassable){
            submit.setText("Create Password");
            submit.setTextSize(10);
        }

        submit.setOnClickListener(view -> {
            Intent intent=new Intent(SplashActivity.this,MainActivity.class);
            if(isPassable){
                if(nordicOcean.equals(getVss.getText().toString())){

                }else{
                    getVss.setError("Password Not Correct");
                    return;
                }
            }else {

                try {
                    writePasswords(path, String.valueOf(getVss.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            intent.putExtra("PUK",b64PUK);
            intent.putExtra("PRK",b64PRK);
            startActivity(intent);
            finish();
        });
//        handler=new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                finish();
//            }
//        },2000);

    }public void writePasswords(String passPath, String pw) throws IOException {
        File passInfo=new File(passPath);
        FileWriter writer =new FileWriter(passInfo,true);
        BufferedWriter bufferedWriter=new BufferedWriter(writer);


        KeyPair pair=null;

        try {
            pair=StringManagement.genRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        publicKey=pair.getPublic();
        privateKey=pair.getPrivate();
        bytePUK=publicKey.getEncoded();
        bytePRK=privateKey.getEncoded();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("TAG", "writePasswords: "+ b64PRK +" "+ b64PUK);
            b64PUK= Base64.getEncoder().encodeToString(bytePUK);
            b64PRK= Base64.getEncoder().encodeToString(bytePRK);
            Log.d("TAG", "writePasswords: "+ b64PRK +" "+ b64PUK);
            bufferedWriter.append(b64PUK+"\n");
            bufferedWriter.append(b64PRK+"\n");
        }
        else{
            bufferedWriter.append("\n");
            bufferedWriter.append("\n");

        }

        Log.d("TAG", "writePasswords: "+pw);
        try {
            bufferedWriter.append(management.encodeString(pw,publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bufferedWriter.close();


    }public boolean CheckPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }




}