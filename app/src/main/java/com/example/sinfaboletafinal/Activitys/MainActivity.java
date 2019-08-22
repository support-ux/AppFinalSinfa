package com.example.sinfaboletafinal.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.sinfaboletafinal.R;

public class MainActivity extends AppCompatActivity {

    Button btnVerificar;
    EditText txtNumberPhone;
    Switch swSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVerificar = findViewById(R.id.btnLogin);
        txtNumberPhone = findViewById(R.id.txtnumberphone);
        swSesion = findViewById(R.id.swSesion);
        chargePreferences();
        ValidarShared();



        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cargar();
                SavePreferences();
            }
        });
    }

    public void chargePreferences(){
        SharedPreferences numberpreferences = getSharedPreferences("NumberPreferencesUser",MODE_PRIVATE);
        swSesion.setChecked(numberpreferences.getBoolean("checked",false));
        txtNumberPhone.setText(numberpreferences.getString("number",""));
    }

    public void SavePreferences(){

        if(swSesion.isChecked()){
            SharedPreferences numberpreferences = getSharedPreferences("NumberPreferencesUser",MODE_PRIVATE);
            SharedPreferences.Editor editor = numberpreferences.edit();
            String number = txtNumberPhone.getText().toString().trim();
            boolean valor = swSesion.isChecked();
            editor.putBoolean("checked",valor);
            editor.putString("number",number);
            editor.commit();
        }else{
            SharedPreferences settings = getSharedPreferences("NumberPreferencesUser", Context.MODE_PRIVATE);
            settings.edit().clear().commit();
        }

    }

    public void ValidarShared(){
        String numero = txtNumberPhone.getText().toString().trim();

        if (numero.length()<=0){
            swSesion.setChecked(false);
        }else{
            swSesion.setChecked(true);
        }
    }

    private void Cargar() {

        String numero = txtNumberPhone.getText().toString().trim();

        if (numero.isEmpty() || numero.length() < 9) {
            txtNumberPhone.setError("Ingrese un número válido");
            txtNumberPhone.requestFocus();
            return;
        }

        String phoneNumber = "+51" + numero;
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra("phonenumber", phoneNumber);
        startActivity(intent);
        finish();
    }



}
