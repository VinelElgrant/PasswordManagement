package com.example.password.management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.password.management.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class InputPassword extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    TextInputEditText site_name,identical_number,password;
    Button generate_random;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_input);

        site_name=findViewById(R.id.editTextTextSiteName);
        identical_number=findViewById(R.id.editTextTextIdentical);
        password=findViewById(R.id.editTextTextPassword);

        TextView write=findViewById(R.id.textView);

        generate_random=findViewById(R.id.RandomPW);

        generate_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRandomPW(password);

            }
        });


    }
    public String available="ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxvnm" ;
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public void getRandomPW(TextInputEditText editText){
        char RPW[]=new char[10];
        for (int i=0;i<10;i++)
        {
            RPW[i]=available.charAt(getRandomNumber(0,available.length()-1));
        }
        editText.setText("asdf",TextInputEditText.BufferType.EDITABLE);

    }

}
