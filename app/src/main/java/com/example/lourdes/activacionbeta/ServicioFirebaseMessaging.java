package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
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



        String destinatario = data.get("destinatario");
        destinatario = "'"+destinatario+"'";

        //Toast.makeText(this,"DESTINATARIO = "+destinatario,Toast.LENGTH_LONG).show();

        //debido al nombre de las tablas
        //if(destinatario.startsWith("")){}


        String autor = data.get("autor");
        String fecha = data.get("fecha");
        String titulo = remoteMessage.getNotification().getTitle();
        String mensaje = remoteMessage.getNotification().getBody();
        int leido = 0;



        final BDDHelper mDbHelper = new BDDHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put("id",1);
        values.put("autor",autor);
        values.put("fecha", fecha);
        values.put("titulo",titulo);
        values.put("mensaje",mensaje);
        values.put("leido",leido);


// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(destinatario, null, values);

        if(newRowId != -1){
            Log.d("MISVALORES",destinatario+" "+autor+" "+fecha+ " "+titulo+" "+mensaje+" "+leido);
        }


        db.close();


        //Toast.makeText(getApplicationContext(),"Destinatario = "+destinatario,Toast.LENGTH_SHORT).show();

        notifyUser(remoteMessage.getFrom(),remoteMessage.getNotification().getBody());
    }

    public void notifyUser(String from, String notification){

        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from,notification,new Intent(getApplicationContext(),MainActivity.class));
    }

}
