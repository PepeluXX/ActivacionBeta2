package com.example.lourdes.activacionbeta;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/*
* Clase que se encarga de asignar un mensaje a una categoría .
*
* @author  Jose Luis
* @version 1.0
*/

public class GuardarEnCategoria extends Activity {

    //Para conectar con la BBDD
    private final BDDHelper miHelper = new BDDHelper(this);

    //Texto para indicar que no existen categorías creadas, si fuese el caso
    TextView texto_no_hay_categorias;
    //Para recoger el resultado de la consulta
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_en_categoria);

        //Layout principal de la actividad
        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        //Crear los parámetos para definir el layout de los botones
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //Recoger los datos del intento que inició esta actividad
        final Bundle datos = getIntent().getExtras();
        //Para leer datos de la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();
        //Crear la consulta
        String RAW_QUERY = "SELECT nombre FROM categorias";
        //Ejecutar la consulta
        cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

       // Log.d("MICURSOR-ID-TABLA",cursor.getString(0)+" "+datos.getString("id")+" "+datos.getString("nombre_tabla"));

        //Si existen categorías creadas, se crea un botón para cada una de ellas
        if(cursor.getCount()!=0) {

            //Creación de los botones, uno por categoría
            for (int j = 0; j < cursor.getCount(); j++) {

                // Crear un linear layout para cada botón
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setBackgroundColor(12);

                //Crear el botón y definir parámetros del mismo
                final Button boton = new Button(getApplicationContext());
                boton.setText(cursor.getString(0));
                boton.setId(j);
                //Agregar layout definido para el boton
                boton.setLayoutParams(params);

                //Recoger el nombre de la categoría
                final String valor = cursor.getString(0);
                //Poner el botón a la escucha se ser pulsado
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Para escribir datos en la BBDD
                        SQLiteDatabase db = miHelper.getWritableDatabase();
                        //Para insertar diferentes valores
                        ContentValues values = new ContentValues();
                        values.put("categoria",valor );

                        //Definir que fila se va a actualizar
                        String selection = "id LIKE ?";
                        String[] selectionArgs = {datos.getString("id") };

                        //Ejecutar la consulta
                        int count = db.update(
                                datos.getString("nombre_tabla"),
                                values,
                                selection,
                                selectionArgs);
                        //Si la inserción es correcta, se comunica al usuario
                        if(count !=0){
                            Toast.makeText(getApplicationContext(),"Se guardó el mensaje como perteneciente a la categoría "+valor,Toast.LENGTH_SHORT).show();
                        }
                        //Terminar actividad actual
                        finish();
                    }
                });
                //Avanzar el cursor para la creación del siguiente botón
                cursor.moveToNext();
                //Añadir botón al layout del botón
                ll.addView(boton);
                //Añadir layout del botón al layout principal
                lm.addView(ll);
            }

        }
        //Si aún no existen categorías creadas
        else{
           texto_no_hay_categorias = (TextView)findViewById(R.id.texto_confirma_guardado);
            texto_no_hay_categorias.setText("No hay categorías creadas.");
          /* Toast.makeText(getApplicationContext(),"Aún no se ha creado ninguna categoría",Toast.LENGTH_SHORT).show();
           finish();*/
        }

    }
}//end of class
