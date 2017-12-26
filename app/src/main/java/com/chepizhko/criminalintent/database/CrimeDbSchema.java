package com.chepizhko.criminalintent.database;


public class CrimeDbSchema {
    //Класс CrimeTable существует только для определения строковых констант,
    // необходимых для описания основных частей определения таблицы.
    // Определение начинается с имени таблицы в базе данных CrimeTable.NAME,
    // за которым следуют описания столбцов.
    // При наличии такого определения вы сможете обращаться к столбцу с именем title в синтаксисе,
    // безопасном для кода Java: CrimeTable.Cols.TITLE.
    // Такой синтаксис существенно снижает риск изменения программы,
    // если вам когда-нибудь понадобится изменить имя столбца или добавить новые данные в таблицу.
    public static final class CrimeTable {

        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }

    }
}
