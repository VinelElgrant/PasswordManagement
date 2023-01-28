package com.example.password.management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class fViewAdapter extends ArrayAdapter<FolderView> {


    LinearLayout folder;

    public fViewAdapter(@Nullable Context context, ArrayList<FolderView> folderViews){
        super(context,0,folderViews);
    }



    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View currentView=convertView;

        if(convertView==null) {
            currentView= LayoutInflater.from(getContext()).inflate(R.layout.folder_adapter,parent,false);
        }

        FolderView currentPos=getItem(position);


        TextView fName=currentView.findViewById(R.id.folderName);
        fName.setText(currentPos.getfName());


        folder=currentView.findViewById(R.id.listFolder);



        return currentView;
    }


}
