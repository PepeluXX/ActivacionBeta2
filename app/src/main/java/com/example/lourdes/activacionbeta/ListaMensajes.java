package com.example.lourdes.activacionbeta;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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


/*
* Se encarga de mostrar una lista con todos los mensajes contenidos en la tabla indicada, que será una tabla
* correspondiente a mensajes para hijos o para cursos.
*
* @author  Jose Luis
* @version 1.0
*
*/

public class ListaMensajes extends AppCompatActivity {

    //Para conectar con la BBDD
    public BDDHelper miHelper = new BDDHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mensajes);

        //Definir layout para albergar botones, un layout para cada botón, y definir parámetros del layout
        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);
        lm.setBackgroundColor(ContextCompat.getColor(this,R.color.fondo));
       //Definir parámetros del botón que se va crear
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //Para leer datos de la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();
        //Recoger los datos del intento que inició esta actividad
        Bundle datos = getIntent().getExtras();
        //Recoger el nombre de la tabla que contiene los mensajes que se van a mostrar
        final String nombre_tabla = datos.getString("nombre_tabla");
        //Definir la consulta
        String RAW_QUERY = "SELECT titulo,leido,id FROM "+nombre_tabla+ " ORDER BY leido,fecha DESC";
        //Ejecutar la consulta
        final Cursor cursor = db.rawQuery(RAW_QUERY,null);
        //Moverse a la primera posición del resultset
        cursor.moveToFirst();


        //Creamos botones dinámicamente, uno para cada mensaje que haya en la tabla correspondiente

        for(int j=0;j<cursor.getCount();j++)
        {
            // Crear LinearLayout que albergará el botón y definir parámetros
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundColor(ContextCompat.getColor(this,R.color.fondo));

            GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this,R.color.colorBordeLayoutListaMensajes)); //white background
            border.setStroke(2, ContextCompat.getColor(this,R.color.colorBordeSeparacion)); //black border with full opacity
            //Controlar compatibilidad de versiones de android
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ll.setBackgroundDrawable(border);
            } else {
                ll.setBackground(border);
            }



            // Crear botón y darle valor a ciertos parámetros
            final Button titulo = new Button(this);

            titulo.setText(cursor.getString(0));
            titulo.setId(cursor.getInt(2));

            //Log.d("CONSULTA", String.valueOf(cursor.getInt(1)));

            //Comprobamos si el mensaje se ha leído o no
            if(cursor.getInt(1)==0){
                //Si el mensaje no se ha leído aún, el texto se pone en negrita
                titulo.setTextColor(ContextCompat.getColor(this, R.color.colorTextoTituloNoLeido));
                titulo.setTypeface(null, Typeface.BOLD);
               // Toast.makeText(this,"no leido = "+cursor.getInt(1),Toast.LENGTH_LONG).show();

            }
            else{
                //Si el mensaje ya se ha leído, se pone el texto normal
                titulo.setTextColor(ContextCompat.getColor(this, R.color.colorTextoTitulo));
                //Toast.makeText(this,"leido = "+cursor.getInt(1),Toast.LENGTH_LONG).show();
            }


            //Se definen mas parámetros para configurar el botón
            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = 25 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            float dp_boton = 200 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            titulo.setTextSize(dp);
            titulo.setGravity(0);
            titulo.setHeight((int) dp_boton);

            //Se añaden algunos parámetros definidos previamente
            titulo.setLayoutParams(params);


            // Se pone el botón a la escucha de ser pulsado
            titulo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //Crear intento para iniciar una nueva actividad
                    Intent intent = new Intent(getBaseContext(),MuestraMensaje.class);
                    //Añadir datos al intento para que los use la actividad que se va a iniciar
                    intent.putExtra("titulo",titulo.getText().toString());
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    intent.putExtra("id_mensaje",titulo.getId());
                    //Comenzamos la nueva actividad
                    startActivity(intent);
                    //Finalizamos la actividad actual
                    finish();

                }
            });

            //Moverse en el resultset para la creación del siguiente botón
            cursor.moveToNext();
            //Añadir el botón al layout definido para albergar el botón
            ll.addView(titulo);
            //Añadir el layout que alberga el botón al layout principal
            lm.addView(ll);
        }
        //Cerrar conexión con la BBDD
        db.close();
    }
    //Definir acciones a realizar cuando se pulse el botón 'Atrás' en el dispositivo
    public void onBackPressed() {
        //Crear intento para iniciar nueva actividad
        Intent intent = new Intent(this,MenuPrincipal.class);
        //Comenzar nueva actividad
        startActivity(intent);
        //Finalizar nueva actividad
        finish();

        super.onBackPressed();
    }

}//end of class

