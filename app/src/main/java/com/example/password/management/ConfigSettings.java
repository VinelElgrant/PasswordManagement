package com.example.password.management;

import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ConfigSettings extends AppCompatActivity {

    private String configString;
    private File configFile;

//    private FileInputStream configInStream=new FileInputStream(configFile);
//    private FileOutputStream configOutStream=new FileOutputStream(configFile);

    private FileReader reader;
    private FileWriter writer;

    private BufferedReader configReader;
    private BufferedWriter configWriter;

    public ConfigSettings(String path) throws IOException {
        this.configString=path;
        this.configFile=new File(configString);
        this.reader=new FileReader(configFile);
        this.writer=new FileWriter(configFile);
        this.configReader=new BufferedReader(reader);
        this.configWriter=new BufferedWriter(writer);
    }

    public void setPassLength(int length) throws IOException {
        //configFile.delete();
        String setLen=Integer.toString(length);
        if(configFile.exists()){
            String getLen=Integer.toString(getPassLength());
            Scanner sc = new Scanner(configFile);
            //instantiating the StringBuffer class
            StringBuffer buffer = new StringBuffer();
            //Reading lines of the file and appending them to StringBuffer
            while (sc.hasNextLine()) {
                buffer.append(sc.nextLine()+System.lineSeparator());
            }
            String fileContents = buffer.toString();

            //closing the Scanner object
            sc.close();

            //Replacing the old line with new line
            fileContents = fileContents.replaceAll("passLength"+getLen, "passLength"+setLen);
            //instantiating the FileWriter class
            FileWriter writer = new FileWriter(configFile);

            writer.append(fileContents);
            writer.flush();
            writer.close();
        }else{
            configWriter.append("passLength"+setLen);
        }
        Log.d("TAG", "setPassLength: "+setLen);



    }
    public int getPassLength() throws IOException {
        int len=10;
        while(configFile.exists()&&configReader.ready()){
            Log.d("TAG", "getPassLength: "+len);
            String s=configReader.readLine();
            if(s.contains("passLength")){
                s.replace("passLength","");
                len=Integer.parseInt(s);
                break;
            }
        }
        resetReadBuffer();
        Log.d("TAG", "getPassLength: "+len);
        return len;
    }

    public void resetReadBuffer() throws IOException {
        configReader.close();
        reader=new FileReader(configFile);
        configReader=new BufferedReader(reader);


    }
    public void resetWriteBuffer() throws IOException {
        configWriter.close();
        writer=new FileWriter(configFile);
        configWriter=new BufferedWriter(writer);
    }


}
