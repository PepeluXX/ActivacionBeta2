package com.example.lourdes.activacionbeta;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class ConfirmarBorradoMensaje extends Activity {

    Button borrado_mensaje_definitivo, cancela;
    private final BDDHelper miHelper = new BDDHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_borrado_mensaje);

        borrado_mensaje_definitivo = (Button)findViewById(R.id.boton_borrado_definitivo);
        cancela = (Button)findViewById(R.id.cancela);

        final Bundle datos = getIntent().getExtras();

        borrado_mensaje_definitivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = miHelper.getWritableDatabase();

                //Define 'where' part of query
                String selection = "id LIKE ?";
                //specify arguments in placeholder
                String []selectionArgs = {String.valueOf(datos.getInt("id_mensaje"))};

                //issue sql statement
                db.delete(datos.getString("nombre_tabla"),selection,selectionArgs);
                db.close();
                Intent intent = new Intent(getApplicationContext(), ListaMensajes.class);
                intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
                startActivity(intent);

                finish();

                Toast.makeText(getApplicationContext(),"Se borró el mensaje",Toast.LENGTH_SHORT).show();
            }
        });

        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MuestraMensaje.class);
                intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
                intent.putExtra("id_mensaje",datos.getInt("id_mensaje"));
                intent.putExtra("titulo",datos.getString("titulo"));
                startActivity(intent);
                finish();
            }
        });

    }
}
