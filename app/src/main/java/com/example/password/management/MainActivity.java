package com.example.password.management;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.password.management.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;


    TextInputEditText folder_name_edit;

    Button folderSubmit;

    String b64PUK,b64PRK;


    final ArrayList<FolderView> folderList=new ArrayList<>();
//
//    SimpleDateFormat mine=new SimpleDateFormat("yyyy MM dd");
//
//    Date date= new Date();



    fViewAdapter folderAdapter;

    ListView folderView;
    ConstraintLayout addFolder;
//
//    Spinner selectFolder;
    String filePath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent openPwViewActivity=new Intent(MainActivity.this, ShowPwInFolder.class);
        Intent getIntent=getIntent();
//        String folderPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()+"/PwManagement";
//        String path=getFilesDir().getPath()+"/passwords.txt";// folderPath+"/passwords.txt";

        filePath=getFilesDir().getPath()+"/FolderNames.txt";//folderPath+"/FolderNames.txt";//
//        Log.d("TAG", "onCreate: "+ Environment.getExternalStoragePublicDirectory("/PwManagement/FolderNames.txt").toString());

        folderAdapter=new fViewAdapter(this,folderList);

        folderView=findViewById(R.id.pass_words);

        addFolder=findViewById(R.id.addFolder);

        //`deleteFiles(filePath);
        try {
            readFolders(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        folderView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                FolderView folder=(FolderView) folderView.getItemAtPosition(pos);
                b64PRK=getIntent.getStringExtra("PRK");
                b64PUK=getIntent.getStringExtra("PUK");
                Log.d("TAG", "MainActivities: "+ b64PRK +" "+ b64PUK);
                openPwViewActivity.putExtra("folderName",folder.getfName());
                openPwViewActivity.putExtra("folderID",folder.getId());
                openPwViewActivity.putExtra("folderCount",folderList.size());
                openPwViewActivity.putExtra("PUK",b64PUK);
                openPwViewActivity.putExtra("PRK",b64PRK);
                MainActivity.this.startActivity(openPwViewActivity);


            }
        });


//
//        add_pwd.setOnClickListener(view -> buttonPopupWindow(null,null));
//        listView.setOnItemClickListener((adapterView, view, pos, l) -> {
//            PasswordView password=(PasswordView) listView.getItemAtPosition(pos);
//
//            buttonPopupWindow(password, pos);
//        });
        addFolder.setOnClickListener(view -> popupAddFolder());

    }

