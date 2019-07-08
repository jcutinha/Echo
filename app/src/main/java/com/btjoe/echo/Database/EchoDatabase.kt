package com.btjoe.echo.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.btjoe.echo.Songs

class EchoDatabase : SQLiteOpenHelper{

    var songList = ArrayList<Songs>()

    object Staticated{
        val DB_NAME = "FavouriteDatabase"
        var DB_VERSION = 1
        val TABLE_NAME = "FavouriteTable"
        val COL_ID = "TrackId"
        val COL_TRACK_NAME = "TrackName"
        val COL_ARTIST_NAME = "ArtistName"
        val COL_TRACK_DATA = "TrackData"
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE " + Staticated.TABLE_NAME + " ( " + Staticated.COL_ID + " INTEGER, " + Staticated.COL_TRACK_NAME + " STRING, "
                + Staticated.COL_ARTIST_NAME + " STRING, " + Staticated.COL_TRACK_DATA + " STRING);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun storeAsFavourite(id : Int?, trackName : String?, artistName : String?, data : String?){
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(Staticated.COL_ID, id)
        cv.put(Staticated.COL_TRACK_NAME, trackName)
        cv.put(Staticated.COL_ARTIST_NAME, artistName)
        cv.put(Staticated.COL_TRACK_DATA, data)

        db.insert(Staticated.TABLE_NAME, null, cv)
        db.close()
    }

    fun queryDBList() : ArrayList<Songs>?{
        try {
            val db = this.readableDatabase
            var query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cusor = db.rawQuery(query_params, null)
            if (cusor.moveToFirst()){
                do {
                    var id = cusor.getInt(cusor.getColumnIndexOrThrow(Staticated.COL_ID))
                    var trackName = cusor.getString(cusor.getColumnIndexOrThrow(Staticated.COL_TRACK_NAME))
                    var artistName = cusor.getString(cusor.getColumnIndexOrThrow(Staticated.COL_ARTIST_NAME))
                    var data = cusor.getString(cusor.getColumnIndexOrThrow(Staticated.COL_TRACK_DATA))
                    songList.add(Songs(id.toLong(), trackName, artistName, data, 0))

                }while (cusor.moveToNext())
            }else {
                return null
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return songList

    }

    fun checkIfIDExists(id : Int): Boolean{
        var storeId = -1090
        val db = this.readableDatabase

        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongID = '$id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COL_ID))
            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
    }

    fun deleteFavourite(id: Int) {
        val db = this.writableDatabase

        db.delete(Staticated.TABLE_NAME, Staticated.COL_ID + " = " + id, null)
        db.close()
    }

    fun checkSize() : Int{
        var counter = 0
        var db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                counter = counter
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }

}