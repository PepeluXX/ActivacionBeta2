package com.example.lourdes.activacionbeta;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuardarEnCategoria extends Activity {

    private final BDDHelper miHelper = new BDDHelper(this);

    TextView texto_confirma_guardado;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar_en_categoria);



        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


        final Bundle datos = getIntent().getExtras();

        SQLiteDatabase db = miHelper.getReadableDatabase();

        String RAW_QUERY = "SELECT nombre FROM categorias";

         cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

       // Log.d("MICURSOR-ID-TABLA",cursor.getString(0)+" "+datos.getString("id")+" "+datos.getString("nombre_tabla"));

        if(cursor.getCount()!=0) {

            for (int j = 0; j < cursor.getCount(); j++) {

                // Create LinearLayout
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setBackgroundColor(12);

                final Button checkBox = new Button(getApplicationContext());

                checkBox.setText(cursor.getString(0));

                checkBox.setId(j);

                checkBox.setLayoutParams(params);

                final String valor = cursor.getString(0);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),"Me caigo?",Toast.LENGTH_SHORT).show();

                        SQLiteDatabase db = miHelper.getWritableDatabase();
                        // New value for one column
                        ContentValues values = new ContentValues();
                        values.put("categoria",valor );

                        // Which row to update, based on the title
                        String selection = "id LIKE ?";
                        String[] selectionArgs = {datos.getString("id") };

                        int count = db.update(
                                datos.getString("nombre_tabla"),
                                values,
                                selection,
                                selectionArgs);

                        if(count !=0){
                            Toast.makeText(getApplicationContext(),"Se guardó el mensaje como perteneciente a la categoría "+valor,Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });

                cursor.moveToNext();
                ll.addView(checkBox);
                lm.addView(ll);
            }

        }
        else{
           texto_confirma_guardado = (TextView)findViewById(R.id.texto_confirma_guardado);
            texto_confirma_guardado.setText("No hay categorías creadas.");
          /* Toast.makeText(getApplicationContext(),"Aún no se ha creado ninguna categoría",Toast.LENGTH_SHORT).show();
           finish();*/
        }

    }
}
