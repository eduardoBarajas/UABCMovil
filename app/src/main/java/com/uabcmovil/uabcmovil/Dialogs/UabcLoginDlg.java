package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class UabcLoginDlg extends Dialog {
    public UabcLoginDlg(@NonNull Activity context, String user, OptionSelectedListener listener) {
        super(context);
        setContentView(R.layout.login_uabc_dialog_layout);
        Button btnIniciar = findViewById(R.id.btnIniciarUabc);
        EditText contraseña = findViewById(R.id.txtContrasenaDlg);
        TextView usuario = findViewById(R.id.txtCorreoDlg);
        usuario.setText(user);
        btnIniciar.setOnClickListener(e->{
            if(!contraseña.getText().toString().isEmpty()){
                listener.optionSelected("UabcLoginDlg",contraseña.getText().toString());
                dismiss();
            }else{
                Toast.makeText(context,"No puede estar vacia el campo de la contraseña!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
