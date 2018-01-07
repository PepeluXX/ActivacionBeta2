package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lourdes on 20/11/2017.
 */

public class ServicioFirebaseMessaging extends FirebaseMessagingService {


    String destinatario = "";
    String titulo="";
    int id_mensaje = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("ACTIVACION_BETA", "From: " + remoteMessage.getFrom());


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("ACTIVACION_BETA", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        Map<String,String> data = remoteMessage.getData();



         destinatario = data.get("destinatario");
        Log.d("DESTINATARIO",destinatario);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        //Toast.makeText(this,"DESTINATARIO = "+destinatario,Toast.LENGTH_LONG).show();

        //debido al nombre de las tablas
        //if(destinatario.startsWith("")){}


        String autor = data.get("autor");
        String fecha = data.get("fecha");
        titulo = data.get("titulo")/*remoteMessage.getNotification().getTitle()*/;
        String mensaje = data.get("texto")/*remoteMessage.getNotification().getBody()*/;
        int leido = 0;



        final BDDHelper mDbHelper = new BDDHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

       compruebaCurso(destinatario);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put("id",1);
        values.put("autor",autor);
        values.put("fecha", fecha);
        values.put("titulo",titulo);
        values.put("mensaje",mensaje);
        values.put("leido",leido);


// Insert the new row, returning the primary key value of the new row

        destinatario = "'"+destinatario+"'";

        long newRowId = db.insert(destinatario, null, values);

        if(newRowId != -1){
            Log.d("MISVALORES",destinatario+" "+autor+" "+fecha+ " "+titulo+" "+mensaje+" "+leido);
        }
        else{
            Log.d("MISVALORESErroneos",destinatario+" "+autor+" "+fecha+ " "+titulo+" "+mensaje+" "+leido);
        }


        db.close();

        db=mDbHelper.getReadableDatabase();
        String query = "SELECT MAX(id) from "+destinatario;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
         id_mensaje = cursor.getInt(0);

         Log.d("id_mensaje",""+id_mensaje);


        //Toast.makeText(getApplicationContext(),"Destinatario = "+destinatario,Toast.LENGTH_SHORT).show();

        notifyUser(titulo/*remoteMessage.getFrom()*/,mensaje/*remoteMessage.getNotification().getBody()*/);
    }

    public void notifyUser(String from, String notification){

        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(),MuestraMensaje.class);
        intent.putExtra("nombre_tabla",destinatario);
        intent.putExtra("titulo",titulo);
        intent.putExtra("id_mensaje",id_mensaje);
        intent.putExtra("desde_notificacion",true);
        myNotificationManager.showNotification(from,notification,intent);
    }

    public boolean compruebaCurso(String destinatario){

        final BDDHelper mDbHelper = new BDDHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //Comprobamos si la tabla ya existe, si no existe, es que el curso del alumno ha cambiado
        //(ya que el curso de los alumnos se modifica en el portal) y hay que crear una tabla nueva
        //para los mensajes destinados a ese curso.
        String RAW_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";
        Cursor cursor1 = db.rawQuery(RAW_QUERY,null);

        cursor1.moveToFirst();

        boolean marca = false;

        for(int i= 0 ;i<cursor1.getCount();i++){
            String name = cursor1.getString(0);

            Log.d("NOMBRESTABLAS",name);
            if(name.equals(destinatario)){
                marca=true;
                return true;
            }
            cursor1.moveToNext();
        }

        if(!marca){

            String CREA_TABLA_CURSOS =
                    "CREATE TABLE '" + destinatario+ "' (" +
                            "id INTEGER PRIMARY KEY," +
                            "autor TEXT," +
                            "fecha TEXT," +
                            "titulo TEXT," +
                            "mensaje TEXT," +
                            "leido INTEGER," +
                            "categoria TEXT)";


            try {
                db.execSQL(CREA_TABLA_CURSOS);


            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + destinatario, Toast.LENGTH_SHORT).show();

            }

        }
        db.close();
        return true;
    }

}
