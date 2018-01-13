package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
* Clase que se encarga de crear categorías a gusto del usuario.
*
* @author  Jose Luis
* @version 1.0
*/

public class CrearCategorias extends AppCompatActivity  {

    //Botones para crear o borrar categorías
    Button boton_crear, boton_borrar;
    //Campo de texto para darle nombre a la categoría que se desea crear
    EditText nombre_categoria;
    //Para conectar con la BBDD
    private final BDDHelper miHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_categorias);

        //Traer elementos desde el layout
        boton_crear = (Button) findViewById(R.id.boton_crear_categoria);
        boton_borrar = (Button)findViewById(R.id.boton_borrar_categorias);
        nombre_categoria = (EditText) findViewById(R.id.nombre_categoria);

        //Poner a la escucha el botón de creación de categorías
        boton_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Comprobar que se ha insertado algún nombre para la categoría
                if(nombre_categoria.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"Inserte algún nombre para la categoría",Toast.LENGTH_SHORT).show();
                }
                //Si se ha insertado algún nombre, se comienza con la creación de la categoría
                else{
                    //Para leer datos de la BBDD
                    SQLiteDatabase db1 = miHelper.getReadableDatabase();

                // Definir las columnas de la tabla de las que queremos leer datos
                String[] projection = {
                        "id"
                };

            // Filtrar resultados: WHERE nombre = ?
                String selection = "nombre = ?";
                String[] selectionArgs = {nombre_categoria.getText().toString()};

            // Definir orden de los datos que se van a mostrar
            /* String sortOrder =
                EstructuraBD.NOMBRE_COLUMNA_3 + " DESC";*/

                //Ejecutar la consulta
                try {
                    Cursor cursor = db1.query(
                            "categorias",                        // La tabla a consultar
                            projection,                                // Las columnas a devolver
                            selection,                                 // Las columnas en las que ejecutar el WHERE
                            selectionArgs,                             // Los valores para la clausula WHERE
                            null,                             // No agrupar las filas
                            null,                              // No filtrar por grupos de filas
                            null                              // No ordenar
                    );

                    cursor.moveToFirst();

                    //Comprobar que la categoría no existe y si true, se crea la nueva categoría
                    if (cursor.getCount() == 0) {

                        //Para poder escribir en la base de datos
                        SQLiteDatabase db = miHelper.getWritableDatabase();

                        // Crear nuevo mapa de valores donde los nombres de columna son la key
                        ContentValues values = new ContentValues();
                        values.put("nombre", nombre_categoria.getText().toString());


                        //Toast.makeText(getApplicationContext(), "Antes de guardar: ", Toast.LENGTH_LONG).show();

                        // Insertar el nuevo valor en la tabla, se devuelve la id de la nueva fila creada
                        long newRowId = db.insert("categorias", null, values);

                        Toast.makeText(getApplicationContext(), "Se Creó la categoria: " +
                                nombre_categoria.getText().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No existe el registro", Toast.LENGTH_SHORT).show();
                }
                //Si la categoría ya existe se limpia el campo de texto
                nombre_categoria.setText("");
            }

            }
        });


        //Poner el botón de borrado de categoría a la escucha de ser pulsado
        boton_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Crear intento para comenzar una nueva actividad
                Intent intent = new Intent(getApplicationContext(),BorrarCategorias.class);
                //Comenzar nueva actividad
                startActivity(intent);

              /*  SQLiteDatabase db = miHelper.getWritableDatabase();

                //Define 'where' part of query
                String selection =   "nombre LIKE ?";
                //specify arguments in placeholder
                String[] selectionArgs = {textoId.getText().toString()};

                //issue sql statement
                db.delete("categorias", selection, selectionArgs);

                Toast.makeText(getApplicationContext(), "Se borró el registro", Toast.LENGTH_SHORT).show();

                textoId.setText("");
                textoNombre.setText("");
                textoApellido.setText("");*/

            }

        });

    }

    //Método para definir que ocurre cuando se pulse el botón "Atrás" en el dispositivo
    public void onBackPressed() {
        //Crear intento para comenzar nueva actividad
        Intent intent = new Intent(this,MenuPrincipal.class);
        //Comenzar nueva actividad
        startActivity(intent);
        //Terminar actividad actual
        finish();
        //Llamar al metodo de la clase de la que se hereda
        super.onBackPressed();
    }
}//end of class



