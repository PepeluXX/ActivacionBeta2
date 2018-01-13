package com.example.lourdes.activacionbeta;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/*
* Clase que se encarga de crear un fragmento para insertar un menú en el. NO SE ESTÁ USANDO.
*
* @author  Jose Luis
* @version 1.0
*/

public class FragmentMenuMensajes extends Fragment {


    public FragmentMenuMensajes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_menu_mensajes, container, false);
    }

}//end of class
