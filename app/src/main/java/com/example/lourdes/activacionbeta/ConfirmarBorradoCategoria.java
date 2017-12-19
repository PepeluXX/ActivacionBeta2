package com.example.lourdes.activacionbeta;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmarBorradoCategoria extends Activity {

    TextView texto_alerta;
    Button aceptar,cancelar;
    String categoria = "";
    private final BDDHelper miHelper = new BDDHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_borrado_categoria);

        Bundle datos = getIntent().getExtras();

        categoria = datos.getString("nombre_categoria");

        texto_alerta = (TextView)findViewById(R.id.textView4);
        aceptar = (Button)findViewById(R.id.button4);
        cancelar = (Button)findViewById(R.id.button5);

        texto_alerta.setText("¿Seguro que desea borrar la categoría \""+categoria+"\" ?");


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = miHelper.getWritableDatabase();

                //Define 'where' part of query
                String selection =  "nombre LIKE ?";
                //specify arguments in placeholder
                String []selectionArgs = {categoria};

                //issue sql statement
                db.delete("categorias",selection,selectionArgs);

                Intent intent = new Intent(getApplicationContext(),BorrarCategorias.class);
                startActivity(intent);
                db.close();

                finish();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),BorrarCategorias.class);
                startActivity(intent);

                finish();

            }
        });

    }
}
