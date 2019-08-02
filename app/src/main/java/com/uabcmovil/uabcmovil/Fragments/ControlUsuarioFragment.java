package com.uabcmovil.uabcmovil.Fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.uabcmovil.uabcmovil.Adapters.ControlDeUsuariosRVAdapter;
import com.uabcmovil.uabcmovil.Data.UsuariosFireBaseConnection;
import com.uabcmovil.uabcmovil.R;

import java.util.Observable;
import java.util.Observer;

public class ControlUsuarioFragment extends Fragment implements Observer {
    private Button btnOpc1, btnOpc2, btnOpc3,btnBuscar;
    private RecyclerView rv;
    private ControlDeUsuariosRVAdapter adapter;
    private ImageView imgNoUsuarios;
    private TextView txtNoUsuarios;
    private AutoCompleteTextView txtNombre, txtMatricula;
    private Spinner roles;
    private String nameSelected = "", matriSelected = "", rolSelected = "";
    private boolean btn1Selected = false, btn2Selected = false, btn3Selected = false;
    private boolean adaptersLoaded = false;
    private ArrayAdapter<String> adapterNombres, adapterMatriculas;
    private UsuariosFireBaseConnection con;
    private InputMethodManager imm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ControlDeUsuariosRVAdapter(getActivity());
        con = UsuariosFireBaseConnection.getInstance();
        con.addObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_de_usuarios_fragment_layout,container,false);
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgNoUsuarios = view.findViewById(R.id.imgControlUsuarios);
        txtNoUsuarios = view.findViewById(R.id.textViewimgControlUsuarios);
        imgNoUsuarios.setFocusableInTouchMode(true);

        btnBuscar = view.findViewById(R.id.btnBuscarControlUsuarios);
        txtNombre = view.findViewById(R.id.autoTextNombre);
        txtMatricula = view.findViewById(R.id.autoTextMatricula);
        roles = view.findViewById(R.id.spinnerRoles);
        btnOpc1 = view.findViewById(R.id.btnNombreBusqueda);
        btnOpc2 = view.findViewById(R.id.btnMatriculaBusqueda);
        btnOpc3 = view.findViewById(R.id.btnRolBusqueda);

        roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rolSelected = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter<String> rolesData = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"No definido","Estudiante","Profesor","AdminLab"});
        rolesData.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roles.setAdapter(rolesData);

        rv = view.findViewById(R.id.rvControlUsuarios);
        rv.setFocusableInTouchMode(true);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        btnOpc1.setOnClickListener(e->{
            if(!btn1Selected){
                btnOpc1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                btn1Selected = true;
                txtNombre.setVisibility(View.VISIBLE);
                btnBuscar.setVisibility(View.VISIBLE);
            }else{
                btnOpc1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btn1Selected = false;
                txtNombre.setVisibility(View.GONE);
                nameSelected = "";
                txtNombre.setText("");
                if(!btn2Selected&&!btn3Selected) {
                    btnBuscar.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    imgNoUsuarios.setVisibility(View.GONE);
                    txtNoUsuarios.setVisibility(View.GONE);
                }
            }
        });
        btnOpc2.setOnClickListener(e->{
            if(!btn2Selected){
                btnOpc2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                btn2Selected = true;
                txtMatricula.setVisibility(View.VISIBLE);
                btnBuscar.setVisibility(View.VISIBLE);
            }else{
                btnOpc2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btn2Selected = false;
                txtMatricula.setVisibility(View.GONE);
                matriSelected = "";
                txtMatricula.setText("");
                if(!btn1Selected&&!btn3Selected) {
                    btnBuscar.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    imgNoUsuarios.setVisibility(View.GONE);
                    txtNoUsuarios.setVisibility(View.GONE);
                }
            }
        });
        btnOpc3.setOnClickListener(e->{
            if(!btn3Selected){
                btnOpc3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                btn3Selected = true;
                roles.setVisibility(View.VISIBLE);
                btnBuscar.setVisibility(View.VISIBLE);
            }else{
                btnOpc3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btn3Selected = false;
                roles.setVisibility(View.GONE);
                rolSelected = "";
                if(!btn1Selected&&!btn2Selected) {
                    btnBuscar.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    imgNoUsuarios.setVisibility(View.GONE);
                    txtNoUsuarios.setVisibility(View.GONE);
                }
            }
        });
        btnBuscar.setOnClickListener(e->{
            nameSelected = txtNombre.getText().toString();
            matriSelected = txtMatricula.getText().toString();
            setDataOnRV();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        setAutoTextAdapters();
        return view;
    }

    private void setAutoTextAdapters() {
        adapterNombres = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, con.getAllNames());
        adapterMatriculas = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, con.getAllMatris());
        if (txtNombre != null) {
            txtNombre.setAdapter(adapterNombres);
            txtMatricula.setAdapter(adapterMatriculas);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(getActivity()!=null) {
            if(((String)o).equals("Se Modifico")) {
                setDataOnRV();
                setAutoTextAdapters();
            }
        }
    }

    private void setDataOnRV(){
        adapter.setData(con.getUsers(nameSelected,matriSelected,rolSelected));
        adapter.notifyDataSetChanged();
        if(rv!=null){
            rv.setAdapter(adapter);
        }
        if(adapter.getItemCount()==0){
            imgNoUsuarios.setVisibility(View.VISIBLE);
            txtNoUsuarios.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            imgNoUsuarios.requestFocus();
        }else{
            imgNoUsuarios.setVisibility(View.GONE);
            txtNoUsuarios.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.requestFocus();
        }
    }
}
