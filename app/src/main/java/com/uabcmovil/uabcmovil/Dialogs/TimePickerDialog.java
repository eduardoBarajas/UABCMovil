package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class TimePickerDialog extends Dialog {
    private int timeExtra = 0;
    private int timeSelected = 0;
    public TimePickerDialog(@NonNull Activity context, OptionSelectedListener listener) {
        super(context);
        setContentView(R.layout.time_picker_dialog);
        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Button btn1Hora, btn2Horas, btn4Horas, btnConfirmar;
        btn1Hora = findViewById(R.id.btn1Hora);
        btn2Horas = findViewById(R.id.btn2Horas);
        btn4Horas = findViewById(R.id.btn4horas);
        btnConfirmar = findViewById(R.id.btnConfirmarTime);
        btn1Hora.setOnClickListener(e->{
            timeExtra = 60;
            btn1Hora.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffff4444")));
            btn2Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
            btn4Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
        });
        btn2Horas.setOnClickListener(e->{
            timeExtra = 120;
            btn1Hora.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
            btn2Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffff4444")));
            btn4Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));

        });
        btn4Horas.setOnClickListener(e->{
            timeExtra = 240;
            btn1Hora.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
            btn2Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
            btn4Horas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffff4444")));

        });
        btnConfirmar.setOnClickListener(e->{
            if(timeExtra == 0){
                Toast.makeText(context,"Asegurate de seleccionar un tiempo de uso",Toast.LENGTH_SHORT).show();
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timeSelected = timePicker.getHour()*60+timePicker.getMinute();
                    if(timeSelected <(7*60) || timeSelected > (22*60)){
                        Toast.makeText(context,"Asegurate de seleccionar una hora valida en el reloj",Toast.LENGTH_SHORT).show();
                    }else{
                        if(timeExtra+timeSelected > (22*60)){
                            Toast.makeText(context,"Asegurate de seleccionar un tiempo de uso menor",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context,"Hora seleccionada",Toast.LENGTH_SHORT).show();
                            listener.optionSelected("TimePickerDialog",String.valueOf(timeSelected/60)+":"+String.valueOf(timeSelected%60)+" - "+
                                    String.valueOf((timeSelected+timeExtra)/60)+":"+String.valueOf((timeSelected+timeExtra)%60));
                            dismiss();
                        }
                    }
                }
            }
        });
    }
}
