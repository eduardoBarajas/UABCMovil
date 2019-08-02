package com.uabcmovil.uabcmovil.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Listeners.OptionSelectedListener;
import com.uabcmovil.uabcmovil.R;

public class OptionChooserDialog extends Dialog {
    OptionSelectedListener listener = null;
    private String tag;
    public OptionChooserDialog(@NonNull Activity context,String tag, String msg, OptionSelectedListener listener) {
        super(context);
        this.tag = tag;
        this.listener = listener;
        setContentView(R.layout.option_chooser_dialog_layout);
        Button btnAceptar = findViewById(R.id.btnAceptarDlg);
        Button btnCancelar = findViewById(R.id.btnCancelarDlg);
        btnAceptar.setOnClickListener(e->{
            listener.optionSelected(tag,"SI");
            dismiss();
        });
        btnCancelar.setOnClickListener(e->{
            listener.optionSelected(tag,"NO");
            dismiss();
        });
        TextView titulo = findViewById(R.id.txtTitleDlgChooser);
        TextView mensaje = findViewById(R.id.txtMsgDlgChooser);
        titulo.setText(tag);
        mensaje.setText(msg);
        btnAceptar.setText("Si");
    }

    @Override
    public void onBackPressed() {
        listener.optionSelected(tag,"NO");
        super.onBackPressed();
    }
}
