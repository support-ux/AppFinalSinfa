package com.example.sinfaboletafinal.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sinfaboletafinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class ConsultaActivity extends AppCompatActivity {
Button btnConsultar;
Button btnDownload;
TextView textView6;
EditText txtnsa;
Spinner cboAnio;
Spinner cboMes;

String Nombre;

RequestQueue rq;
JsonRequest jrq;

byte[] pdf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        btnConsultar = findViewById(R.id.btnConsultar);
        btnDownload = findViewById(R.id.btnDownload);
        textView6 = findViewById(R.id.textView6);
        txtnsa = findViewById(R.id.txtnsa);
        cboAnio = findViewById(R.id.cboAnio);
        cboMes = findViewById(R.id.cboMes);
        isStoragePermissionGranted(ConsultaActivity.this);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validaForm();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadPDF();
            }
        });
        
    }

    private void DownloadPDF() {
        String filepath ="/sdcard/Android_ux/"+Nombre+".pdf";
        OutputStream pdffos = null;
        try {
            pdffos = new FileOutputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            pdffos.write(pdf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pdffos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            pdffos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(ConsultaActivity.this,"Archivo guardado en: "+filepath,Toast.LENGTH_LONG).show();

    }

    public void validaForm(){

        String NSA,ANIO,MES;

        NSA = txtnsa.getText().toString();
        ANIO = cboAnio.getSelectedItem().toString();
        MES = cboMes.getSelectedItem().toString();
        if(TextUtils.isEmpty(NSA)||NSA.length()<6) {
            txtnsa.setError("Ingrese NSA Válido...!!");
        }else{
            buscarResultados("http://192.168.1.2/wsboletas/Datos/getDataClient.php?nsa="+NSA+"&anio="+ANIO+"&mes="+MES);

        }
    }

    private void buscarResultados(final String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.optJSONArray("datos");

                    jsonObject = jsonArray.getJSONObject(0);
                    String nsa = jsonObject.optString("NSA");
                    String anio = jsonObject.optString("ANIO");
                    String mes = jsonObject.optString("MES");

                    if(nsa.equals("NO REGISTRA NSA")){

                        textView6.setVisibility(View.GONE);
                        btnDownload.setVisibility(View.GONE);
                        Toast.makeText(ConsultaActivity.this,"NO SE ENCONTRARON RESULTADOS",Toast.LENGTH_LONG).show();

                    }else{
                        String archivo = jsonObject.optString("ARCHIVO");
                        byte[] byteCode = Base64.decode(archivo, Base64.DEFAULT);
                        pdf = byteCode;
                        File createFile = new File(Environment.getExternalStorageDirectory(),"/Android_ux/");
                        createFile.mkdirs();

                        String name = "NSA-"+nsa+"-B-"+mes+"-"+anio;
                        Nombre = name;
                        textView6.setVisibility(View.VISIBLE);
                        btnDownload.setVisibility(View.VISIBLE);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ConsultaActivity.this,"Se ah producido un error!!",Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String NSA,ANIO,MES;

                NSA = txtnsa.getText().toString();
                ANIO = cboAnio.getSelectedItem().toString();
                MES = cboMes.getSelectedItem().toString();

                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("NSA",NSA);
                parametros.put("ANIO", ANIO);
                parametros.put("MES", MES);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                //Toast.makeText(getApplicationContext(), "Permission is revoked",Toast.LENGTH_SHORT).show();
                //Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Toast.makeText(getApplicationContext(), "Permission is revoked",Toast.LENGTH_SHORT).show();
            //Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    /*public void mostrarPDF() {
        String path = Environment.getExternalStorageDirectory()+"/Android_ux/"+Nombre;
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target,"Open File");

        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No cuentas con lector PDF",Toast.LENGTH_SHORT).show();
        }
    }*/
}
