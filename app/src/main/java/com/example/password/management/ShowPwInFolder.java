package com.example.password.management;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.password.management.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowPwInFolder extends AppCompatActivity{
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    StringManagement management=new StringManagement();
    ConfigSettings configSettings;

    TextInputEditText site_name,identical_number,password;

    Button generate_random,submit_button,cancel_button;

    final ArrayList<PasswordView> passwordViews=new ArrayList<>();

    PwViewAdapter adapter;

    SimpleDateFormat mine=new SimpleDateFormat("yyyy MM dd");

    Date date= new Date();

    int count;

    PassSecure secure =new PassSecure();
    ListView PwListView;
    String folderId;
    String passPath;

    String b64PUK,b64PRK;
    PublicKey publicKey;
    PrivateKey privateKey;

    Switch showDetails;
    LinearLayout detailedOptions;

    EditText passLength;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem menuItem =menu.findItem(R.id.search_passes);

        SearchView searchView=(SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search for Name, ID");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<PasswordView> list=new ArrayList<PasswordView>();

                for(PasswordView view:passwordViews){
                        if(view.getSiteName().toLowerCase().contains(s.toLowerCase())
                                || view.getIdentify().toLowerCase().contains(s.toLowerCase())
                                || view.getPswd().toLowerCase().contains(s.toLowerCase())){
                            list.add(view);
                        }
                    }
                PwViewAdapter pwViewAdapter=new PwViewAdapter(getApplicationContext(),list);

                PwListView.setAdapter(pwViewAdapter);

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_folder_show_pw);


        Intent intent = getIntent();

        String FolderName = intent.getStringExtra("folderName");
        folderId = intent.getStringExtra("folderID");

        String folderPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()+"/PwManagement";

        passPath=getFilesDir().getPath()+"/"+folderId+".txt";//folderPath+"/"+folderId+".txt";
        b64PUK=intent.getStringExtra("PUK");
        b64PRK=intent.getStringExtra("PRK");
        count=intent.getIntExtra("folderCount",0);

        try {
            Log.d(TAG, "onCreate: "+b64PRK+b64PUK);
            publicKey=StringManagement.getPublicKeyFromBase64String(b64PUK);
            privateKey=StringManagement.getPrivateKeyFromBase64String(b64PRK);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(FolderName);
        ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        ImageButton add_pwd=findViewById(R.id.add_pwd);

        PwListView=findViewById(R.id.passWord);

        adapter=new PwViewAdapter(this,passwordViews);

        //deleteFiles(passPath);
        try {
            readPasswords(passPath);
        } catch (Exception e) {
            e.printStackTrace();
        }



        add_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPopupWindow(null,null);
            }
        });
        PwListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                PasswordView crntPos=(PasswordView) PwListView.getItemAtPosition(pos);

                buttonPopupWindow(crntPos,pos);
            }
        });

    }
    public String capital="ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            lower= "qwertyuiopasdfghjklzxcvbnm",
            number="0123456789",
            spec="!@#$%^&*`~_+-=" ;//Available Characters for Random password


    //Get random number for random index of Available
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    //Get random Password
    public void getRandomPW(TextInputEditText editText,int len){
        StringBuilder RPW=new StringBuilder();

        RPW.append((lower+number).charAt(getRandomNumber(0,(lower+number).length())));
        int randomIndex=getRandomNumber(0,len-3);
        for (int i=0;i<len-3;i++)
        {
            if(i==randomIndex){
                RPW.append(spec.charAt(getRandomNumber(0,spec.length())));
            }
            RPW.append((capital+lower+number).charAt(getRandomNumber(0,(capital+lower+number).length())));
        }
        RPW.append((capital+lower+number).charAt(getRandomNumber(0,(capital+lower+number).length())));

        editText.setText(RPW.toString(),TextInputEditText.BufferType.EDITABLE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void buttonPopupWindow(@Nullable PasswordView pass, @Nullable Integer pos){

        Dialog dialog =new Dialog(this);
        dialog.setContentView(R.layout.password_input);
        dialog.show();

        site_name=dialog.findViewById(R.id.editTextTextSiteName);
        identical_number=dialog.findViewById(R.id.editTextTextIdentical);
        password=dialog.findViewById(R.id.editTextTextPassword);

        showDetails=dialog.findViewById(R.id.show_details);
        detailedOptions=dialog.findViewById(R.id.detailed_options);

        passLength=dialog.findViewById(R.id.passLength);


        TextInputLayout passwordHint=dialog.findViewById(R.id.changeHintOnly);

        if(pass!=null)
        {
            site_name.setText(pass.getSiteName());
            identical_number.setText(pass.getIdentify());
            password.setText(pass.getPswd());
        }

        generate_random=dialog.findViewById(R.id.RandomPW);
        submit_button=dialog.findViewById(R.id.submit_button);
        cancel_button=dialog.findViewById(R.id.cancel_button);


        generate_random.setOnClickListener(view -> {
            if(passLength.getText().toString().matches("")){
                passLength.setError("Please enter Password length");
                return;
            }
            int len=Integer.parseInt(passLength.getText().toString());
            if(len>30||len<8){
                passLength.setError("Random Password can only be 8 to 30 letters");
                return;
            }
            getRandomPW(password,len);

        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input=charSequence.toString();
                showSecurity(input,passwordHint);
                int cnt=0;
                try {
                    cnt=countPasswords(input,count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(cnt!=0)
                    password.setError("You are using this for "+cnt+" other sites");
                else
                    password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        showDetails.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                detailedOptions.setVisibility(View.VISIBLE);
            }else {
                detailedOptions.setVisibility(View.GONE);
            }
        });
        //If pressed Submit Button
        submit_button.setOnClickListener(view -> {

            String site=site_name.getText().toString();
            String id=identical_number.getText().toString();
            String pw=password.getText().toString();

            secure.CheckSecure(pw);

            editList(site,id,pw,pos);
            dialog.dismiss();
        });
        //If pressed Cancel Button
        cancel_button.setOnClickListener(view -> dialog.dismiss());

    }
    public void editList(String site, String id, String pw,@Nullable Integer pos){
        PasswordView view=new PasswordView(site,id,pw,mine.format(date));


        if(pos==null) {
            passwordViews.add(view);
            try {
                editPasswords(passPath,view,null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            passwordViews.set(pos ,view);
            try {
                editPasswords(passPath,view,pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PwListView.setAdapter(adapter);
    }



    public void readPasswords(String path) throws Exception {
        File passInfo=new File(path);
        FileReader reader=new FileReader(passInfo);
        BufferedReader bufferedReader=new BufferedReader(reader);

        //Log.d("fuck",Integer.toString(Files.readAllLines(passInfo.toPath()).size()));
        while (bufferedReader.ready()){
            String fol[]=management.decodeString(bufferedReader.readLine(),privateKey).split("\\|",4);
            passwordViews.add(new PasswordView(fol[0],fol[1],fol[3],fol[2]));
            PwListView.setAdapter(adapter);
            //Log.d("parameters",fol[0]+fol[1]+fol[2]+fol[3]);
        }
        Log.d(TAG, "readPasswords: "+path);
        bufferedReader.close();
    }

    public int countPasswords(String word,int count) throws Exception {
        int samePass=0;
        for(int i=1;i<=count;i++){
            String Path=getFilesDir().getPath()+"/"+ i +".txt";
            File passInfo=new File(Path);
            if(passInfo.exists()) {
                FileReader reader = new FileReader(passInfo);
                BufferedReader bufferedReader = new BufferedReader(reader);
                while (bufferedReader.ready()) {
                    String pass[] = management.decodeString(bufferedReader.readLine(), privateKey).split("\\|", 4);
                    if (pass[3].equals(word)) {
                        samePass++;
                    }
                }
            }
        }
        Log.d(TAG, "countPasswords: "+samePass);
        return samePass;
    }

    public void editPasswords(String path, PasswordView view,@Nullable Integer pos) throws Exception {

        File passInfo = new File(path);

        if(pos!=null) {
            passInfo.delete();
            File rewritePass=new File(path);
            FileWriter writer = new FileWriter(rewritePass,true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            int checkPosition=0;
            for(PasswordView Pv:passwordViews){
                if(checkPosition==pos){
                    bufferedWriter.append(management.encodeString(Pv.getSiteName() + "|" + Pv.getIdentify()  + "|" + Pv.getDate() + "|" + Pv.getPswd(),publicKey) + "\n");
                }else {
                    bufferedWriter.append(management.encodeString(Pv.getSiteName() + "|" + Pv.getIdentify()  + "|" + Pv.getDate() + "|" + Pv.getPswd(),publicKey) + "\n");
                }
            }
            bufferedWriter.close();

        }
        else {
            FileWriter writer = new FileWriter(passInfo, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.append(management.encodeString(view.getSiteName() + "|" + view.getIdentify()  + "|" + view.getDate() + "|" + view.getPswd(),publicKey) + "\n");

            bufferedWriter.close();
        }
    }
    public void deleteFiles(String path){
        File file=new File(path);
        if(file.exists()){
            file.delete();
        }
    }
    public void showSecurity(String pass, TextInputLayout view){
        switch(secure.CheckSecure(pass)){
            case 1:
                view.setHint("too low");
                view.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.red)));
                break;
            case 2:
                view.setHint("low");
                view.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.orange)));
                break;
            case 3:
                view.setHint("Common");
                view.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.yellow_500)));
                break;
            case 4:
                view.setHint("HIGH");
                view.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.lime_500)));
                break;
            case 5:
                view.setHint("VERY HIGH");
                view.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.green_200)));
                break;



        }
    }

}
