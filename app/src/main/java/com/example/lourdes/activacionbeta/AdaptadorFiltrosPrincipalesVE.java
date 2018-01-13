package com.example.lourdes.activacionbeta;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/*
* Clase en la que se crea el menú principal de la aplicación. Muestra un menú con filas
* desplegables. Al pulsar sobre los hijos desplegados se accede o bien a listas de mensajes o directamente
* al mensaje seleccionado
*
* @author  Jose Luis
* @version 1.0
* @since   12/01/2018
*/

public class AdaptadorFiltrosPrincipalesVE  extends BaseExpandableListAdapter{

    private Context context;
    private HashMap<String,List<String>> filtros_principales;
    private List<String>subfiltros;

    public AdaptadorFiltrosPrincipalesVE(Context context, HashMap<String,List<String>>filtros_principales,List<String>subfiltros){
        this.context = context;
        this.filtros_principales = filtros_principales;
        this.subfiltros = subfiltros;
    }

    //devuelve la cantidad de subfiltros
    @Override
    public int getGroupCount() {

        return subfiltros.size();

    }

    //Devuelve número de subfiltros contenidos en cada lista
    @Override
    public int getChildrenCount(int i) {

        return filtros_principales.get(subfiltros.get(i)).size();

    }

    @Override
    public Object getGroup(int i) {

        return subfiltros.get(i);

    }

    @Override
    public Object getChild(int parent, int child) {

        return filtros_principales.get(subfiltros.get(parent)).get(child);

    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int parent, int child) {
        return child;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parent, boolean isExpanded, View convertView, ViewGroup parentView) {

        String group_title = (String)getGroup(parent);

        if(convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout,parentView,false);
        }

        TextView parent_textview = (TextView) convertView.findViewById(R.id.parent_txt);
        parent_textview.setTypeface(null, Typeface.BOLD);
        parent_textview.setText(group_title);

        return convertView;

    }

    @Override
    public View getChildView(int parent, int child, boolean lastChild, View convertView, ViewGroup parentView) {

        String child_title = (String)getChild(parent,child);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout,parentView,false);
        }

        TextView child_textview = convertView.findViewById(R.id.child_txt);
        child_textview.setText(child_title);

        return convertView;

    }
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}






















