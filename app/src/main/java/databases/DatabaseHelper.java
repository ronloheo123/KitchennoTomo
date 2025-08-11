//package databases;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final String DB_NAME="DatabaseRecipe.db";
//    private static final int DB_VERSION=1;
//
//    public DatabaseHelper(Context context){
//        super(context,DB_NAME,null,DB_VERSION);
//    }
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//            String createTable ="CREATE TABLE Food (" +
//                    "id INTERGER PRIMARY KEY AUTOINCREMENT, "+
//                    "Name Text not null)";
//            db.execSQL(createTable);
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            //db.execSQL("DROP TABLE IF EXISTS Food");
//            //onCreate(db);
//    }
//
//}
