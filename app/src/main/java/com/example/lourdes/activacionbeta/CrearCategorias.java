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

public class CrearCategorias extends AppCompatActivity  {

    Button boton_crear, boton_borrar;
    EditText nombre_categoria;
    private final BDDHelper miHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_categorias);

        boton_crear = (Button) findViewById(R.id.boton_crear_categoria);
        boton_borrar = (Button)findViewById(R.id.boton_borrar_categorias);
        nombre_categoria = (EditText) findViewById(R.id.nombre_categoria);

        boton_crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(nombre_categoria.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"Inserte algún nombre para la categoría",Toast.LENGTH_SHORT).show();
                }
                else{
                SQLiteDatabase db1 = miHelper.getReadableDatabase();

                // Define a projection that specifies which columns from the database
// you will actually use after this query.
                String[] projection = {
                        "id"
                };

// Filter results WHERE "title" = 'My Title'
                String selection = "nombre = ?";
                String[] selectionArgs = {nombre_categoria.getText().toString()};

// How you want the results sorted in the resulting Cursor
       /* String sortOrder =
                EstructuraBD.NOMBRE_COLUMNA_3 + " DESC";*/


                try {
                    Cursor cursor = db1.query(
                            "categorias",                     // The table to query
                            projection,                               // The columns to return
                            selection,                                // The columns for the WHERE clause
                            selectionArgs,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            null                                 // The sort order
                    );

                    cursor.moveToFirst();

                    if (cursor.getCount() == 0) {

                        SQLiteDatabase db = miHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put("nombre", nombre_categoria.getText().toString());


                        Toast.makeText(getApplicationContext(), "Antes de guardar: ", Toast.LENGTH_LONG).show();

// Insert the new row, returning the primary key value of the new row
                        long newRowId = db.insert("categorias", null, values);

                        Toast.makeText(getApplicationContext(), "Se Creó la categoria: " +
                                nombre_categoria.getText().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No existe el registro", Toast.LENGTH_SHORT).show();
                }
                nombre_categoria.setText("");
            }

            }
        });


        boton_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),BorrarCategorias.class);
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
}


