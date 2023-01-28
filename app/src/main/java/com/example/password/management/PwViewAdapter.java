package com.example.password.management;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;


public class PwViewAdapter extends ArrayAdapter<PasswordView> {
    Context mContext;

    DateCalcuate calcuate=new DateCalcuate();

    ArrayList<PasswordView> passwordViews;

    PassSecure secure=new PassSecure();

    public PwViewAdapter(@NonNull Context context, ArrayList<PasswordView> passwordViews){
        super(context,0,passwordViews);
        this.mContext=context;
        this.passwordViews=passwordViews;
    }

    Button copyPass;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View crntView=convertView;

        if(crntView==null){
            crntView= LayoutInflater.from(getContext()).inflate(R.layout.cust_adap,parent,false);

        }
        copyPass=crntView.findViewById(R.id.copyPass);

        PasswordView crntPos=getItem(position);

        TextView site=crntView.findViewById(R.id.site_name);
        site.setText(crntPos.getSiteName());

        TextView ident=crntView.findViewById(R.id.identifier);
        ident.setText(crntPos.getIdentify());

        TextView pass=crntView.findViewById(R.id.pswd);
        pass.setText(crntPos.getPswd());

        TextView days=crntView.findViewById(R.id.dates);
        String das="0days";
        try {
            das=Long.toString(calcuate.getDaysByToday(crntPos.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showDays(das,days);

        TextView secure=crntView.findViewById(R.id.how_safe);
        showSecurity(crntPos.getPswd(),secure);


        copyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",pass.getText());
                clipboard.setPrimaryClip(clip);
            }
        });

        return crntView;
    }

    public void showSecurity(String pass,TextView view){
        switch(secure.CheckSecure(pass)){
            case 1:
                view.setText("too low");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.red));
                break;
            case 2:
                view.setText("low");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
                break;
            case 3:
                view.setText("Common");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.yellow_500));
                break;
            case 4:
                view.setText("HIGH");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.lime_500));
                break;
            case 5:
                view.setText("VERY HIGH");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.green_200));
                break;
            default:
                view.setText("Not Set");
                view.setTextColor(ContextCompat.getColor(getContext(),R.color.black));


        }
    }
    public void showDays(String das,TextView view){
        int days=Integer.parseInt(das);
        String safe;
        if(days>270) {
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            safe="매우위험";
        }
        else if(days>180){
            view.setTextColor(ContextCompat.getColor(getContext(),R.color.orange));
            safe="위험";
        }
        else if (days>90) {
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow_500));
            safe="경고";
        }
        else {
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.green_200));
            safe="안전";
        }
        view.setText(das+" days, "+safe);
    }
//    @NonNull
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraints) {
//                FilterResults results=new FilterResults();
//
//                if(constraints==null||constraints.length()==0){
//                    results.values=passwordViews;
//                    results.count=passwordViews.size();
//                }
//                else{
//                    ArrayList<PasswordView> filteredPasswordViews=new ArrayList<PasswordView>();
//
//                    for(PasswordView view:passwordViews){
//                        if(view.getSiteName().toUpperCase().contains(constraints.toString().toUpperCase())
//                                || view.getIdentify().toUpperCase().contains(constraints.toString().toUpperCase())
//                                || view.getPswd().toUpperCase().contains(constraints.toString().toUpperCase())){
//                            filteredPasswordViews.add(view);
//                        }
//                    }
//
//                    results.values=filteredPasswordViews;
//                    results.count=filteredPasswordViews.size();
//
//                }
//
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                passwordViews=(ArrayList<PasswordView>) filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
}
