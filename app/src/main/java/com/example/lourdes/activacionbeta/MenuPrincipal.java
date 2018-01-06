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

public class MenuPrincipal extends AppCompatActivity {

    //HashMap<String, List<String>> filtros_principales;
    List<String>subfiltros;
    ExpandableListView Exp_list;
    AdaptadorFiltrosPrincipalesVE adapter;

    public BDDHelper mDbHelper = new BDDHelper(this);

    HashMap<String, List<String>> filtros_principales = new LinkedHashMap<String, List<String>>();
    ArrayList<String> ver_todos = new ArrayList<String>();
    ArrayList<String> por_hijo = new ArrayList<String>();
    ArrayList<String> por_curso = new ArrayList<String>();
    ArrayList<String>por_categorias = new ArrayList<>();
    ArrayList<String>sin_leer = new ArrayList<>();
    ArrayList<String>generales = new ArrayList<>();

    ImageView sobre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);


        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout2);

        sobre = (ImageView)findViewById(R.id.sobre);

        ArrayList<String>check = new ArrayList<>();
        check = getTitulosTodos("leido=0");

        if(!check.isEmpty()){
            sobre.setImageResource(R.drawable.ic_email_enano);
        }

        ver_todos = getTitulosTodos("leido=1");
        por_hijo = getNombreHijos();
        por_curso = getNombreCursos();
        por_categorias = getCategorias();
        sin_leer = getTitulosTodos("leido=0");
        generales = getTitulosGenerales();


        Exp_list = (ExpandableListView)findViewById(R.id.exp_list);
        filtros_principales.put("Sin leer",sin_leer);
        filtros_principales.put("Por Hijo",por_hijo);
        filtros_principales.put("Por Curso",por_curso);
        filtros_principales.put("Generales",generales);
        filtros_principales.put("Ver todos leídos",ver_todos);
        filtros_principales.put("Por Categoría",por_categorias);


        subfiltros = new ArrayList<String>(filtros_principales.keySet());
        adapter = new AdaptadorFiltrosPrincipalesVE(this,filtros_principales,subfiltros);
        Exp_list.setAdapter(adapter);



        Exp_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

               /* Toast.makeText(getBaseContext(),filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition)+" from category"+
                        subfiltros.get(groupPosition)+" is selected",Toast.LENGTH_LONG).show();*/

                Intent intent = new Intent(getApplicationContext(),ListaMensajes.class);

                String filtro=subfiltros.get(groupPosition);

                Toast.makeText(getBaseContext(),"FILTRO ="+filtro,Toast.LENGTH_LONG).show();

                String nombre_tabla="";

                if(filtro.equals("Por Hijo")){

                     nombre_tabla = filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition).replace(" ","_");
                     nombre_tabla = "'hijo-"+nombre_tabla+"!'";
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    startActivity(intent);
                    finish();
                }
                else if(filtro.equals("Por Curso")){

                    nombre_tabla = filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition).replace(" ","_");
                    nombre_tabla = "'curso-"+nombre_tabla+"!'";
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    startActivity(intent);
                    finish();
                }
                else if(filtro.equals("Por Categoría")){
                    Intent intent1 = new Intent(getApplicationContext(),MuestraMensajeCategorias.class);
                    intent1.putExtra("childPosition",String.valueOf(childPosition));
                    startActivity(intent1);
                    finish();
                }
                else if(filtro.equals("Sin leer")){
                    getMensajeFromTodos(childPosition,"leido = 0");
                }
                else if(filtro.equals("Generales")){
                    Intent intento = new Intent(getApplicationContext(),ListaMensajes.class);
                    intent.putExtra("nombre_tabla","general");
                    startActivity(intent);
                    finish();

                }
                else {
                   /* nombre_tabla = subfiltros.get(groupPosition);
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    startActivity(intent);*/
                    getMensajeFromTodos(childPosition,"leido = 1");
                    //finish();
                }
                return false;
            }
        });

      /* final Button borra = new Button(this);
        borra.setText("Borrar categorías seleccionadas");
        //borra.setId;

        lm.addView(borra);*/

        Button boton_crear_categorias =(Button)findViewById(R.id.boton_crear_categorias);

        boton_crear_categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = new Intent(getApplicationContext(),CrearCategorias.class);
                startActivity(intent2);


            }
        });
        
    }


    public ArrayList<String> getTitulosGenerales (){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        ArrayList<String> titulos = new ArrayList<>();

        String RAW_QUERY = "SELECT titulo FROM general";
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        for(int i=0; i<cursor.getCount();i++){
            titulos.add(cursor.getString(0));
            cursor.moveToNext();
        }

        return titulos;

    }
    public void getMensajeFromTodos(int childPosition, String leido_igual_a){

        ArrayList<String> nombres_tablas = getNombresTablas();

       /* for (int i = 0; i<nombres_tablas.size();i++){

            Toast.makeText(this,"nombre_tablas["+i+"]"+nombres_tablas.get(i),Toast.LENGTH_SHORT).show();
        }*/

        HashMap<String,ArrayList<Integer>> cajon = new HashMap<>();

        BDDHelper miHelper = new BDDHelper(this);

        SQLiteDatabase bd = miHelper.getReadableDatabase();

        Cursor cursor;

        int contador = 0;

        ArrayList<String> tabla_con_datos_array_aux = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();


        for(int i=0; i < nombres_tablas.size();i++){
            String tabla_con_datos = "";
            ArrayList<Integer> lista_contador = new ArrayList<>();
            String RAW_QUERY = "SELECT id FROM '"+nombres_tablas.get(i)+"' WHERE "+leido_igual_a;
            cursor=bd.rawQuery(RAW_QUERY,null);
            cursor.moveToFirst();
            if(cursor.getCount()!= 0){
                tabla_con_datos = nombres_tablas.get(i);
               // Toast.makeText(this,"tabla con datos = "+tabla_con_datos,Toast.LENGTH_SHORT).show();
                for(int j=0;j<cursor.getCount();j++){
                    //Toast.makeText(this,"contador = "+contador,Toast.LENGTH_SHORT).show();
                    lista_contador.add(contador);
                    contador++;
                    ids.add(cursor.getInt(0));
                   // Toast.makeText(this,"ids = "+cursor.getInt(j),Toast.LENGTH_SHORT).show();
                    cursor.moveToNext();
                }

                cajon.put(tabla_con_datos,lista_contador);
                tabla_con_datos_array_aux.add(tabla_con_datos);
            }
        }

        //Ahora busco en que tabla está el mensaje, y con contador encuentro la id


        ArrayList <Integer> contador_aux = new ArrayList<>();
        String nombre_tabla_final ="";

        for(int i= 0;i<cajon.size();i++){

           contador_aux=cajon.get(tabla_con_datos_array_aux.get(i));

           for(int j=0; j < contador_aux.size();j++){

               if(contador_aux.get(j) == childPosition){

                   Log.d("CONTADORCHILD",contador_aux.get(j)+ " "+childPosition+" "+ids.size());
                   nombre_tabla_final = tabla_con_datos_array_aux.get(i);

                   //break;
               }
           }
           //break?

        }

        for (int d = 0;d<ids.size();d++){
            Log.d("IDS ",""+ids.get(d));
        }


        int id_mensaje = ids.get(childPosition);

        Toast.makeText(this,"id_mensaje = "+id_mensaje+ " child position = "+childPosition,Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MuestraMensaje.class);

        intent.putExtra("nombre_tabla","'"+nombre_tabla_final+"'");
        intent.putExtra("id_mensaje",id_mensaje);

        Toast.makeText(this,"nombre tabla final = "+nombre_tabla_final,Toast.LENGTH_SHORT).show();
        Log.d("NOMBRETABLAFINAL",nombre_tabla_final);
        String query = "select titulo from '"+nombre_tabla_final+"' where id="+id_mensaje;
        Cursor otro_cursor = bd.rawQuery(query,null);

        otro_cursor.moveToFirst();
        String titulo = otro_cursor.getString(0);
        intent.putExtra("titulo",titulo);
        bd.close();
        startActivity(intent);
        finish();

    }


    public ArrayList<String>getNombreCursos(){

        ArrayList<String> nombres_tablas = getNombresTablas();

        ArrayList<String> nombres_cursos = new ArrayList<>();



        for(int i=0; i<nombres_tablas.size();i++){

            String curso;

            if(nombres_tablas.get(i).startsWith("curso-")){

                curso= nombres_tablas.get(i).substring(6,nombres_tablas.get(i).indexOf("!"));
                curso = curso.replace("_"," ");
                nombres_cursos.add(curso);
            }
        }

        return nombres_cursos;
    }


    public ArrayList<String>getNombreHijos(){

        ArrayList<String> nombres_tablas = getNombresTablas();

        ArrayList<String> nombres_hijos = new ArrayList<>();



        for(int i=0; i<nombres_tablas.size();i++){

            String nombre;

            if(nombres_tablas.get(i).startsWith("hijo-")){

                nombre = nombres_tablas.get(i).substring(5,nombres_tablas.get(i).indexOf("!"));
                nombre = nombre.replace("_"," ");
                nombres_hijos.add(nombre);
            }
        }

        return nombres_hijos;
    }



    public ArrayList<String> getTitulosTodos(String query) {


        ArrayList<String> nombres_tablas = getNombresTablas();

        ArrayList<String> titulos_mensajes = new ArrayList<String>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        for (int i = 0; i<nombres_tablas.size();i++) {

            String RAW_QUERY = "SELECT id,titulo FROM '"+nombres_tablas.get(i)+"' WHERE "+query;

            String id_titulo="";

            Cursor cursor = db.rawQuery(RAW_QUERY, null);

            cursor.moveToFirst();

            for(int j = 0;j<cursor.getCount();j++) {
                //Toast.makeText(this,"cursor.getString(0) = "+cursor.getString(0),Toast.LENGTH_LONG).show();
                Log.d("MIFALLO", cursor.getString(0));

                id_titulo = String.valueOf(cursor.getInt(0))+"."+cursor.getString(1);
                titulos_mensajes.add(id_titulo);

                cursor.moveToNext();
            }
        }
        db.close();
        return titulos_mensajes;

}

    public ArrayList<String> getCategorias(){

        ArrayList<String>categorias = new ArrayList<>();
       // categorias.add("Crear categoría");

        SQLiteDatabase db = mDbHelper.getReadableDatabase();


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

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

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
