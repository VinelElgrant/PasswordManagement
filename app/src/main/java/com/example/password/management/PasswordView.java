package com.example.password.management;

import java.util.Date;

public class PasswordView {

    private String site_name;

    private String identify;

    private String pswd;

    private String date;

    public PasswordView(String site_name,String identify, String pswd, String date) {
        this.site_name=site_name;
        this.identify=identify;
        this.pswd=pswd;
        this.date=date;
    }

    public String getSiteName() {
        return(site_name);
    }

    public String getIdentify(){
        return identify;
    }

    public String getPswd(){
        return pswd;
    }

    public String getDate(){
        return date;
    }


}
