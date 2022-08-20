package co.desofsi.solportech.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateClass {
    private Calendar calendar;
    private int dia, mes, anio;

    public DateClass() {
        this.calendar = Calendar.getInstance();
    }

    public String time(String timestamp){
        try{
            return timestamp.substring(10, 16)+",  ";
        }catch (Exception e){
            return "";
        }
    }

    public String dateToday() {
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH) + 1;
        anio = calendar.get(Calendar.YEAR);
        String date = "2020/10/10";
        if (mes < 10 && dia < 10) {
            date = anio + "/0" + mes + "/0" + dia;
        } else if (mes > 10 && dia < 10) {
            date = anio + "/" + mes + "/0" + dia;
        } else if (mes < 10 && dia > 10) {
            date = anio + "/0" + mes + "/" + dia;
        } else {
            date = anio + "/" + mes + "/" + dia;
        }
        return date;
    }
    public String dateTodayFormatServer() {
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH) + 1;
        anio = calendar.get(Calendar.YEAR);
        String date = "2020/10/10";
        if (mes < 10 && dia < 10) {
            date = anio + "-0" + mes + "-0" + dia;
        } else if (mes > 10 && dia < 10) {
            date = anio + "-" + mes + "-0" + dia;
        } else if (mes < 10 && dia > 10) {
            date = anio + "-0" + mes + "-" + dia;
        } else {
            date = anio + "-" + mes + "-" + dia;
        }
        return date;
    }

    public String dateFormatHuman(String date_timestamp) {
        String date_result = "";
        String pattern = "yyyy-MM-dd";
        String date_select = date_timestamp.substring(0, 10);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = simpleDateFormat.parse(date_select);
            SimpleDateFormat format = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            String date_for_human = format.format(date);
            date_result = date_for_human;
            return date_result;
        } catch (ParseException e) {
            e.printStackTrace();
            return date_result;

        }

    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
}

