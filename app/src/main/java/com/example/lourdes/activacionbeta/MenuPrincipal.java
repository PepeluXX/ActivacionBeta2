package com.example.lourdes.activacionbeta;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


/*
* Clase en la que se crea el menú principal de la aplicación. Muestra un menú con filas
* desplegables. Al pulsar sobre los hijos desplegados se accede o bien a listas de mensajes o directamente
* al mensaje seleccionado
*
* @author  Jose Luis
* @version 1.0
* @since   12/01/2018
*/

public class MenuPrincipal extends AppCompatActivity {

    //Para almacenar el key set de los filtros principales
    List<String>subfiltros;
    //Lista expandible
    ExpandableListView Exp_list;
    //Objeto de clase para adaptar el menú
    AdaptadorFiltrosPrincipalesVE adapter;

    //Objeto para acceder a la BBDD SQLite
    public BDDHelper mDbHelper = new BDDHelper(this);

    //Para almacenar los filtros principales y las filas a desplegar
    HashMap<String, List<String>> filtros_principales = new LinkedHashMap<String, List<String>>();

    //Para almacenar las filas desplegadas en "Ver todos leídos"
    ArrayList<String> ver_todos = new ArrayList<String>();
    //Para almacenar las filas desplegadas en "Por hijo"
    ArrayList<String> por_hijo = new ArrayList<String>();
    //Para almacenar las filas desplegadas en "Por curso"
    ArrayList<String> por_curso = new ArrayList<String>();
    //Para almacenar las filas desplegadas en "Por categorías"
    ArrayList<String>por_categorias = new ArrayList<>();
    //Para almacenar las filas desplegadas en "Sin leer"
    ArrayList<String>sin_leer = new ArrayList<>();
    //Para almacenar las filas desplegadas en "Generales"
    ArrayList<String>generales = new ArrayList<>();

    //Imagen a mostrar si hay mensajes sin leer
    ImageView sobre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        //final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout2);

        //Traer imagen
        sobre = (ImageView)findViewById(R.id.sobre);

        //Array para almacenar resultado del método
        ArrayList<String>check = new ArrayList<>();
        //Ejecutar método para comprobar si hay mensajes sin leer
        check = getTitulosTodos("leido=0");

        //Si hay mensajes sin leer, se activa la imagen
        if(!check.isEmpty()){
            sobre.setImageResource(R.drawable.ic_email_enano);
        }

        //Llenar array con los titulos de los mensajes leídos
        ver_todos = getTitulosTodos("leido=1");
        //Llenar array con los nombres de los hijos
        por_hijo = getNombreHijos();
        //Llenar array con los nombres de los cursos
        por_curso = getNombreCursos();
        //Llenar array con los nombres de las categorías creadas
        por_categorias = getCategorias();
        //Llenar array con los títulos de los mensajes sin leer
        sin_leer = getTitulosTodos("leido=0");
        //Llenar array con los titulos de los mensajes generales
        generales = getTitulosGenerales();

        //Traer vista expandible desde el layout
        Exp_list = (ExpandableListView)findViewById(R.id.exp_list);
        //Añadir keys para los arrays y los arrays identificados por esa key
        filtros_principales.put("Sin leer",sin_leer);
        filtros_principales.put("Por Hijo",por_hijo);
        filtros_principales.put("Por Curso",por_curso);
        filtros_principales.put("Generales",generales);
        filtros_principales.put("Ver todos leídos",ver_todos);
        filtros_principales.put("Por Categoría",por_categorias);

        //Recoger las keys
        subfiltros = new ArrayList<String>(filtros_principales.keySet());
        //Pasarle valores al objeto de la clase para terminar de construirlo
        adapter = new AdaptadorFiltrosPrincipalesVE(this,filtros_principales,subfiltros);
        //Adaptar la vista expandible con los valores que se le han pasado (ArrayList<key,Array<String>>,ArrayList<String>)
        Exp_list.setAdapter(adapter);

