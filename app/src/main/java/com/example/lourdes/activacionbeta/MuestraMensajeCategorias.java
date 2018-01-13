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


/*
* Se encarga de mostrar el mensaje seleccionado por el usuario en la lista de categorías. Sigue un proceso
* muy parecido al método getMensajeFromTodos(int childPositio,String leido_igual_a), con la diferencia
* de que aquí no se accede directamente al mensaje, sino a la lista de mensajes pertenecientes a esa categoría.
*
* @author  Jose Luis
* @version 1.0
*
*/

public class MuestraMensajeCategorias extends AppCompatActivity {

    //Para conectar con la BBDD
    private BDDHelper miHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_mensaje_categorias);

        //Layout principal de la actividad
        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        // Crear layout para los botones que se van a crear
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //Recogemos datos del intento
        Bundle datos = getIntent().getExtras();
        //Para saber que categoría de las existentes ha sido la elegida en el menú desplegado
        int childPosition = Integer.parseInt(datos.getString("childPosition"));

        //Toast.makeText(getBaseContext(),"childPosition = "+childPosition,Toast.LENGTH_SHORT).show();

        //Obtener la lista de categorías que se devuelve en el mismo orden que en MenuPrincipal-Por Categorías
        ArrayList<String> categorias = getCategorias();

        //conociendo el child pulsado, sabemos la categoría seleccionada, ya que muestran como se devuelven en la consulta
        String categoria = categorias.get(childPosition);

        //Log.d("CATEGORÍA",categoria);

        //Almacenar los nombres de las tablas para ir recorriéndolas
        ArrayList<String>nombres_tablas = getNombresTablas();

        //Para almacenar los ids de los mensajes pertenecientes a la categoría seleccionada
        final ArrayList<Integer>ids = new ArrayList<>();

        //Para almacenar los nombres de las tablas que contienen mensajes en esa categoría y su respectivo contador
        final HashMap<String,ArrayList<Integer>> tabla__contador = new HashMap<>();

        //Para relacionar un contador con la id del boton pulsado
        int contador=0;

        //Para guardar los titulos de los mensajes y darle texto a los botones
        final ArrayList<String>titulos = new ArrayList<>();

        //Auxiliares con los nombres de las tablas con datos y con los arrays que contienen a contador,
        // para acceder a HashMap<String,ArrayList<Integer>>
        final ArrayList<String>aux_nombres_tablas = new ArrayList<>();
        final ArrayList<Integer> aux_contador = new ArrayList<>();

        //Para conectar con la BBDD
        BDDHelper miHelper = new BDDHelper(getApplicationContext());
        //Conectamos con la BBDD en modo leer datos
        SQLiteDatabase db = miHelper.getReadableDatabase();

        //Recorrer las tablas en busca de mensajes que corresponden a la categoría seleccionada
        for(int i=0;i<nombres_tablas.size();i++){
            //Crear la consulta
            String query = "SELECT id,titulo FROM '"+nombres_tablas.get(i)+"' WHERE categoria = '"+categoria+"'";
            //Ejecutar consulta
            Cursor cursor = db.rawQuery(query,null);
            cursor.moveToFirst();

            //Si la tabla que estamos recorriendo contiene mensajes de la categoría seleccionada
            if(cursor.getCount()!=0) {
                //Guardar el nombre de la tabla
                String tabla_con_datos = nombres_tablas.get(i);
                //Crear un Array<Integer> para llevarla cuenta
                ArrayList<Integer>lista_contador = new ArrayList<>();

                //Añadir valores de contador,ids y titulos de los mensajes de esa categoría
                for (int j = 0; j < cursor.getCount(); j++) {
                    //Añadir contador
                    lista_contador.add(contador);
                    //Aumentar contador
                    contador++;
                    //Añadir id
                    ids.add(cursor.getInt(0));
                    //Añadir título
                    titulos.add(cursor.getString(1));
                    //Moverse en el cursor para los resultados siguientes
                    cursor.moveToNext();
                }
                //Añadir nombres de tablas
                aux_nombres_tablas.add(tabla_con_datos);
                //Añadir nombre de tabla y array contador
                tabla__contador.put(tabla_con_datos,lista_contador);
            }
        }
       /* for(int i=0;i<lista_contador.size();i++){
            Log.d("LISTA-CONTADOR",""+lista_contador.get(i));
        }*/

        //Crear botones de manera dinámica, uno para cada mensaje correspondiente a esa categoría
        for( int i = 0; i<ids.size(); i++){

            // Crear LinearLayout que alberga el botón y definir parámetros
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

            // Crear botón y definir parámetros del mismo
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

            //Poner el botón a la escucha
            titulo.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                   //Recoger id del botón pulsado
                   int boton_pulsado = titulo.getId();
                   //Para ir pasándole los distintos arrays contador
                   ArrayList<Integer>aux_contador = new ArrayList<>();
                   //Para el nombre de la tabla en la que se encuentra el mensaje que se quiere mostrar
                   String nombre_tabla_final="";
                   //La id del mensaje que se va a mostrar
                   int id=0;

                   //Recorrer las tablas en busca del mensaje
                   for(int j=0;j<aux_nombres_tablas.size();j++){
                       //Pasar el array contador al auxiliar
                        aux_contador = tabla__contador.get(aux_nombres_tablas.get(j));
                       //Recorrer el contador para comparar con el botón pulsado
                        for(int d = 0;d < aux_contador.size();d++) {
                            //Si coincide, tenemos nombre de tabla e id del mensaje a mostrar
                            if (aux_contador.get(d) == boton_pulsado) {
                                nombre_tabla_final = aux_nombres_tablas.get(j);
                                id = ids.get(boton_pulsado);
                                Log.d("BOTONPULSADO-MATCH",""+aux_contador.get(d));
                            }
                        }
                   }

                    //Recoger el titulo del mensaje a mostrar
                    String titulo_mensaje = titulos.get(boton_pulsado);

                    //Crear intento para iniciar nueva actividad
                    Intent intent = new Intent(getApplicationContext(),MuestraMensaje.class);
                    //Añadir datos al intento para que los use la nueva actividad que se va a iniciar
                    intent.putExtra("nombre_tabla","'"+nombre_tabla_final+"'");
                    intent.putExtra("id_mensaje",id);
                    intent.putExtra("titulo",titulo_mensaje);
                    //Comenzar nueva actividad
                    startActivity(intent);
                    //Terminar actividad actual
                    finish();
                }
            });

            //Añadir botón al layout
            ll.addView(titulo);
            //Añadir layout al layout principal
            lm.addView(ll);


        }
    }

    /*
    * Devuelve los nombres de las categorías creadas por el usuario.
    *
    * @return categorias un ArrayList<String> conteniendo los nombres de las categorías.
    *
    * */

    public ArrayList<String> getCategorias(){

        //Para almacenar las diferentes categorías
        ArrayList<String>categorias = new ArrayList<>();
        //Para leer de la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();
        //Crear consulta
        String RAW_QUERY = "SELECT nombre FROM categorias";
        //Ejecutar consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        //Recoger resultados del resultset
        for(int i=0; i<cursor.getCount();i++){
            categorias.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return categorias;
    }


    /*
   * Devuelve los nombres de las tablas de la BBDD descartando los de las tablas que no necesita.
   *
   * @return nombres_tablas un Array<String> conteniendo los nombres de las tablas.
   *
   * */
    public ArrayList<String> getNombresTablas(){

        //Para conectar con la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();
        //Para almacenar los nombres de las tablas
        ArrayList<String> nombres_tablas = new ArrayList<>();
        //Para almacenar sólo los nombres de las tablas que se van a usar
        ArrayList<String> nombres_tablas_aux = new ArrayList<>();
        //Construir consulta
        String RAW_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";
        //Ejecutar consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

        //Recorrer el resultado de la consulta y descartar los nombres de las tablas que no vamos a utilizar
        for(int i=0;i<cursor.getCount();i++) {

            String resultado = cursor.getString(0);

            nombres_tablas_aux.add(resultado);

            cursor.moveToNext();
        }
        //Descartar las tablas que no quiero incluir en los resultados

        for (int i=0;i<nombres_tablas_aux.size();i++){

            if(!nombres_tablas_aux.get(i).equals("android_metadata") && !nombres_tablas_aux.get(i).equals("tokens")
                    && !nombres_tablas_aux.get(i).equals("categorias")){

                nombres_tablas.add(nombres_tablas_aux.get(i));
            }
        }
        //Cerrar conexión con la BBDD
        db.close();

        return nombres_tablas;
    }


    //Define las acciones a realizar cuando se pulsa el botón 'Atrás' en el dispositivo
    public void onBackPressed() {
        Intent intent = new Intent(this,MenuPrincipal.class);
        //intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
        startActivity(intent);
        finish();

        super.onBackPressed();
    }

}//end of class