//
//
//    public String available="ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxvnm0123456789" ;//Available Characters for Random password
//
//    //Get random number for random index of Available
//    public int getRandomNumber(int min, int max) {
//        return (int) ((Math.random() * (max - min)) + min);
//    }
//
//    //Get random Password
//    public void getRandomPW(TextInputEditText editText){
//        StringBuilder RPW=new StringBuilder();
//
//        for (int i=0;i<10;i++)
//        {
//            RPW.append(available.charAt(getRandomNumber(0,available.length())));
//        }
//        editText.setText(RPW.toString(),TextInputEditText.BufferType.EDITABLE);
//
//    }
//
//
//    public void editList(int folderPos, String site, String id, String pw,@Nullable Integer pos){
//
//        if(pos==null) {
//            pswrdList.get(folderPos).add(new PasswordView(site, id, pw, mine.format(date)));
//
//        }else{
//            pswrdList.get(folderPos).set(pos ,new PasswordView(site, id, pw, mine.format(date)));
//        }
//
//
//        folderList.get(folderPos).setPasswordList(listViews.get(folderPos));
//
//        folderList.get(folderPos).setPasswordViewArrayList(pswrdList.get(folderPos));
//
//        folderList.get(folderPos).setViewAdapter(pwViewAdapter.get(folderPos));
//
//        folderAdapter.pswrdList=pswrdList.get(folderPos);
//
//        folderAdapter.listView.setAdapter(pwViewAdapter.get(folderPos));
//
//        folderAdapter.notifyDataSetChanged();
//        pwViewAdapter.get(folderPos).notifyDataSetChanged();
//
//        folderView.setAdapter(folderAdapter);
//        if(listViews.get(folderPos)!=null) {
//            listViews.get(folderPos).setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
//                    PasswordView password = (PasswordView) listViews.get(folderPos).getItemAtPosition(pos);
//
//                    buttonPopupWindow(password, pos);
//                }
//            });
//        }else{
//            Toast.makeText(getApplicationContext(),folderList.get(folderPos).getfName(),Toast.LENGTH_SHORT).show();
//        }
//        //listView.setAdapter(pwViewAdapter);
//    }

    public void popupAddFolder(){
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.folder_name_input);
        dialog.show();


        folder_name_edit=dialog.findViewById(R.id.EditFolderName);

        folderSubmit=dialog.findViewById(R.id.submit_folder_button);

        folderSubmit.setOnClickListener(view -> {
            String folder_name=folder_name_edit.getText().toString();
            if(TextUtils.isEmpty(folder_name))
            {
                folder_name_edit.setError("Folder Name Cannot be Empty");
                return;
            }

            folderList.add(new FolderView(folder_name,Integer.toString(folderList.size()+1)));
            try {
                writeFolders(filePath,folder_name,folderList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

            folderView.setAdapter(folderAdapter);

            dialog.dismiss();
        });

    }
//
//    public void buttonPopupWindow(@Nullable PasswordView pass, @Nullable Integer pos){
//        if(folderView.getCount()==0)
//        {
//            Toast.makeText(getApplicationContext(),"Make A Folder",Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        Dialog dialog =new Dialog(this);
//        dialog.setContentView(R.layout.password_input);
//        dialog.show();
//        selectFolder=dialog.findViewById(R.id.selectFolder);
//        site_name=dialog.findViewById(R.id.editTextTextSiteName);
//        identical_number=dialog.findViewById(R.id.editTextTextIdentical);
//        password=dialog.findViewById(R.id.editTextTextPassword);
//
//
//        if(pass!=null)
//        {
//            site_name.setText(pass.getSiteName());
//            identical_number.setText(pass.getIdentify());
//            password.setText(pass.getPswd());
//        }
//
//        generate_random=dialog.findViewById(R.id.RandomPW);
//        submit_button=dialog.findViewById(R.id.submit_button);
//        cancel_button=dialog.findViewById(R.id.cancel_button);
//
//        ArrayList<String> fd=new ArrayList<>();
//        for(int i=0;i<folderView.getCount();i++)
//        {
//            fd.add(folderList.get(i).getfName());
//        }
//
//        ArrayAdapter adapter= new ArrayAdapter(this, android.R.layout.simple_spinner_item,fd);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        selectFolder.setAdapter(adapter);
//
//
//        generate_random.setOnClickListener(view -> getRandomPW(password));
//
//        //If pressed Submit Button
//        submit_button.setOnClickListener(view -> {
//            String site=site_name.getText().toString();
//            String id=identical_number.getText().toString();
//            String pw=password.getText().toString();
//            int folderPos =selectFolder.getSelectedItemPosition();
//
//            editList(folderPos,site,id,pw,pos);
//            dialog.dismiss();
//        });
//        //If pressed Cancel Button
//        cancel_button.setOnClickListener(view -> dialog.dismiss());
//
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void readFolders(String path) throws IOException {
        File folderNames=new File(path);
        FileReader reader=new FileReader(folderNames);
        BufferedReader bufferedReader=new BufferedReader(reader);

        while (bufferedReader.ready()){
            String fol[]=bufferedReader.readLine().split("\\+",2);
            folderList.add(new FolderView(fol[1],fol[0]));
        }
        folderView.setAdapter(folderAdapter);
        bufferedReader.close();
    }
    public void writeFolders(String path,String folder,int len) throws IOException {
        File folderNames=new File(path);
        FileWriter writer =new FileWriter(folderNames,true);
        BufferedWriter bufferedWriter=new BufferedWriter(writer);

        bufferedWriter.append(len +"+"+folder+"\n");
        bufferedWriter.close();
    }
    public void deleteFiles(String path){
        File file=new File(path);
        if(file.exists()){
            file.delete();
        }
    }

}