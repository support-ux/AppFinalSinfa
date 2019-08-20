package com.example.sinfaboletafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConsultaActivity extends AppCompatActivity {
Button btnConsultar;
Button btnDownload;
TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        btnConsultar = findViewById(R.id.btnConsultar);

        btnDownload = findViewById(R.id.btnDownload);

        textView6 = findViewById(R.id.textView6);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnDownload.setVisibility(View.VISIBLE);
                textView6.setVisibility(View.VISIBLE);
            }
        });
        
    }
}
