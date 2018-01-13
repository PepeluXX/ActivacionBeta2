package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/*
* Se encarga de mostrar el mensaje seleccionado por el usuario en la lista de mensajes.
*
* @author  Jose Luis
* @version 1.0
*
*/
public class MuestraMensaje extends AppCompatActivity {

    //Para conectar con la BBDD
    final BDDHelper miHelper = new BDDHelper(this);
    //Campos de texto para presentar datos sobre el mensaje
    TextView texto_autor,texto_fecha,texto_titulo,texto_mensaje;
    //Botones con imagen que representan las opciones a ejecutar sobre un mensaje
    ImageButton boton_borrar_mensaje,boton_guardar_mensaje;
    //Para almacenar la id del mensaje a mostrar
    int id;
    //Para recoger los datos que vienen en el intento desde la actividad que inició esta actividad
    Bundle datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_mensaje);

        //Traer elementos desde el layout
        texto_autor = (TextView)findViewById(R.id.texto_autor);
        texto_fecha = (TextView)findViewById(R.id.texto_fecha);
        texto_titulo = (TextView)findViewById(R.id.texto_titulo);
        texto_mensaje = (TextView)findViewById(R.id.texto_mensaje);
        boton_borrar_mensaje = (ImageButton)findViewById(R.id.boton_borrar_mensaje);
        boton_guardar_mensaje = (ImageButton)findViewById(R.id.boton_guardar_mensaje);

        //Recoger datos del intento
        datos = getIntent().getExtras();

        //Construir la consulta
        String RAW_QUERY = "SELECT autor,fecha,titulo,mensaje,id FROM "+datos.getString("nombre_tabla")+
                " WHERE titulo = '"+datos.getString("titulo")+"' AND id = "+datos.getInt("id_mensaje");

        //Log.d("VALORES_INTENT",datos.getString("nombre_tabla")+" "+datos.getString("titulo")+" "+datos.getInt("id_mensaje"));

        //Para leer datos de la BBDD
        SQLiteDatabase db = miHelper.getReadableDatabase();
        //Ejecutar la consulta
        Cursor cursor = db.rawQuery(RAW_QUERY,null);
        cursor.moveToFirst();

        //Configurar los campos de texto para mostrar el mensaje (el for sobra, ya que solo se muestra un mensaje)
        for(int i=0;i<cursor.getCount();i++){
            texto_autor.setText("Enviado por: "+cursor.getString(0));
            texto_fecha.setText("Recibido el: " +cursor.getString(1));
            texto_titulo.setText("Asunto:\n"+cursor.getString(2));
            texto_mensaje.setText("Mensaje:\n"+cursor.getString(3));
            id=cursor.getInt(4);
            cursor.moveToNext();
        }

        //Cerrar conexión con la BBDD
        db.close();

        //Nueva conexión con la BBDD para marcar el mensaje como leído
        SQLiteDatabase db2=miHelper.getWritableDatabase();

        //Definir valores a insertar en la tabla
        ContentValues values = new ContentValues();
        values.put("leido", 1);


        //Indicar la fila en la que se van a modificar datos
        String selection = " id LIKE ?";
        String[] selectionArgs = {String.valueOf(datos.getInt("id_mensaje"))};
        //Ejecutar la consulta
        int count = db2.update(
                datos.getString("nombre_tabla"),
                values,
                selection,
                selectionArgs);
        //Cerrar conexión con la BBDD
        db2.close();


        //Poner el botón de borrar a la escucha de ser pulsado

        boton_borrar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Definir intento para iniciar una nueva actividad
                Intent intent = new Intent(getApplicationContext(),ConfirmarBorradoMensaje.class);
                //Insertar datos en el intento para que los use la actividad a iniciar
                intent.putExtra("id_mensaje",id);
                intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
                intent.putExtra("titulo",datos.getString("titulo"));
                //Iniciar nueva actividad
                startActivity(intent);
                //Finalizar actividad actual
                finish();

            }
        });


        //Poner el botón de guardar mensaje(en categoría) a la escucha de ser pulsado

        boton_guardar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Crear intento para iniciar nueva actividad
                Intent intent = new Intent(getApplicationContext(),GuardarEnCategoria.class);
                //Insertar datos en el intento
                intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
                intent.putExtra("id",String.valueOf(id));
                //Comenzar nueva actividad
                startActivity(intent);

                //finish();

            }
        });
    }

    //Definira acciones a realizar cuando se pulse el botón 'Atrás' en el dispositivo
    @Override
    public void onBackPressed() {
        //Crear intento para iniciar nueva actividad
        Intent intent = new Intent(this,ListaMensajes.class);
        //Añadir datos en el intento
        intent.putExtra("nombre_tabla",datos.getString("nombre_tabla"));
        //Iniciar nueva actividad
        startActivity(intent);
        //Finalizar actividad actual
        finish();
        //Ejecutar método de la clase superior
        super.onBackPressed();
    }

}//end of class
