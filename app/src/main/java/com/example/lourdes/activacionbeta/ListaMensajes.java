package com.example.lourdes.activacionbeta;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Scope;

public class ListaMensajes extends AppCompatActivity {

    public BDDHelper miHelper = new BDDHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mensajes);

        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);



        //solicitamos titulos a la tabla que venga de la otra activity como 'filtro'

        SQLiteDatabase db = miHelper.getReadableDatabase();

        Bundle datos = getIntent().getExtras();

        final String nombre_tabla = datos.getString("nombre_tabla");

        String RAW_QUERY = "SELECT titulo,leido,id FROM "+nombre_tabla;

        final Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();


        //Creamos elementos dinámicamente

        for(int j=0;j<cursor.getCount();j++)
        {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundColor(12);

            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this,R.color.colorBordeLayoutListaMensajes)); //white background
            border.setStroke(2, ContextCompat.getColor(this,R.color.colorBordeSeparacion)); //black border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ll.setBackgroundDrawable(border);
            } else {
                ll.setBackground(border);
            }



            // Create Button
            final Button titulo = new Button(this);

            titulo.setText(cursor.getString(0));
            titulo.setId(cursor.getInt(2));

            Log.d("CONSULTA", String.valueOf(cursor.getInt(1)));

            if(cursor.getInt(1)==0){
                titulo.setTextColor(ContextCompat.getColor(this, R.color.colorTextoTituloNoLeido));
               // Toast.makeText(this,"no leido = "+cursor.getInt(1),Toast.LENGTH_LONG).show();

            }
            else{
                titulo.setTextColor(ContextCompat.getColor(this, R.color.colorTextoTitulo));
                //Toast.makeText(this,"leido = "+cursor.getInt(1),Toast.LENGTH_LONG).show();
            }


            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = 25 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            float dp_boton = 200 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            titulo.setTextSize(dp);
            titulo.setGravity(0);
            titulo.setHeight((int) dp_boton);


            titulo.setLayoutParams(params);
            cursor.moveToNext();

            // Set click listener for button
            titulo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(getBaseContext(),MuestraMensaje.class);
                    intent.putExtra("titulo",titulo.getText().toString());
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    intent.putExtra("id_mensaje",titulo.getId());
                    startActivity(intent);
                    finish();

                }
            });

            ll.addView(titulo);


            lm.addView(ll);
        }
        db.close();
    }
    }

