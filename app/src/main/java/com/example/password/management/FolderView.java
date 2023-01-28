package com.example.password.management;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FolderView extends AppCompatActivity {
    private String fName;
    private String id;


    public FolderView(String fName,String id){
        this.fName=fName;
        this.id=id;
    }



    public String getfName() {
        return fName;
    }
    public String getId(){
        return id;
    }


    public void setfName(String Name){
        fName=Name;
    }

}
