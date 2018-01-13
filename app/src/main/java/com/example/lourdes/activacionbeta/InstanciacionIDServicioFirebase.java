package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/*
* Se encarga de conectar con los servidores de Firebase, los cuales, de manera transparente para el usuario, van a
* detectar si se trata de una aplicación recientemente instalada o de una aplicación que ya tiene un token asignado
* para el dispositivo en el que está instalada.
*
* @author  Jose Luis
* @version 1.0
* @since 20//11/2017
*
*/


public class InstanciacionIDServicioFirebase extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        // Obtener un nuevo InstaceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("ACTIVA", "Refreshed token: " + refreshedToken);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        storeToken(refreshedToken);
    }


    /*
    * Almacena el token de manera persistente
    *
    * @param token el token a almacenar nombres_cursos
    *
    * */
    public void storeToken(String token){
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
