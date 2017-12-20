package com.example.lourdes.activacionbeta;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MuestraMensajeCategorias extends AppCompatActivity {

    private BDDHelper miHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_mensaje_categorias);

        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Bundle datos = getIntent().getExtras();
        int childPosition = Integer.parseInt(datos.getString("childPosition"));

        Toast.makeText(getBaseContext(),"childPosition = "+childPosition,Toast.LENGTH_SHORT).show();

        //obtenemos la lista de categorías que se devuelve en el mismo orden que en MenuPrincipal

        ArrayList<String> categorias = getCategorias();

        //conociendo el child pulsado, sabemos la categoría seleccionada, ya que muestran como se devuelven en la consulta

        String categoria = categorias.get(childPosition);

        Log.d("CATEGORÍA",categoria);

        //almacenamos los nombres de las tablas para ir recorriéndolas

        ArrayList<String>nombres_tablas = getNombresTablas();

        //almacenamos los ids de los mensajes pertenecientes a la categoría seleccionada

        final ArrayList<Integer>ids = new ArrayList<>();

        //para almacenar los nombres de las tablas que contienen mensajes en esa categoría

        final HashMap<String,ArrayList<Integer>> tabla__contador = new HashMap<>();

        //para relacionar un contador con la id del boton pulsado

       // ArrayList<Integer> lista_contador = new ArrayList<>();
        int contador=0;

        //para guardar los titulos de los mensajes y darle texto a los botones
        final ArrayList<String>titulos = new ArrayList<>();

        //auxiliares con los nombres de las tablas con datos y con los arrays que contienen a contador,
        // para acceder a HashMap<String,ArrayList<Integer>>
        final ArrayList<String>aux_nombres_tablas = new ArrayList<>();
        final ArrayList<Integer> aux_contador = new ArrayList<>();


        BDDHelper miHelper = new BDDHelper(getApplicationContext());
        SQLiteDatabase db = miHelper.getReadableDatabase();


        for(int i=0;i<nombres_tablas.size();i++){
            String query = "SELECT id,titulo FROM '"+nombres_tablas.get(i)+"' WHERE categoria = '"+categoria+"'";
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();

            if(cursor.getCount()!=0) {
                String tabla_con_datos = nombres_tablas.get(i);
                ArrayList<Integer>lista_contador = new ArrayList<>();
                for (int j = 0; j < cursor.getCount(); j++) {

                    lista_contador.add(contador);
                    contador++;
                    ids.add(cursor.getInt(0));
                    titulos.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                aux_nombres_tablas.add(tabla_con_datos);
                tabla__contador.put(tabla_con_datos,lista_contador);
            }
        }
       /* for(int i=0;i<lista_contador.size();i++){
            Log.d("LISTA-CONTADOR",""+lista_contador.get(i));
        }*/

        //añadimos los elemenstos dinámicamente

        for( int i = 0; i<ids.size(); i++){

            // Create LinearLayout
            final LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundColor(12);

            final GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this,R.color.colorBordeLayoutListaMensajes)); //white background
            border.setStroke(2, ContextCompat.getColor(this,R.color.colorBordeSeparacion)); //black border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ll.setBackgroundDrawable(border);
            } else {
                ll.setBackground(border);
            }

            // Create Button
            final Button titulo = new Button(this);

            titulo.setText(ids.get(i)+". "+titulos.get(i));
            titulo.setId(i);

            titulo.setTextColor(ContextCompat.getColor(this, R.color.colorTextoTitulo));

            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = 25 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            float dp_boton = 200 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            titulo.setTextSize(dp);
            titulo.setGravity(0);
            titulo.setHeight((int) dp_boton);

            titulo.setLayoutParams(params);

            // Set click listener for button
            titulo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Toast.makeText(getApplicationContext(),"hola desde aqui",Toast.LENGTH_SHORT).show();
                   /* Intent intent = new Intent(getBaseContext(),MuestraMensaje.class);
                    intent.putExtra("titulo",titulo.getText().toString());
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    intent.putExtra("id_mensaje",titulo.getId());
                    startActivity(intent);
                    finish();*/

                   int boton_pulsado = titulo.getId();

                   ArrayList<Integer>aux_contador = new ArrayList<>();
                   String nombre_tabla_final="";
                   int id=0;

                   for(int j=0;j<aux_nombres_tablas.size();j++){
                       Log.d("BOTONPULSADO-tablasize",""+aux_nombres_tablas.size());
                        aux_contador = tabla__contador.get(aux_nombres_tablas.get(j));
                       Log.d("BOTONPULSADO-tablaname",""+aux_nombres_tablas.get(j));
                        for(int d = 0;d < aux_contador.size();d++) {
                            Log.d("BOTONPULSADO-CONTADOR",""+aux_contador.get(d));
                            Log.d("BOTONPULSADO-CONTASIZE",""+aux_contador.size());
                            if (aux_contador.get(d) == boton_pulsado) {
                                nombre_tabla_final = aux_nombres_tablas.get(j);
                                id = ids.get(boton_pulsado);
                                Log.d("BOTONPULSADO-MATCH",""+aux_contador.get(d));
                            }
                        }
                   }
                       String titulo_mensaje = titulos.get(boton_pulsado);
                    Log.d("BOTONPULSADO-ID"," "+id);
                        Log.d("BOTONPULSADO",""+boton_pulsado);
                    Log.d("BOTONPULSADO-TITULO",""+titulo_mensaje);
                    Log.d("BOTONPULSADO-TABLA",""+nombre_tabla_final);

                        Intent intent = new Intent(getApplicationContext(),MuestraMensaje.class);
                        intent.putExtra("nombre_tabla","'"+nombre_tabla_final+"'");
                        intent.putExtra("id_mensaje",id);
                        intent.putExtra("titulo",titulo_mensaje);
                        startActivity(intent);
                        finish();




                }
            });

            ll.addView(titulo);


            lm.addView(ll);


        }
    }

    public ArrayList<String> getCategorias(){

        ArrayList<String>categorias = new ArrayList<>();
        // categorias.add("Crear categoría");

        SQLiteDatabase db = miHelper.getReadableDatabase();


        String RAW_QUERY = "SELECT nombre FROM categorias";
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        for(int i=0; i<cursor.getCount();i++){
            categorias.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return categorias;
    }


    public ArrayList<String> getNombresTablas(){

        SQLiteDatabase db = miHelper.getReadableDatabase();

        ArrayList<String> nombres_tablas_aux = new ArrayList<>();
        ArrayList<String> nombres_tablas = new ArrayList<>();

        //tomamos los nombres de las tablas descartando las que no queremos : tokens y android_metadata

        String RAW_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";


        Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

        for(int i=0;i<cursor.getCount();i++) {


            String resultado = cursor.getString(0);

            nombres_tablas_aux.add(resultado);

            // Toast.makeText(getApplicationContext(), "RESULTADO = " + resultado, Toast.LENGTH_SHORT).show();

            cursor.moveToNext();
        }

        //descarto las tablas que no quiero incluir en los resultados

        for (int i=0;i<nombres_tablas_aux.size();i++){

            if(!nombres_tablas_aux.get(i).equals("android_metadata") && !nombres_tablas_aux.get(i).equals("tokens")
                    && !nombres_tablas_aux.get(i).equals("categorias")){

                nombres_tablas.add(nombres_tablas_aux.get(i));
            }
        }

        db.close();

        return nombres_tablas;
    }
}
