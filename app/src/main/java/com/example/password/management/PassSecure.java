package com.example.password.management;

import android.util.Log;

public class PassSecure {


    public int CheckSecure(String pass){
        String capital="QWERTYUIOPASDFGHJKKLZXCVBNM";
        String lower="qwertyuiopasdfghjklzxcvbnm";
        String spec="!@#$%^&*`~_+-=";

        int count=0;
        for(char c:capital.toCharArray()){
            if(pass.indexOf(c)!=-1){
                count+=2;
                for(char d:capital.toCharArray()){
                    if(pass.charAt(0)==d){
                        count--;
                        break;
                    }
                }
                break;
            }
        }
        for(char c:lower.toCharArray()){
            if(pass.indexOf(c)!=-1){
                count++;
                break;
            }
        }
        for(char c:spec.toCharArray()){
            if(pass.indexOf(c)!=-1){
                count+=2;
                for(char d:spec.toCharArray()){
                    if(pass.lastIndexOf(d)==pass.length()-1
                            || pass.indexOf(d)==pass.charAt(0)){
                        count--;
                        break;
                    }
                }
                break;
            }
        }


        return count;
    }


}
