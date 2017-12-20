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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //private TextView textViewToken;
    private EditText editTextDNI,editTextPassword;
    private Button botonRegistro;
    private String marka;
    private static final String URL_REGISTRO_TOKEN= "http://185.196.254.88/ActivacionBeta/v1/TokenRegistration.php";

    String respuesta_servidor;

    final BDDHelper mDbHelper = new BDDHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textViewToken = (TextView)findViewById(R.id.textViewToken);
        editTextDNI = (EditText) findViewById(R.id.editTextDNI);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        botonRegistro = (Button)findViewById(R.id.botonRegistro);

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

                Toast.makeText(this,"La BD existe", Toast.LENGTH_LONG).show();

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



    //Función registraToken se encarga de conectar con el portal web y, si el usuario se encuentra previamente registrado en el portal,
    //se registra el token en la BBDD, si dicho registro se produce con éxito, entonces se guarda el token también en la BBDD de la aplicación, para
    //la posterior comprobación y para que así el formulario de inicio no vuelva a mostrarse en posteriores ejecuciones de la aplicación

    public void registraToken(){

        //se recogen los datos insertados por el usuario

        final String dni = editTextDNI.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //se comprueba que el usuario ha rellenado los campos

        if(TextUtils.isEmpty(dni) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"Por favor rellene los campos necesarios", Toast.LENGTH_LONG).show();


        }
        //se comprueba si el token fue generado y guardado
        else{
            if(SharedPrefManager.getInstance(this).getToken() != null){


                //se configura la petición POST que vamos a enviarle al portal web

                StringRequest stringRequest = new StringRequest(

                        Request.Method.POST,

                        URL_REGISTRO_TOKEN,

                        new Response.Listener<String>() {

                            //se configuran las acciones a realizar cuando se obtiene una respuesta desde el portal web

                            @Override
                            public void onResponse(String response) {

                                try{
                                    JSONObject obj = new JSONObject(response);

                                    respuesta_servidor = obj.getString("message");

                                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();

                                    //¿Se ha registrado correctamente el token en la BBDD del portal web?

                                    if(respuesta_servidor.equals("Token registrado ok")) {

                                        //comienza la inserción del token en la BBDD sqlite

                                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                                        //Se crea un nuevo map de valores (key,value), donde los nombres de las columnas de la tabla son las keys

                                        ContentValues values = new ContentValues();

                                        values.put(EstructuraBDD.COLUMNA_ID, "1");
                                        values.put(EstructuraBDD.COLUMNA_TOKEN, SharedPrefManager.getInstance(getApplicationContext()).getToken());

                                        // Se inserta la nueva fila y se devuelve el valor de la clave primaria (id) de la nueva fila insertada, en caso de error devolverá -1

                                        long newRowId = db.insert(EstructuraBDD.TABLE_NAME, null, values);

                                        if(newRowId != -1) {
                                            Toast.makeText(getApplicationContext(), "Se guardó el registro con clave: " +
                                                    newRowId, Toast.LENGTH_LONG).show();


                                            //textViewToken.setText(SharedPrefManager.getInstance(getApplicationContext()).getToken());


                                            //creamos las tablas necesarias, una para cada hijo

                                            String nombres_hijos = obj.getString("nombres_hijos");
                                            nombres_hijos = nombres_hijos.replace(" ","_"); //para nombres compuestos, ya que los nombres de las tablas no aceptan espacios en blanco

                                            Toast.makeText(getApplicationContext(), "nombres_hijos: " +
                                                    nombres_hijos, Toast.LENGTH_LONG).show();

                                            //String nombres_hijos = "Nerea,Antonio";

                                            //int count = nombres_hijos.length() - nombres_hijos.replace(",", ",").length();
                                            int count = 0;
                                            String aux = "";

                                            for (int i=0; i < nombres_hijos.length(); i++) {

                                                aux = nombres_hijos.substring(i, i + 1);

                                                if (aux.equals(",")) {
                                                    count++;
                                                }

                                            }
                                            count+=1;

                                            Toast.makeText(getApplicationContext(),"Count ="+count,Toast.LENGTH_SHORT).show();
                                            String [] array_nombres = new String[count];
                                            int i1 =0;

                                            while(!nombres_hijos.equals("")){

                                                Toast.makeText(getApplicationContext(),"dentro del while",Toast.LENGTH_LONG).show();

                                                // true si sólo un hijo

                                                if(nombres_hijos.indexOf(",")==-1){
                                                    array_nombres[i1] = nombres_hijos;
                                                    nombres_hijos="";
                                                    Toast.makeText(getApplicationContext(),"array_nombres["+i1+"] = "+array_nombres[i1],Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    String nombre_aux = nombres_hijos.substring(0,nombres_hijos.indexOf(","));

                                                    Toast.makeText(getApplicationContext(),"nombre_aux = "+nombre_aux,Toast.LENGTH_LONG).show();

                                                    array_nombres[i1] = nombre_aux;

                                                    nombres_hijos = nombres_hijos.substring(nombres_hijos.indexOf(",")+1,nombres_hijos.length());

                                                    Toast.makeText(getApplicationContext(),"array_nombres["+i1+"] = "+array_nombres[i1],Toast.LENGTH_LONG).show();

                                                    i1++;
                                                }
                                            }
                                            for(int i = 0;i<array_nombres.length;i++){

                                                String CREA_TABLA_HIJO =
                                                        "CREATE TABLE 'hijo-" + array_nombres[i]+ "!' (" +
                                                                 "id INTEGER PRIMARY KEY," +
                                                                 "autor TEXT," +
                                                                 "fecha TEXT," +
                                                                 "titulo TEXT," +
                                                                 "mensaje TEXT," +
                                                                 "leido INTEGER," +
                                                                 "categoria TEXT)";


                                                try {
                                                    db.execSQL(CREA_TABLA_HIJO);

                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + array_nombres[i], Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            //creamos tablas para los mensajes destinados a un curso completo

                                            String cursos_hijos = obj.getString("cursos_hijos");
                                            cursos_hijos = cursos_hijos.replace(" ","_"); // para evitar espacios en blanco en el nombre de la tabla

                                            int count2 = 0;

                                            String aux2 = "";


                                            for (int  i=0; i < cursos_hijos.length(); i++) {

                                                aux2 = cursos_hijos.substring(i, i + 1);

                                                if (aux2.equals(",")) {
                                                    count2++;
                                                }
                                            }
                                            count2+=1;

                                            String [] array_cursos = new String[count2];
                                            int i2 =0;

                                            while(!cursos_hijos.equals("")){

                                                //true si sólo un  curso

                                                if(cursos_hijos.indexOf(",")==-1){
                                                    array_cursos[i2] = cursos_hijos;
                                                    cursos_hijos="";
                                                    Toast.makeText(getApplicationContext(),"array_cursos["+i2+"] = "+array_cursos[i2],Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String curso_aux = cursos_hijos.substring(0,cursos_hijos.indexOf(","));

                                                    Toast.makeText(getApplicationContext(),"curso_aux = "+curso_aux,Toast.LENGTH_SHORT).show();

                                                    array_cursos[i2] = curso_aux;

                                                    cursos_hijos = cursos_hijos.substring(cursos_hijos.indexOf(",")+1,cursos_hijos.length());

                                                    Toast.makeText(getApplicationContext(),"array_cursos["+i2+"] = "+array_cursos[i2],Toast.LENGTH_LONG).show();

                                                    i2++;
                                                }
                                            }//end while

                                            for(int i = 0;i<array_cursos.length;i++){

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
                                                    db.execSQL(CREA_TABLA_CURSOS);

                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Fallo al crear la tabla " + array_cursos[i], Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            //creamos tabla para avisos generales del centro

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

                                            //creamos la tabla categorias
                                            String CREA_TABLA_CATEGORIAS =
                                                    "CREATE TABLE categorias (" +
                                                            "id INTEGER PRIMARY KEY," +
                                                            "nombre TEXT)";


                                            try {
                                                db.execSQL(CREA_TABLA_CATEGORIAS);

                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Fallo al crear la tabla general" , Toast.LENGTH_SHORT).show();
                                            }

                                            db.close();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                            Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);

                                            startActivity(intent);

                                            finish();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Ha fallado la inserción del token en la BBDD SQLite y la creación de tablas", Toast.LENGTH_LONG).show();
                                        }

                                    }else{Toast.makeText(getApplicationContext(),"El registro del token en el portal web ha fallado",Toast.LENGTH_LONG).show();}

                                }catch(JSONException e){

                                    e.printStackTrace();
                                }
                            }
                        },
                        //recogemos cualquier fallo que ocurra al realizar la petición POST al portal web

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getMessage()+"mensajote",Toast.LENGTH_LONG).show();
                            }
                        }
                ){

                    //se configuran los parámetros de la petición POST

                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {

                        Map<String,String> params = new HashMap<>();

                        params.put("token",SharedPrefManager.getInstance(getApplicationContext()).getToken());
                        params.put("dni",dni);
                        params.put("password",password);

                        return params;
                    }
                };

                //una vez configurados la petición, la añadimos a la queue

                RequestQueue requestQueue = Volley.newRequestQueue(this);

                requestQueue.add(stringRequest);

            }else{
                Toast.makeText(this,"Token not generated",Toast.LENGTH_LONG).show();
            }
        }

    }

    //sobre escritura del método onClick()

    @Override
    public void onClick(View view) {
        if(view == botonRegistro){
            registraToken();
        }
    }
}
