package ru.sukhikh.appgetcoupon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class FavoritesDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static  String DATABASE_NAME="FavoritesCoupon";
    private static String TABLE_NAME= "FavoriteTable";
    private static String TABLE_NAME_NOTIFY  = "NotificationTable";
    public static String KEY_ID = "id";
    public static String NOTIFICATION ="nStatus";
    public static String ITEM_SHOP_TITLE = "itemShopTitle";
    public static String ITEM_CATEGORY_TITLE = "itemCategoryTitle";
    public static String ITEM_DATE = "itemDate";
    public static String FAVORITE_STATUS = "fStatus";
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + " TEXT PRIMARY KEY, " + ITEM_SHOP_TITLE + " TEXT,"
            + ITEM_DATE + " REAL," + ITEM_CATEGORY_TITLE + " TEXT,"
            + FAVORITE_STATUS+ " TEXT)";

    public FavoritesDB(Context context){
        super(context, DATABASE_NAME, null, DB_VERSION); }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //create empty table
    public void insertEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for(int i=0;i<11;i++)
        {
            cv.put(KEY_ID,i);
            cv.put(FAVORITE_STATUS, "0");

            db.insert(TABLE_NAME, null, cv);
        }
    }

    //insert data in table
    public void insertIntoTheDatabase(String item_shop_title, String item_category_title,
                                      Long item_date, String id, String fav_status) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ITEM_SHOP_TITLE, item_shop_title);
        cv.put(ITEM_CATEGORY_TITLE, item_category_title);
        cv.put(ITEM_DATE, item_date);
        cv.put(KEY_ID, id);
        cv.put(FAVORITE_STATUS, fav_status);
        db.insert(TABLE_NAME, null, cv);
        Log.d("FavDB Status", item_shop_title+" , favstatus - "+fav_status+" - . "+cv);
    }

    public Cursor read_all_data(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        String sql="select * from "+TABLE_NAME+" where "+KEY_ID+"="+id+"";
        return db.rawQuery(sql, null, null);
    }

    public void remove_fav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
       // String sql = "UPDATE " + TABLE_NAME + " SET  "+ FAVORITE_STATUS+" ='0' WHERE "+KEY_ID+"="+id+"";
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID+" ="+id+"";
        db.execSQL(sql);
        //db.delete(TABLE_NAME, null, null);
        Log.d("remove", id.toString());
    }

    public Cursor select_all_favorite_list() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + FAVORITE_STATUS + " ='1'" + " ORDER BY " + ITEM_DATE;
        return db.rawQuery(sql, null, null);
    }
    public boolean check_status(String Title){

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + " ='"+Title.hashCode()+"'";
        Cursor cursor = db.rawQuery(sql, null, null);
        boolean st=false;
        if(!cursor.isAfterLast()) {
            cursor.moveToFirst();
            String status = cursor.getString(cursor.getColumnIndex(FAVORITE_STATUS));
            st=status.equals("1");
        }
        return st;
    }

    public Cursor by_section(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ITEM_SHOP_TITLE;
        return  db.rawQuery(sql, null, null);
    }
    public Cursor by_section(String Category){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ITEM_CATEGORY_TITLE + " ='" + Category+"'";
        return  db.rawQuery(sql, null, null);
    }

    public ArrayList<String> getListShops(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> listShops = new ArrayList<>();
        String sql = "SELECT "+ITEM_SHOP_TITLE+" FROM " + TABLE_NAME;
        Cursor cursor =  db.rawQuery(sql, null, null);
        while(cursor.moveToNext())
            listShops.add(cursor.getString(cursor.getColumnIndex(FavoritesDB.ITEM_SHOP_TITLE)));
        return listShops;
    }

    public  Cursor update_category(String NewCategory, String OldCategory, String ShopName){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "UPDATE "+ TABLE_NAME + " SET " + ITEM_CATEGORY_TITLE + " ='" + NewCategory + "' WHERE "
                + ITEM_CATEGORY_TITLE + " ='" + OldCategory+"' AND " + ITEM_SHOP_TITLE + " ='" + ShopName + "'";
        return  db.rawQuery(sql, null, null);
    }
}
