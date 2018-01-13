package com.example.lourdes.activacionbeta;


/*
* Clase que se encarga de definir la estructura de la tabla que almacenará el token.
*
* @author  Jose Luis
* @version 1.0
* @since 07/12/2017
*/


public class EstructuraBDD {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private EstructuraBDD() {}


        //Nombre de la tabla
        public static final String TABLE_NAME = "tokens";
        //Nombre de las columnas de las tablas
        public static final String COLUMNA_ID = "id";
        public static final String COLUMNA_TOKEN = "token";

        //Crear consulta de creación de tabla
        public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EstructuraBDD.TABLE_NAME + " (" +
                    EstructuraBDD.COLUMNA_ID + " INTEGER PRIMARY KEY," +
                    EstructuraBDD.COLUMNA_TOKEN + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

}//end of class
