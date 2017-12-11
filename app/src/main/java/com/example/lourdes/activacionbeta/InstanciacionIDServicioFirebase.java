package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Lourdes on 20/11/2017.
 */

//esta clase se encarga de conectar con los servidores de firebase para solicitar un nuevo token.
//si firebase detecta que este dispositivo tiene un token asignado,no le asigna ninguno ¿O LE VUELVE A ASIGNAR EL QUE YA TIENE
//TODO: COMPROBAR EL COMPORTAMIENTO, Y SI EN EL LOG, CADA VEZ QUE SE INICIA LA APLICACIÓN MUESTRA EL MISMO TOKEN.

public class InstanciacionIDServicioFirebase extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("ACTIVACION_BETA", "Refreshed token: " + refreshedToken);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
        storeToken(refreshedToken);
    }


    public void storeToken(String token){
        SharedPrefManager.getInstance(getApplicationContext()).storeToken(token);
    }
}
