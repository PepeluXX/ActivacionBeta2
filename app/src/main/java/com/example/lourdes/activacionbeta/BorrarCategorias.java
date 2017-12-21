package com.example.lourdes.activacionbeta;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BorrarCategorias extends AppCompatActivity {

    private final BDDHelper miHelper = new BDDHelper(this);
    //private Button borra ;
    private TextView no_categorias;
    int marka = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_categorias);


        //borra = (Button)findViewById(R.id.boton_borra);

        final LinearLayout lm = (LinearLayout) findViewById(R.id.linearLayout);

        // create the layout params that will be used to define how your
        // button will be displayed
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);



        //solicitamos titulos a la tabla que venga de la otra activity como 'filtro'

        SQLiteDatabase db = miHelper.getReadableDatabase();

        //Bundle datos = getIntent().getExtras();

        //final String nombre_tabla = datos.getString("nombre_tabla");

        String RAW_QUERY = "SELECT nombre FROM categorias";

        final Cursor cursor = db.rawQuery(RAW_QUERY,null);

        cursor.moveToFirst();

        if(cursor.getCount()==0){
            no_categorias = (TextView)findViewById(R.id.texto_no_cat);
            no_categorias.setText(R.string.no_categorias);
        }

        //Creamos elementos dinámicamente

        for(int j=0;j<cursor.getCount();j++) {
            // Create LinearLayout
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setBackgroundColor(12);

           /* GradientDrawable border = new GradientDrawable();
            border.setColor(ContextCompat.getColor(this, R.color.colorBordeLayoutListaMensajes)); //white background
            border.setStroke(2, ContextCompat.getColor(this, R.color.colorBordeSeparacion)); //black border with full opacity
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                ll.setBackgroundDrawable(border);
            } else {
                ll.setBackground(border);
            }*/

            final Button checkBox = new Button(this);

            checkBox.setText(cursor.getString(0));

            checkBox.setId(j);

            checkBox.setLayoutParams(params);

            Log.d("idsboton",""+checkBox.getId());

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"comenzar actividad",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ConfirmarBorradoCategoria.class);
                    intent.putExtra("nombre_categoria",checkBox.getText().toString());
                    startActivity(intent);
                    finish();

                }
            });


/*
            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = 25 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            float dp_boton = 200 / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

            checkBox.setTextSize(dp);
            checkBox.setGravity(0);
            checkBox.setHeight((int) dp_boton);


            checkBox.setLayoutParams(params);*/
            cursor.moveToNext();

            ll.addView(checkBox);
            lm.addView(ll);

        }

        db.close();
      /*  final Button borra = new Button(this);
        borra.setText("Borrar categorías seleccionadas");
        borra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 ArrayList<String>nombres_tablas = new ArrayList<>();
                 BDDHelper miHelper = new BDDHelper(getApplicationContext());
                 SQLiteDatabase db = miHelper.getReadableDatabase();
                 String query = "select nombre from categorias";
                 Cursor cursor = db.rawQuery(query,null);
                 cursor.moveToFirst();

                 for(int i = 0;i<cursor.getCount();i++){
                     nombres_tablas.add(cursor.getString(0));
                     cursor.moveToNext();
                 }

            }
        });
        //borra.setId;

        lm.addView(borra);*/
    }
}
