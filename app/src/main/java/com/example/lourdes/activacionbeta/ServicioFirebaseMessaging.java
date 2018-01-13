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


/*
* Gestiona las acciones a realizar cuando se recibe un mensaje desde el servidor.
*
* @author  Jose Luis
* @version 1.0
* @since 20//11/2017
*
*/
public class ServicioFirebaseMessaging extends FirebaseMessagingService {

    //Para almacenar el destinatario(curso,alumno ó general)
    String destinatario = "";
    //Para el título del mensaje
    String titulo="";
    //Para la id del mensaje
    int id_mensaje = 0;


    /*
   * Gestion las acciones a realizar cuando se recibe un mensaje del servidor.
   *
   * @param remoteMessage el mensaje que se ha enviado desde el servidor.
   *
   * */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("ACTIVACION_BETA", "From: " + remoteMessage.getFrom());


        // Comprobar si el mensaje contiene un payload de notificación
        if (remoteMessage.getNotification() != null) {
            Log.d("ACTIVACION_BETA", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        //Recoger toda los datos que vienen en el mensaje
        Map<String,String> data = remoteMessage.getData();

        //Recoger el destinatario del mensaje
        destinatario = data.get("destinatario");
        //Log.d("DESTINATARIO",destinatario);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        //debido al nombre de las tablas
        //if(destinatario.startsWith("")){}

        //Recoger el resto de datos del mensaje
        String autor = data.get("autor");
        String fecha = data.get("fecha");
        titulo = data.get("titulo")/*remoteMessage.getNotification().getTitle()*/;
        String mensaje = data.get("texto")/*remoteMessage.getNotification().getBody()*/;
        //Para insertar el mensaje en la tabla correspondiente como no leído
        int leido = 0;


        //Para conectar con la base de datos
        final BDDHelper mDbHelper = new BDDHelper(getApplicationContext());
        //Para poder escribir datos en la BBDD
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Para comprobar si la tabla ya existe
       compruebaCurso(destinatario);

        //Crear un nuevo mapa de valores, donde los nombres de las columnas son las keys
        ContentValues values = new ContentValues();
        //values.put("id",1);
        values.put("autor",autor);
        values.put("fecha", fecha);
        values.put("titulo",titulo);
        values.put("mensaje",mensaje);
        values.put("leido",leido);


// Insert the new row, returning the primary key value of the new row

        //Se adapta para que no de error en la consulta
        destinatario = "'"+destinatario+"'";

        //Se insertan los datos en la tabla, si hay éxito, se devuelve el número de la nueva fila insertada
        long newRowId = db.insert(destinatario, null, values);

        //Si la inserción es correcta
        if(newRowId != -1){
            Log.d("MISVALORES",destinatario+" "+autor+" "+fecha+ " "+titulo+" "+mensaje+" "+leido);
        }
        //Si no se ha insertado la nueva fila
        else{
            Log.d("MISVALORESErroneos",destinatario+" "+autor+" "+fecha+ " "+titulo+" "+mensaje+" "+leido);
        }

        //Cerrar la conexión con la BBDD
        db.close();

        //Esto que sigue, hasta el notifyUser(titulo,mensaje) lo puedo eliminar ya que al final no lo uso.....

        //Abrir una nueva conexión para leer datos de la BBDD,
        db=mDbHelper.getReadableDatabase();
        //Crear consulta
        String query = "SELECT MAX(id) from "+destinatario;
        //Ejecutar consulta
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        id_mensaje = cursor.getInt(0);
        Log.d("id_mensaje",""+id_mensaje);

        //..... hasta aquí.

        //Toast.makeText(getApplicationContext(),"Destinatario = "+destinatario,Toast.LENGTH_SHORT).show();

        //Ejecutar el método que va a llamar a la creación de la notificación, pasándole datos necesarios
        notifyUser(titulo/*remoteMessage.getFrom()*/,mensaje/*remoteMessage.getNotification().getBody()*/);
    }

    /*
   * Crea un intento para iniciar la actividad que creará la notificación.
   *
   * @param from es el título del mensaje
   * @param notification es el cuerpo del mensaje
   *
   * */

    public void notifyUser(String from, String notification){

        //Para llamar al método que crea la notificación
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        //Crear el intento que se pasará como parámetro al método que crea la notificación y que será
        //a su vez la actividad que se iniciará cuando se pulse sobre la notificación
        Intent intent = new Intent(getApplicationContext(),MuestraMensaje.class);
        //Añadir valores en el intento
        intent.putExtra("nombre_tabla",destinatario);
        intent.putExtra("titulo",titulo);
        intent.putExtra("id_mensaje",id_mensaje);
        intent.putExtra("desde_notificacion",true);
        //Llamar al método showNotification() que es el que crea y muestra la notificación
        myNotificationManager.showNotification(from,notification,intent);
    }

    /*
   * Si en el portal, el alumno ha pasado de curso y así se refleja en las tablas de datos del servidor,
   * se hace necesario crear una tabla para almacenar los mensajes destinados a ese nuevo curso
   *
   * @param destinatario es el nombre de la tabla en la que se va a guardar el mensaje
   *
   * @return true simplemente para indicar que se ha ejecutado el método.
   *
   * */

    public boolean compruebaCurso(String destinatario){

        //Para conectar con la BBDD
        final BDDHelper mDbHelper = new BDDHelper(getApplicationContext());
        //Para escribir datos en la BBDD
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //Comprobar si la tabla ya existe, si no existe, es que el curso del alumno ha cambiado
        //(ya que el curso de los alumnos se modifica en el portal) y hay que crear una tabla nueva
        //para los mensajes destinados a ese curso.
        //Construir la consulta
        String RAW_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";
        //Ejecutar la consulta
        Cursor cursor1 = db.rawQuery(RAW_QUERY,null);

        cursor1.moveToFirst();
        //definir un booleano que si true, indica que la tabla ya existe; si false, el alumno ha pasado de curso y
        //hay que crear otra tabla para los mensajes del nuevo curso
        boolean marca = false;

        //Recorremos los resultados devueltos en busca de una coincidencia entre el destinatario y las tablas existentes
        for(int i= 0 ;i<cursor1.getCount();i++){
            String name = cursor1.getString(0);

            //Log.d("NOMBRESTABLAS",name);

            //Si la tabla existe
            if(name.equals(destinatario)){
                marca=true;
                return true;
            }
            cursor1.moveToNext();
        }
        //Si la tabla no existe, se crea otra nueva para poder almacenar los mensajes destinados a ese curso
        if(!marca){
            //Crear la consulta
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
                //Ejecutar la consulta
                db.execSQL(CREA_TABLA_CURSOS);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + destinatario, Toast.LENGTH_SHORT).show();
            }
        }
        //Cerrar conexión con la BBDD
        db.close();
        return true;
    }

}//end of class
