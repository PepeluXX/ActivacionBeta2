package com.example.lourdes.activacionbeta;

/**
 * Created by Lourdes on 07/12/2017.
 */

public class EstructuraBDD {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private EstructuraBDD() {}

    /* Inner class that defines the table contents */

        public static final String TABLE_NAME = "tokens";
        public static final String COLUMNA_ID = "id";
        public static final String COLUMNA_TOKEN = "token";


        public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EstructuraBDD.TABLE_NAME + " (" +
                    EstructuraBDD.COLUMNA_ID + " INTEGER PRIMARY KEY," +
                    EstructuraBDD.COLUMNA_TOKEN + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

}
