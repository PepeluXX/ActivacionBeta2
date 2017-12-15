package com.example.lourdes.activacionbeta;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity {

    //HashMap<String, List<String>> filtros_principales;
    List<String>subfiltros;
    ExpandableListView Exp_list;
    AdaptadorFiltrosPrincipalesVE adapter;

    public BDDHelper mDbHelper = new BDDHelper(this);

    HashMap<String, List<String>> filtros_principales = new HashMap<String, List<String>>();
    ArrayList<String> ver_todos = new ArrayList<String>();
    ArrayList<String> por_hijo = new ArrayList<String>();
    ArrayList<String> por_curso = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ver_todos = getTitulosTodos();
        por_hijo = getNombreHijos();
        por_curso = getNombreCursos();
        //por_categorias = getCategorias();


        Exp_list = (ExpandableListView)findViewById(R.id.exp_list);
        filtros_principales.put("Ver todos",ver_todos);
        filtros_principales.put("Por Hijo",por_hijo);
        filtros_principales.put("Por Curso",por_curso);
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
                }
                else if(filtro.equals("Por Curso")){

                    nombre_tabla = filtros_principales.get(subfiltros.get(groupPosition)).get(childPosition).replace(" ","_");
                    nombre_tabla = "'curso-"+nombre_tabla+"!'";
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    startActivity(intent);
                }
                else{
                    nombre_tabla = subfiltros.get(groupPosition);
                    intent.putExtra("nombre_tabla",nombre_tabla);
                    startActivity(intent);
                }



                return false;
            }
        });

        
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



    public ArrayList<String> getTitulosTodos() {


        ArrayList<String> nombres_tablas = getNombresTablas();

        ArrayList<String> titulos_mensajes = new ArrayList<String>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        for (int i = 0; i<nombres_tablas.size();i++) {

            String RAW_QUERY = "SELECT titulo FROM '"+nombres_tablas.get(i)+"' ORDER BY fecha DESC";

            Cursor cursor = db.rawQuery(RAW_QUERY, null);

            cursor.moveToFirst();

            for(int j = 0;j<cursor.getCount();j++) {
                //Toast.makeText(this,"cursor.getString(0) = "+cursor.getString(0),Toast.LENGTH_LONG).show();
                Log.d("MIFALLO", cursor.getString(0));

                titulos_mensajes.add(cursor.getString(0));

                cursor.moveToNext();
            }
        }
        db.close();
        return titulos_mensajes;

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
