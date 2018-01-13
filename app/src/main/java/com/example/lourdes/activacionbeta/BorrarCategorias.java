package com.example.lourdes.activacionbeta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


/*
* Clase que se encarga de borrar categorías seleccionadas por el usuario y previamente creadas por el mismo.
*
* @author  Jose Luis
* @version 1.0
*/

public class BorrarCategorias extends AppCompatActivity {

    //Para conectar con la BBDD
    private final BDDHelper miHelper = new BDDHelper(this);
    //Para mostrar texto que indica aún no hay categorías creadas
    private TextView no_categorias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_categorias);



        //Layout principal de la actividad
        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        //Parámetros del layout para definir la apariencia de los botones
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);



        //Para poder leer datos de la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();

        //Construir la consulta
        String RAW_QUERY = "SELECT nombre FROM categorias";
        //Ejecutar la consulta
        final Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

        //Si aún no existen categorías creadas se muestra un TextView indicándolo
        if(cursor.getCount()==0){
            no_categorias = (TextView)findViewById(R.id.texto_no_cat);
            no_categorias.setText(R.string.no_categorias);
        }

        //Crear botones dinámicamente,uno para cada categoría

        for(int j=0;j<cursor.getCount();j++) {
            // Crear un LinearLayout para cada botón
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundColor(12);

           /* GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this, R.color.colorBordeLayoutListaMensajes)); //white background
            border.setStroke(2, ContextCompat.getColor(this, R.color.colorBordeSeparacion)); //black border with full opacity
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ll.setBackgroundDrawable(border);
            } else {
                ll.setBackground(border);
            }*/


           //Crear el boton que reperesenta a una categoría
            final Button boton = new Button(this);
            //Darle nombre de la categoría correspondiente
            boton.setText(cursor.getString(0));
            //Darle una id
            boton.setId(j);
            //Establecer parámetros del Layout
            boton.setLayoutParams(params);

            //Log.d("idsboton",""+boton.getId());

            //Poner el botón a la escucha de ser pulsado
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getApplicationContext(),"comenzar actividad",Toast.LENGTH_SHORT).show();

                    //Crear intento para comenzar nueva actividad
                    Intent intent = new Intent(getApplicationContext(),ConfirmarBorradoCategoria.class);
                    //Pasarle datos al intento, en concreto el nombre de la categoría a borrar
                    intent.putExtra("nombre_categoria",boton.getText().toString());
                    //Iniciar nueva actividad
                    startActivity(intent);
                    //Terminar actividad actual
                    finish();

                }
            });

/*
            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = 25 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            float dp_boton = 200 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            boton.setTextSize(dp);
            boton.setGravity(0);
            boton.setHeight((int) dp_boton);

            boton.setLayoutParams(params);*/

            //Añadir el botón a la vista
            ll.addView(boton);
            //Avanzar a la siguiente fila del resultset, para obtener el siguiente nombre de categoría
            cursor.moveToNext();
            //Añadir el layout que contiene el botón al layout principal
            lm.addView(ll);

        }

        //Cerrar conexión con la BBDD
        db.close();


      /*  final Button borra = new Button(this);
        borra.setText("Borrar categorías seleccionadas");
        borra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 ArrayList<String>nombres_tablas = new ArrayList<>();
                 BDDHelper miHelper = new BDDHelper(getApplicationContext());
                 SQLiteDatabase db = miHelper.getReadableDatabase();
                 String query = "select nombre from categorias";
                 Cursor cursor = db.rawQuery(query,null);
                 cursor.moveToFirst();

                 for(int i = 0;i<cursor.getCount();i++){
                     nombres_tablas.add(cursor.getString(0));
                     cursor.moveToNext();
                 }

            }
        });
        //borra.setId;

        lm.addView(borra);*/
    }

}//end of class
