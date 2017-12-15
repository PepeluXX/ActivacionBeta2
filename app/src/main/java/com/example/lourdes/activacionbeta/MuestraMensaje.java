package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MuestraMensaje extends AppCompatActivity {

    final BDDHelper miHelper = new BDDHelper(this);

    TextView texto_autor,texto_fecha,texto_titulo,texto_mensaje;

    int id;

    Bundle datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_mensaje);

        texto_autor = (TextView)findViewById(R.id.texto_autor);
        texto_fecha = (TextView)findViewById(R.id.texto_fecha);
        texto_titulo = (TextView)findViewById(R.id.texto_titulo);
        texto_mensaje = (TextView)findViewById(R.id.texto_mensaje);

        datos = getIntent().getExtras();

        String RAW_QUERY = "SELECT autor,fecha,titulo,mensaje,id FROM "+datos.getString("nombre_tabla")+
                " WHERE titulo = '"+datos.getString("titulo")+"' AND id = "+datos.getInt("id_mensaje");

        SQLiteDatabase db = miHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        for(int i=0;i<cursor.getCount();i++){
            texto_autor.setText(cursor.getString(0));
            texto_fecha.setText(cursor.getString(1));
            texto_titulo.setText(cursor.getString(2));
            texto_mensaje.setText(cursor.getString(3));
            id=cursor.getInt(4);
            cursor.moveToNext();
        }

        db.close();

        //marcamos el mensaje como leído

        SQLiteDatabase db2=miHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put("leido", 1);


// Which row to update, based on the title
        String selection = " id LIKE ?";
        String[] selectionArgs = {String.valueOf(datos.getInt("id_mensaje"))};

        int count = db2.update(
                datos.getString("nombre_tabla"),
                values,
                selection,
                selectionArgs);

        db2.close();
        //miHelper.close();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,ListaMensajes.class);
        intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
        startActivity(intent);
        finish();

        super.onBackPressed();
    }
}
