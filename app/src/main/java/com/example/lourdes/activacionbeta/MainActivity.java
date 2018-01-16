package com.example.lourdes.activacionbeta;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * La clase MainActivity es por donde comienza la aplicación. Muestra un formulario a rellenar.
 * Si los datos insertados son correctos. Dicho formulario no se presenta más.
 *
 * @author  Jose Luis
 * @version 1.0
 * @since   12/01/2018
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //campos de texto para el DNI y el Password
    private EditText editTextDNI,editTextPassword;

    //Botón para el envío del formulario
    private Button botonRegistro;

    //URL a la que conectar para enviar los datos
    private static final String URL_REGISTRO_TOKEN= "https://www.portaldedesarrollo.com/TokenRegistration.php";

    //Cadena de texto para almacenar el mensaje respuesta del servidor
    String respuesta_servidor;

    //Objeto de clase para acceder a la BBDD SQLite
    final BDDHelper mDbHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se recogen los elementos del layout
        editTextDNI = (EditText) findViewById(R.id.editTextDNI);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        botonRegistro = (Button)findViewById(R.id.botonRegistro);

        //Se recoge el token, que se crea cuando se inicia la aplicación por primera vez
        String token = SharedPrefManager.getInstance(this).getToken();


        //Aquí comienza la gestión del inicio de la aplicación.
        //En primer lugar se comprueba si el registro en el portal web se ha producido y si se ha generado el token.
        //Si es la primera vez que se ha iniciado la aplicación, aparecerá el formulario para rellenar los datos.
        //Si el registro desde la aplicación ya se ha realizado, el formulario no se vuelve a mostrar nunca más (sólo en caso de desisntalación y reinstalación)
        // y se pasa a la actividad que contiene el menú principal para la gestión de mensajes.
        //El token se guarda en la base de datos únicamente si el registro en el portal web ha sido exitoso.

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //GET token
        String[] projection = {
                EstructuraBDD.COLUMNA_TOKEN
        };

        //WHERE id = 1
        String selection = EstructuraBDD.COLUMNA_ID + " = ?";
        String[] selectionArgs = {"1"};

        try{
            Cursor cursor = db.query(
                    EstructuraBDD.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            cursor.moveToFirst();


             //¿El token está guardado en la base de datos? esto implica que el registro en el portal (desde la aplicación) se produjo con éxito

            if(cursor.getString(0)!=null){

                //si el registro ya se produjo, pasamos al menú principal de gestión de mensajes

                //Toast.makeText(this,"La BD existe", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this,MenuPrincipal.class);

                startActivity(intent); //pasamos al menú de gestión de mensajes

                db.close();

                finish(); //finalizamos MainActivity
            }

        }catch(Exception e){
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Se pone el botón de registro a la escucha de eventos para cuando sea pulsado

        botonRegistro.setOnClickListener(this);

    }

    //sobre escritura del método onClick()

    @Override
    public void onClick(View view) {
        if(view == botonRegistro){
            registraToken();
        }
    }




    /*
    *Se encarga de conectar con el portal web y, si el usuario se encuentra previamente registrado en el portal,
    *se registra el token en la BBDD del portal, si dicho registro se produce con éxito, entonces se guarda el token también en la BBDD de la aplicación, para
    *la posterior comprobación y para que así el formulario de inicio no vuelva a mostrarse en posteriores ejecuciones de la aplicación
    */
    // NOTAS: ¿Guardar el token o guardar simplemente un booleano o un entero, al final cuando comienza la aplicación lo único que se hace
    //es comprobar si ya existe un registro.

    public void registraToken(){

        //se recogen los datos insertados por el usuario
        final String dni = editTextDNI.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //se comprueba que el usuario ha rellenado los campos. Se obliga a que rellene ambos campos para seguir.
        if(TextUtils.isEmpty(dni) || TextUtils.isEmpty(password)){

            Toast.makeText(this,"Por favor rellene los campos necesarios", Toast.LENGTH_LONG).show();
        }
        //Si se han insertado valores para usuario y password, se crea petición POST para enviar al servidor
        else{

            if(SharedPrefManager.getInstance(this).getToken() != null){

                //Configurar la petición POST que vamos a enviarle al servidor. Se crea un objeto de la clase StringRequest al que se
                //le pasa para crearlo el método de la petición, la URL, un método que describe las acciones a realizar cuando se obtiene una
                //respuesta del servidor y el método con acciones a realizar cuando se recibe un error (se recibe null)

                StringRequest stringRequest = new StringRequest(

                        //el método de la petición
                        Request.Method.POST,

                        //La URL del servidor
                        URL_REGISTRO_TOKEN,

                        //Acciones a realizar si se obtiene una respuesta del servidor
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try{

                                    //Almacena toda la respuesta del servidor a la petición
                                    JSONObject obj = new JSONObject(response);

                                    //En 'message' se encuentra el código que indica el resultado de la petición
                                    respuesta_servidor = obj.getString("message");
                                    Toast.makeText(getApplicationContext(),"Respuesta servidor = "+respuesta_servidor,Toast.LENGTH_LONG).show();
                                    //Si el token y los datos de usuario se han registrado correctamente en el portal

                                    if(respuesta_servidor.equals("OK")) {

                                        Toast.makeText(getApplicationContext(),"Token registrado OK",Toast.LENGTH_LONG).show();

                                        //comienza la inserción del token en la BBDD sqlite

                                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                                        //Se crea un nuevo map de valores (key,value), donde los nombres de las columnas de la tabla son las keys

                                        ContentValues values = new ContentValues();

                                        values.put(EstructuraBDD.COLUMNA_ID, "1");
                                        values.put(EstructuraBDD.COLUMNA_TOKEN, SharedPrefManager.getInstance(getApplicationContext()).getToken()); // o guadar otra cosa, lo importante es que ya se registró bien en el portal

                                        // Se inserta la nueva fila y se devuelve el valor de la clave primaria (id) de la nueva fila insertada,
                                        // en caso de error devolverá -1

                                        long newRowId = db.insert(EstructuraBDD.TABLE_NAME, null, values);

                                        //si el token se ha guardado correctamente
                                        if(newRowId != -1) {

                                          /*  Toast.makeText(getApplicationContext(), "Se guardó el registro con clave: " +
                                                    newRowId, Toast.LENGTH_LONG).show();*/
                                            //textViewToken.setText(SharedPrefManager.getInstance(getApplicationContext()).getToken());


                                            //Se crean las tablas necesarias, una para cada hijo

                                            //Se recogen los datos enviados en la respuesta del servidor
                                            String nombres_hijos = obj.getString("nombres_hijos");
                                            //se adapta para nombres compuestos, ya que los nombres de las tablas no aceptan espacios en blanco
                                            nombres_hijos = nombres_hijos.replace(" ","_");


                                            /*Toast.makeText(getApplicationContext(), "nombres_hijos: " +
                                                    nombres_hijos, Toast.LENGTH_LONG).show();*/


                                            //Para contar las ocurrencias de ','
                                            //cambiar por:
                                            // int count = StringUtils.countMatches(nombres_hijos,",");
                                            int count=0;
                                            String aux = "";

                                            //Los nombres de los hijos vienen desde el servidor en un String y separados por ','
                                            //se cuentan las ocurrencias y +1 es el número de hijos.

                                            for (int i=0; i < nombres_hijos.length(); i++) {

                                                aux = nombres_hijos.substring(i, i + 1);

                                                if (aux.equals(",")) {
                                                    count++;
                                                }

                                            }
                                            count+=1;

                                           //Almacenar los nombres en un Array para ir recorriendo y creando tablas en cada iteración
                                            String [] array_nombres = new String[count];
                                           //Para contar las posiciones del array
                                            int i1 =0;

                                            while(!nombres_hijos.equals("")){

                                                Toast.makeText(getApplicationContext(),"dentro del while",Toast.LENGTH_LONG).show();

                                                // true si sólo un hijo

                                                if(nombres_hijos.indexOf(",")==-1){

                                                    //Se guarda el nombre en el array...
                                                    array_nombres[i1] = nombres_hijos;
                                                    //... y adaptamos la condición necesaria para salir del while
                                                    nombres_hijos="";

                                                    //Toast.makeText(getApplicationContext(),"array_nombres["+i1+"] = "+array_nombres[i1],Toast.LENGTH_LONG).show();
                                                }
                                                //Si hay más de un hijo
                                                else{

                                                    //Recoger primer nombre
                                                    String nombre_aux = nombres_hijos.substring(0,nombres_hijos.indexOf(","));
                                                    //Guardar nombre en el array
                                                    array_nombres[i1] = nombre_aux;
                                                    //Readaptar el nombre para la siguiente iteración
                                                    nombres_hijos = nombres_hijos.substring(nombres_hijos.indexOf(",")+1,nombres_hijos.length());
                                                    //Para pasar a la siguiente posición del array
                                                    i1++;
                                                }

                                            }//end while

                                            //Crear una tabla por cada hijo para almacenar los mensajes destinados a ellos
                                            for(int i = 0;i<array_nombres.length;i++){
                                                //Crear consulta
                                                String CREA_TABLA_HIJO =
                                                        //Añadir 'hijo-' y '!' para luego facilitar la consulta de tablas, facilitando reconocer si es tabla de mensajes para hijo
                                                        "CREATE TABLE 'hijo-" + array_nombres[i]+ "!' (" +
                                                                 "id INTEGER PRIMARY KEY," +
                                                                 "autor TEXT," +
                                                                 "fecha TEXT," +
                                                                 "titulo TEXT," +
                                                                 "mensaje TEXT," +
                                                                 "leido INTEGER," +
                                                                 "categoria TEXT)";


                                                try {
                                                    //Se ejecuta la consulta para la creación de la tabla
                                                    db.execSQL(CREA_TABLA_HIJO);
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + array_nombres[i], Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            //Crear tablas para los mensajes destinados a un curso completo:

                                            //Recoger de la respuesta del servidor los cursos de los hijos
                                            String cursos_hijos = obj.getString("cursos_hijos");
                                            // para evitar espacios en blanco en el nombre de la tabla
                                            cursos_hijos = cursos_hijos.replace(" ","_");


                                            //Esto también debería de reducirlo
                                            int count2 = 0;
                                            String aux2 = "";

                                            for (int  i=0; i < cursos_hijos.length(); i++) {

                                                aux2 = cursos_hijos.substring(i, i + 1);

                                                if (aux2.equals(",")) {
                                                    count2++;
                                                }
                                            }
                                            count2+=1;

                                            //Para almacenar los nombres de los cursos
                                            String [] array_cursos = new String[count2];
                                            //Para las posiciones del array
                                            int i2 =0;

                                            while(!cursos_hijos.equals("")){

                                                //true si sólo un  curso
                                                if(cursos_hijos.indexOf(",")==-1){
                                                    //Guardar nombre del curso
                                                    array_cursos[i2] = cursos_hijos;
                                                    //Adaptar la condición para salir del while
                                                    cursos_hijos="";
                                                   //Toast.makeText(getApplicationContext(),"array_cursos["+i2+"] = "+array_cursos[i2],Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    //Recoger nombre del curso
                                                    String curso_aux = cursos_hijos.substring(0,cursos_hijos.indexOf(","));

                                                    //Toast.makeText(getApplicationContext(),"curso_aux = "+curso_aux,Toast.LENGTH_SHORT).show();

                                                    //Guardar nombre del curso
                                                    array_cursos[i2] = curso_aux;
                                                    //Adaptar el String para la siguiente iteración
                                                    cursos_hijos = cursos_hijos.substring(cursos_hijos.indexOf(",")+1,cursos_hijos.length());

                                                   // Toast.makeText(getApplicationContext(),"array_cursos["+i2+"] = "+array_cursos[i2],Toast.LENGTH_LONG).show();

                                                    //Avanzar posición en el array
                                                    i2++;
                                                }

                                            }//end while

                                            //Crear tablas, una para cada curso de cada hijo
                                            for(int i = 0;i<array_cursos.length;i++){
                                                //Crear consulta para la creación de la tabla
                                                String CREA_TABLA_CURSOS =
                                                        "CREATE TABLE 'curso-" + array_cursos[i]+ "!' (" +
                                                                "id INTEGER PRIMARY KEY," +
                                                                "autor TEXT," +
                                                                "fecha TEXT," +
                                                                "titulo TEXT," +
                                                                "mensaje TEXT," +
                                                                "leido INTEGER," +
                                                                "categoria TEXT)";
                                                try {
                                                    //Ejecutar consulta
                                                    db.execSQL(CREA_TABLA_CURSOS);
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + array_cursos[i], Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            //Crear tabla para avisos generales del centro

                                            String CREA_TABLA_GENERAL =
                                                    "CREATE TABLE general (" +
                                                            "id INTEGER PRIMARY KEY," +
                                                            "autor TEXT," +
                                                            "fecha TEXT," +
                                                            "titulo TEXT,"+
                                                            "mensaje TEXT," +
                                                            "leido INTEGER," +
                                                            "categoria TEXT)";


                                            try {
                                                db.execSQL(CREA_TABLA_GENERAL);
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo al crear la tabla general" , Toast.LENGTH_SHORT).show();
                                            }

                                            //Crear la tabla categorias
                                            String CREA_TABLA_CATEGORIAS =
                                                    "CREATE TABLE categorias (" +
                                                            "id INTEGER PRIMARY KEY," +
                                                            "nombre TEXT)";


                                            try {
                                                db.execSQL(CREA_TABLA_CATEGORIAS);
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo al crear la tabla general" , Toast.LENGTH_SHORT).show();
                                            }

                                            //Cerrar conexión con la BBDD
                                            db.close();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                            //Se crea un intento para abandonar esta activity y pasar a activity MenuPrincipal
                                            Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
                                            //Comenzar nueva actividad MenuPrincipal
                                            startActivity(intent);
                                            //Finalizar activity actual
                                            finish();

                                        }//end if
                                        //Si la inserción del token en la BBDD SQLite ha fallado
                                        else{
                                            Toast.makeText(getApplicationContext(), "Ha fallado la inserción del token en la BBDD SQLite y la creación de tablas", Toast.LENGTH_LONG).show();
                                        }

                                    }//end if
                                    //Si el servidor devuelve un error en el registro de los datos
                                    else{
                                        Toast.makeText(getApplicationContext(),"El registro en el portal web ha fallado.",Toast.LENGTH_LONG).show();
                                    }

                                }catch(JSONException e){

                                    e.printStackTrace();
                                }
                            }
                        },
                        //Si no se ha podido conectar con el servidor

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"No se ha podido conectar con el servidor. Respuesta = "+error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                ){

                    //Configurar los parámetros de la petición POST:

                    //Sobreescribir método getParams() de la clase StrinRequest
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {
                        //Crear mapa de valores
                        Map<String,String> params = new HashMap<>();
                        //Añadir token
                        params.put("token",SharedPrefManager.getInstance(getApplicationContext()).getToken());
                        //Añadir dni
                        params.put("dni",dni);
                        //Añadir password
                        params.put("password",password);

                        return params;
                    }
                };

                //Una vez configurada la petición, la añadimos a la queue

                RequestQueue requestQueue = Volley.newRequestQueue(this);

                requestQueue.add(stringRequest);

            }
            //Si la asignación de token en los servidores FCM de Google ha fallado
            else{
                Toast.makeText(this,"El Token no se ha generado.",Toast.LENGTH_LONG).show();
            }
        }//end of else, el que se ejecutaba si la validación del formulario era correcta (se habían insertado valores y se habían mandado al servidor)

    }//end of registraToken()


}//end of class
