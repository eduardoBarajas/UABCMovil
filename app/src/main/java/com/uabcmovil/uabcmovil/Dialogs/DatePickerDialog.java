package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DatePickerDialog extends Dialog {
    private int day = 0, month = 0, year = 0;
    public DatePickerDialog(@NonNull Activity context, OptionSelectedListener listener) {
        super(context);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
        String[] date = format.format(Calendar.getInstance().getTime()).split("/");
        day = Integer.parseInt(date[2]);
        month = Integer.parseInt(date[1]);
        year = Integer.parseInt(date[0]);
        Log.e("dATE",String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year));
        setContentView(R.layout.date_picker_dialog_layout);
        DatePicker datePicker = findViewById(R.id.datePickerDlg);
        Button btnConfirmar = findViewById(R.id.btnConfirmarDateDlg);

        btnConfirmar.setOnClickListener(e->{
            if(datePicker.getYear()< year || datePicker.getYear() > year){
                Toast.makeText(context,"No puedes hacer solicitudes fuera del año actual",Toast.LENGTH_SHORT).show();
            }else{
                Log.e("Diferencia mes",String.valueOf(datePicker.getMonth())+"   "+String.valueOf(month));
                if(datePicker.getMonth()+1 < month){
                    Toast.makeText(context,"Elige un mes valido!",Toast.LENGTH_SHORT).show();
                }else{
                    if(datePicker.getDayOfMonth() < day && datePicker.getMonth()+1 <= month ){
                        Toast.makeText(context,"Elige un dia valido!",Toast.LENGTH_SHORT).show();
                    }else{
                        listener.optionSelected("DatePickerDialog",String.valueOf(datePicker.getDayOfMonth())+"/"+String.valueOf(datePicker.getMonth()+1)+"/"+String.valueOf(datePicker.getYear()));
                        dismiss();
                    }
                }
            }
        });
    }
}
