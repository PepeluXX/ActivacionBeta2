package com.example.lourdes.activacionbeta;

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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewToken;
    private EditText editTextDNI,editTextPassword;
    private Button botonRegistro;
    private static final String URL_REGISTRO_TOKEN= "http://185.196.254.88/ActivacionBeta/v1/TokenRegistration.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewToken = (TextView)findViewById(R.id.textViewToken);

        editTextDNI = (EditText) findViewById(R.id.editTextDNI);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        botonRegistro = (Button)findViewById(R.id.botonRegistro);

        botonRegistro.setOnClickListener(this);

        textViewToken.setText(SharedPrefManager.getInstance(this).getToken());

    }


    public void registraToken(){

        final String dni = editTextDNI.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //se comprueba que el usuario ha rellenado los campos

        if(TextUtils.isEmpty(dni) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"Por favor rellene los campos necesarios", Toast.LENGTH_LONG).show();


        }//se comprueba si el token fue generado y guardado
        else{
            if(SharedPrefManager.getInstance(this).getToken() != null){


                StringRequest stringRequest = new StringRequest(

                        Request.Method.POST,

                        URL_REGISTRO_TOKEN,

                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try{
                                    JSONObject obj = new JSONObject(response);

                                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();

                                }catch(JSONException e){

                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError {

                        Map<String,String> params = new HashMap<>();

                        params.put("token",SharedPrefManager.getInstance(getApplicationContext()).getToken());
                        params.put("dni",dni);
                        params.put("password",password);

                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);

                requestQueue.add(stringRequest);

            }else{
                Toast.makeText(this,"Token not generated",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onClick(View view) {
        if(view == botonRegistro){
            registraToken();
        }
    }
}
