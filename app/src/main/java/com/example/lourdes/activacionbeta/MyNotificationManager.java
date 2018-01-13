package com.example.lourdes.activacionbeta;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

/*
* Se encarga de gestionar las notificaciones en el dispositivo siempre que se reciba un mensaje a la aplicación.
*
* @author  Jose Luis
* @version 1.0
* @since   21/11/2017
*/

public class MyNotificationManager {

    //Contexto de esta actividad
    private Context ctx;
    //Id de la notificación, puede ser cualquier valor ya que no afecta.
    public static final int NOTIFICATION_ID = 234;
    //Para indicar que sonido se va a usar cuando se reciba el mensaje
    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    //Constructor
    public MyNotificationManager(Context ctx) {

        this.ctx = ctx;
    }

    /*
   * Crea una notificación.
   *
   * @param from el título del mensaje
   * @param notification el cuerpo del mensaje
   * @param intent con información de la actividad desde la que se ha llamado al método
   *
   * */
    public void showNotification (String from, String notification, Intent intent){

        //Definir un intento y acciones a realizar cuando sea llamado
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //Para configurar la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        Notification mNotification = builder.setSmallIcon(R.mipmap.ic_launcher)
                //Se definen los parámetros de la notificación
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(from)
                .setContentText(notification)
                .setSound(uri)
                .setSmallIcon(R.drawable.ic_face)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_face))
                .build();

        //Definir los flags de la notificación
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        //Para notificar al usuario sobre el suceso ocurrido (se recibió el mensaje)
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        //Ejecutar la notificación
        notificationManager.notify(NOTIFICATION_ID,mNotification);
    }

}//end of class