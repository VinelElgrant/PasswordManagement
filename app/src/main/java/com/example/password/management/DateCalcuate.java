package com.example.password.management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateCalcuate {
    MainActivity activity=new MainActivity();

    SimpleDateFormat mine= new SimpleDateFormat("yyyy MM dd");

    Date today=new Date();

    public long getDaysByToday(String date) throws ParseException {
        return TimeUnit.DAYS.convert(Math.abs(today.getTime()-mine.parse(date).getTime()),TimeUnit.MILLISECONDS);
    }

}