        //Poner las filas desplegadas a la escucha de ser pulsadas
        Exp_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

               /* Toast.makeText(getBaseContext(),filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition)+" from category"+
                        subfiltros.get(groupPosition)+" is selected",Toast.LENGTH_LONG).show();*/

                //Se recoge la categoría principal a la que pertenece la fila desplegada pulsada
                String filtro=subfiltros.get(groupPosition);

                //Toast.makeText(getBaseContext(),"FILTRO ="+filtro,Toast.LENGTH_LONG).show();

                //Para almacenar el nombre de la tabla en la que se va a buscar el mensaje
                String nombre_tabla="";

                //Esta serie de if sería conveniente cambiarla por un switch(filtro){}

                //Si la categoría es por hijo
                if(filtro.equals("Por Hijo")){
                    //Crear intento para iniciar una nueva activity
                    Intent intent = new Intent(getApplicationContext(),ListaMensajes.class);
                    //Recoger la tabla seleccionada (fila desplegada pulsada)
                    nombre_tabla = filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition).replace(" ","_");
                    //Adaptar el nombre de la tabla a como se encuentra en la BBDD para la consulta
                    nombre_tabla = "'hijo-"+nombre_tabla+"!'";
                    //Añadir datos al intento para recogerlos en la actividad que se va a iniciar
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    //Iniciar activity
                    startActivity(intent);
                    //Finalizar actividad actual
                    finish();
                }
                //Si la categoría es por curso
                else if(filtro.equals("Por Curso")){
                    //Crear un intento para iniciar una nueva activity
                    Intent intent = new Intent(getApplicationContext(),ListaMensajes.class);
                    //Recoger el nombre de la tabla
                    nombre_tabla = filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition).replace(" ","_");
                    //Adaptar el nombre de la tabla para la consulta
                    nombre_tabla = "'curso-"+nombre_tabla+"!'";
                    //Añadir datos al intento
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    //Iniciar nueva actividad
                    startActivity(intent);
                    //Finalizar actividad actual
                    finish();
                }
                //Si la categoría es por categoría
                else if(filtro.equals("Por Categoría")){
                    //Crear intento para iniciar nueva actividad
                    Intent intent = new Intent(getApplicationContext(),MuestraMensajeCategorias.class);
                    //Añadir datos al intento
                    intent.putExtra("childPosition",String.valueOf(childPosition));
                    //Iniciar actividad
                    startActivity(intent);
                    //Finalizar actividad actual
                    finish();
                }
                //Para estas otras categorías, si pulsada una de sus filas desplegadas, se accede directamente
                //al mensaje, sin pasar por la lista de mensajes como ocurre en los casos previos. Se ejecutan por
                //tanto los métodos que nos lo permiten

                //Si la categoría es sin leer
                else if(filtro.equals("Sin leer")){
                    //Ejecutar el método que muestra los mensajes
                    getMensajeFromTodos(childPosition,"leido = 0");
                    //finish()?
                }
                else if(filtro.equals("Generales")){
                    //Ejecutar el método que muestra los mensajes
                    getMensajeGeneral(childPosition);
                    //finish()?
                }
                else { //if(filtro.equals("Ver todos leídos"))
                    //Ejecutar el método que muestra los mensajes
                    getMensajeFromTodos(childPosition,"leido = 1");
                    //finish()? no porque cierro el menú principal que debe ser la última activity antes de abandonar la app
                }
                return false;
            }
        }); //end of clickListener()

        //Añadir botón que permite acceder a la creación de categorías
        Button boton_crear_categorias =(Button)findViewById(R.id.boton_crear_categorias);

        //Poner el botón a la escucha de ser pulsado. Si se pulsa se accede a la actividad "CrearCategorias"
        boton_crear_categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = new Intent(getApplicationContext(),CrearCategorias.class);
                startActivity(intent2);

            }
        });
        
    }//end onCreate()


    /*
    * Inicia una nueva actividad pasándole los datos que necesita para buscar en la BBDD el mensaje
    * que se le indica. Para ello realiza una consulta a la base de datos.
    * Finaliza la actividad actual.
    *
    * @param childPosition la fila del menú desplegable pulsada
    *
    * */

    public void getMensajeGeneral(int childPosition){

        //Crear objeto para conectar y leer datos de la BBDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Para almacenar los títulos de los mensajes
        ArrayList<String> titulos = new ArrayList<>();
        //Construir la consulta
        String RAW_QUERY = "SELECT id,titulo FROM general ORDER BY leido,fecha DESC";
        //Ejecutar la consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        //Mover el cursor para situarse en la fila deseada
        cursor.moveToPosition(childPosition);
        //Recoger el parámetro 'id'
        int id = cursor.getInt(0);
        //Recoger el parámetro 'titulo'
        String titulo = cursor.getString(1);
        //Crear intento para iniciar nueva actividad
        Intent intent = new Intent(getApplicationContext(),MuestraMensaje.class);
        //Insertar datos en el intento para que los use la actividad que se va a iniciar
        intent.putExtra("nombre_tabla","general");
        intent.putExtra("titulo",titulo);
        intent.putExtra("id_mensaje",id);
        //Comenzar la nueva actividad
        startActivity(intent);
        //finalizar actividad actual
        finish();
    }

   /*
    * Obtiene los títulos de todos los mensajes guardados en la tabla 'general'
    *
    * @return titulos un ArrayList<String> conteniendo los títulos.
    *
    * */

    public ArrayList<String> getTitulosGenerales (){

        //Crear objeto para conectar y leer datos de la BBDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Para almacenar los titulos
        ArrayList<String> titulos = new ArrayList<>();
        //Crear la consulta
        String RAW_QUERY = "SELECT titulo FROM general ORDER BY leido,fecha DESC";
        //Ejecutar la consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        //Moverse a la primera fila devuelta
        cursor.moveToFirst();

        //Añadir los títulos al array
        for(int i=0; i<cursor.getCount();i++){
            //Añadir
            titulos.add(cursor.getString(0));
            //Moverse a la fila siguiente
            cursor.moveToNext();
        }
        return titulos;
    }

    /*
    * Inicia una nueva actividad pasándole los datos que necesita para buscar en la BBDD el mensaje
    * que se le indica. Para ello realiza varias  consultas a la base de datos.
    * Finaliza la actividad actual.
    *
    * @param childPosition la fila del menú desplegable pulsada, para saber sobre que mensaje se ha pulsado
    * @param leido_igual_a para indicarle si se buscan mensajes leidos o sin leer
    *
    * */

    public void getMensajeFromTodos(int childPosition, String leido_igual_a){

        //Dado que la actividad que se va a iniciar desde este método necesita que le se pase el nombre
        //de la tabla que contiene el mensaje, la id del mensaje y el título, se realizan las operaciones
        //necesarias para ello. El proceso es el siguiente:
        //Se recogen los nombres de todas las tablas para usarlos en consultas.
        //Se consulta para cada tabla si contiene mensajes leídos.
        //Si contiene mensajes leídos:
                //Se guarda el nombre de la tabla.
                //Se guardan las ids de los mensajes leídos de esa tabla.
                //Se aumenta el contador en tantos mensajes leídos se encuentren, de manera
                //que cada posición del contador quede asignada a una id de mensajes de diferentes tablas.
                //Se guarda en HashMap<String,ArrayList<Integer>> el nombre de la tabla y las posiciones
                //del contador correspondientes.
                //Se guardan las ids de los mensajes leídos de cada tabla.
         //Cuando se tienen los nombres de las tablas que contienen mensajes no leídos:
         //Se recorren las tablas realizando las siguientes acciones:
                //Se recogen las posiciones del contador correspondientes a cada tabla con mensajes leídos.
                //Se compara el contador con el mensaje/fila pulsado (childPosition).
                //Si coincide implica que esa es la tabla que contiene el mensaje seleccionado
                //y además esa posición en contador es la que coincidirá con la id del mensaje, almacenada
                //en el array de ids.





        //Recoger los nombres de las tablas de la BBDD
        ArrayList<String> nombres_tablas = getNombresTablas();

       /* for (int i = 0; i<nombres_tablas.size();i++){

            Toast.makeText(this,"nombre_tablas["+i+"]"+nombres_tablas.get(i),Toast.LENGTH_SHORT).show();
        }*/

        //Para almacenar <nombreTabla,Array<contador>>
        HashMap<String,ArrayList<Integer>> cajon = new HashMap<>();

        //Para conectar con la BBDD
        BDDHelper miHelper = new BDDHelper(this);
        SQLiteDatabase bd = miHelper.getReadableDatabase();

        //Para recoger la respuesta a la consulta
        Cursor cursor;
        //Para llevar la cuenta de cuantos mensajes sin leer hay
        int contador = 0;
        //Para poder recoger elementos de un HashMap<key,value>
        ArrayList<String> tabla_con_datos_array_aux = new ArrayList<>();
        //Para almacenar las ids de los mensajes leídos
        ArrayList<Integer> ids = new ArrayList<>();

        //Recorrer las tablas en busca de mensajes leídos
        for(int i=0; i < nombres_tablas.size();i++){
            //Para almacenar un nuevo posible nombre
            String tabla_con_datos = "";
            //Para almacenar los diferentes valores de 'contador'
            ArrayList<Integer> lista_contador = new ArrayList<>();
            //Crear la consulta
            String RAW_QUERY = "SELECT id FROM '"+nombres_tablas.get(i)+"' WHERE "+leido_igual_a;
            //Ejecutar la consulta
            cursor=bd.rawQuery(RAW_QUERY,null);
            //Situarse en la primera fila del resultset devuelto
            cursor.moveToFirst();
            //Si la consulta a la tabla ha devuelto algún valor
            if(cursor.getCount()!= 0){
                //Esta tabla tiene datos (mensajes no leídos)
                tabla_con_datos = nombres_tablas.get(i);
               // Toast.makeText(this,"tabla con datos = "+tabla_con_datos,Toast.LENGTH_SHORT).show();

                //Recoger las ids de los mensajes no leídos
                for(int j=0;j<cursor.getCount();j++){
                    //Toast.makeText(this,"contador = "+contador,Toast.LENGTH_SHORT).show();

                    //Añadir valor de contador al array
                    lista_contador.add(contador);
                    //Aumentar contador
                    contador++;
                    //Añadir la id del mensaje no leído de esa tabla al array
                    ids.add(cursor.getInt(0));
                   // Toast.makeText(this,"ids = "+cursor.getInt(j),Toast.LENGTH_SHORT).show();

                    //Avanzar a la siguiente fila del resultset
                    cursor.moveToNext();
                }

                //Insertar valor en el HashMap<String,Array<Integer>>
                cajon.put(tabla_con_datos,lista_contador);
                //Insertar valor en el array
                tabla_con_datos_array_aux.add(tabla_con_datos);

            }
        }//end for

        //Buscar en que tabla se encuentra el mensaje y asociar contador con id:


        //Para ir recogiendo los distintos ArrayList<Integer> del HashMap<String,ArrayList<Integer>>
        ArrayList <Integer> contador_aux = new ArrayList<>();
        //Para almacenar el nombre de la tabla que contiene el mensaje seleccionado
        String nombre_tabla_final ="";

        //Buscar que la posición (fila pulsada) coincida con contador:

        //Recorrer el HashMap
        for(int i= 0;i<cajon.size();i++){

            //Recoger ArrayList<Integer> del HashMap
           contador_aux=cajon.get(tabla_con_datos_array_aux.get(i));

           //Recorrer el ArrayList<Integer>
           for(int j=0; j < contador_aux.size();j++){

               //Comprobar si ese valor de 'contador' coincide con el de la fila pulsada
               if(contador_aux.get(j) == childPosition){

                  // Log.d("CONTADORCHILD",contador_aux.get(j)+ " "+childPosition+" "+ids.size());

                  //Esta es la tabla que contiene el mensaje
                   nombre_tabla_final = tabla_con_datos_array_aux.get(i);

                   //break;
               }
           }
           //break?

        }

       /* for (int d = 0;d<ids.size();d++){
            Log.d("IDS ",""+ids.get(d));
        }*/


        //Asociar id de mensaje con posición de la fila pulsada
        int id_mensaje = ids.get(childPosition);

       // Toast.makeText(this,"id_mensaje = "+id_mensaje+ " child position = "+childPosition,Toast.LENGTH_SHORT).show();

        //Crear intento para iniciar nueva actividad
        Intent intent = new Intent(this,MuestraMensaje.class);
        //Añadir datos en el intento
        intent.putExtra("nombre_tabla","'"+nombre_tabla_final+"'");
        intent.putExtra("id_mensaje",id_mensaje);

        //Toast.makeText(this,"nombre tabla final = "+nombre_tabla_final,Toast.LENGTH_SHORT).show();
       // Log.d("NOMBRETABLAFINAL",nombre_tabla_final);

        //Recoger el título para pasarlo también como dato en el intento
        String query = "select titulo from '"+nombre_tabla_final+"' where id="+id_mensaje;
        Cursor otro_cursor = bd.rawQuery(query,null);

        otro_cursor.moveToFirst();

        String titulo = otro_cursor.getString(0);
        intent.putExtra("titulo",titulo);
        //Cerrar conexión con base de datos
        bd.close();
        //Comenzar nueva actividad
        startActivity(intent);
        //Finalizar actividad actual
        finish();
    }


    /*
    * Devuelve los nombres de los cursos para los que se han creado tablas en la BBDD.
    * Se usa para llenar el menú desplegable con los nombres de los cursos.
    *
    * @return nombres_cursos un Array<String> conteniendo los nombres de los cursos.
    *
    * */

    public ArrayList<String>getNombreCursos(){

        //Para almacenar los nombres de las tablas
        ArrayList<String> nombres_tablas = getNombresTablas();
        //Para almacenar los nombres de los cursos
        ArrayList<String> nombres_cursos = new ArrayList<>();

        //Buscar los nombres de tablas relacionadas con un curso
        for(int i=0; i<nombres_tablas.size();i++){
            //Para almacenar el nombre del curso
            String curso;
            //Comprobar si es tabla con mensajes de un curso
            if(nombres_tablas.get(i).startsWith("curso-")){
                //Adaptar el nombre descartando los caracteres no deseados
                curso= nombres_tablas.get(i).substring(6,nombres_tablas.get(i).indexOf("!"));
                curso = curso.replace("_"," ");
                nombres_cursos.add(curso);

            }
        }

        return nombres_cursos;
    }



    /*
   * Devuelve los nombres de los hijos para los que se han creado tablas en la BBDD.
   * Se usa para llenar el menú desplegable con los nombres de los hijos.
   *
   * @return nombres_hijos un Array<String> conteniendo los nombres de los hijos.
   *
   * */

    public ArrayList<String>getNombreHijos(){

        //Para almacenar los nombres de las tablas y usarlos en las consultas
        ArrayList<String> nombres_tablas = getNombresTablas();
        //Para almacenar los nombres de los hijos
        ArrayList<String> nombres_hijos = new ArrayList<>();


        //Recorrer las tablas
        for(int i=0; i<nombres_tablas.size();i++){

            String nombre;
            //Si es una tabla con mensajes para hijo
            if(nombres_tablas.get(i).startsWith("hijo-")){
                //Adaptar el nombre para mostrarlo en el menú
                nombre = nombres_tablas.get(i).substring(5,nombres_tablas.get(i).indexOf("!"));
                nombre = nombre.replace("_"," ");
                nombres_hijos.add(nombre);
            }
        }

        return nombres_hijos;
    }


     /*
    * Devuelve los títulos de todos los mensajes leídos. Se usa para llenar el menú desplegable
    * de la categoría "Ver todos leídos".
    *
    * @param query que indica si se buscan mensajes leídos o no leídos
    *
    * @return titulos_mensajes un ArrayList<String> conteniendo los títulos de los mensajes no leídos.
    *
    * */

    public ArrayList<String> getTitulosTodos(String query) {

        //Para almacenar los nombres de las tablas
        ArrayList<String> nombres_tablas = getNombresTablas();
        //Para almacenar los títulos de los mensajes
        ArrayList<String> titulos_mensajes = new ArrayList<String>();

        //Para conectar y leer datos de la BBDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Recorrer las tablas recogiendo id y título de cada mensaje leído
        for (int i = 0; i<nombres_tablas.size();i++) {
            //Crear consulta
            String RAW_QUERY = "SELECT id,titulo FROM '"+nombres_tablas.get(i)+"' WHERE "+query;
            //Para almacenar la id y el título
            String id_titulo="";
            //Ejecutar consulta
            Cursor cursor = db.rawQuery(RAW_QUERY, null);
            //Colocarse en el cursor
            cursor.moveToFirst();

            //Recorrer el cursos concatenando resultados para formar el texto de las filas desplegadas
            for(int j = 0;j<cursor.getCount();j++) {
                //Toast.makeText(this,"cursor.getString(0) = "+cursor.getString(0),Toast.LENGTH_LONG).show();
                //Log.d("MIFALLO", cursor.getString(0));

                //Concatenar
                id_titulo = String.valueOf(cursor.getInt(0))+"."+cursor.getString(1);
                //Añadir al array
                titulos_mensajes.add(id_titulo);
                //Moverse a la siguiente posición del resultset
                cursor.moveToNext();
            }
        }
        //Cerrar conexión con la BBDD
        db.close();

        return titulos_mensajes;
}

    /*
     * Devuelve los nombres de las categorías creadas por el usuario.
     *
     * @return categorias un ArrayList<String> conteniendo los nombres de las categorías.
     *
     * */

    public ArrayList<String> getCategorias(){

        //Para almacenar los nombres de las categorías
        ArrayList<String>categorias = new ArrayList<>();
        //Para poder leer datos de la BBDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Crear consulta y ejecutarla
        String RAW_QUERY = "SELECT nombre FROM categorias";
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        //Recoger resultados y llenar el Array
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

        //Para leer datos de la BBDD
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Para almacenar los nombres de las tablas
        ArrayList<String> nombres_tablas = new ArrayList<>();
        //Para almacenar sólo los nombres de las tablas que vamos a usar
        ArrayList<String> nombres_tablas_aux = new ArrayList<>();


        //Crear consulta para obtener los nombres de las tablas
        String RAW_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";

        //Ejecutar consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

        //Recoger resultados de la consulta
        for(int i=0;i<cursor.getCount();i++) {

            String resultado = cursor.getString(0);

            nombres_tablas_aux.add(resultado);

           // Toast.makeText(getApplicationContext(), "RESULTADO = " + resultado, Toast.LENGTH_SHORT).show();

            cursor.moveToNext();
        }

        //Descartar las tablas que no se van a usar

        for (int i=0;i<nombres_tablas_aux.size();i++){

            if(!nombres_tablas_aux.get(i).equals("android_metadata") && !nombres_tablas_aux.get(i).equals("tokens")
                    && !nombres_tablas_aux.get(i).equals("categorias")){

                nombres_tablas.add(nombres_tablas_aux.get(i));
            }
        }

        //Cerra conexión con la BBDD
        db.close();

        return nombres_tablas;
    }



}//end of class
