package com3001.jb01026.finalyearproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com3001.jb01026.finalyearproject.model.CareFrequency;
import com3001.jb01026.finalyearproject.model.Expertise;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlantType;
import com3001.jb01026.finalyearproject.model.PlotSize;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Plant.db";
    public static final String TABLE_NAME = "Plants";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Type";
    public static final String COL_4 = "Image";
    public static final String COL_5 = "Description";
    public static final String COL_6 = "PlotSize";
    public static final String COL_7 = "CareFreq";
    public static final String COL_8 = "Expertise";
    public static final String COL_9 = "MonthByMonth";

    private static final int EXPECTED_COLUMNS  = 8;

    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        if(context == null) {
            Log.d("TEST", "CONTEXT IS NULL");
        }

        //TODO: This probably shouldn't be here...
        InputStream stream = context.getResources().openRawResource(R.raw.file);
        ImportData(getReadableDatabase(), stream);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT, " +
                COL_6 + " INTEGER, " +
                COL_7 + " INTEGER, " +
                COL_8 + " INTEGER, " +
                COL_9 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query;
        query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);

    }

    public void ImportData(SQLiteDatabase db, InputStream stream) {

        String query;
        query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);

        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        String line ="";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                columns[3]=columns[3].substring(1, columns[3].length()-1);

                ContentValues cv = new ContentValues(8);
                cv.put(COL_2, columns[0].trim());
                cv.put(COL_3, columns[1].trim());
                cv.put(COL_4, columns[2].trim());
                cv.put(COL_5, columns[3].trim());
                cv.put(COL_6, columns[4].trim());
                cv.put(COL_7, columns[5].trim());
                cv.put(COL_8, columns[6].trim());
                cv.put(COL_9, columns[7].trim());

                db.insert(TABLE_NAME, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public ArrayList<Plant> getPlantList(SQLiteDatabase db) {

        ArrayList<Plant> plantList = new ArrayList<Plant>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM Plants", null);

            if(c!=null) {
                if(c.moveToFirst()) {
                    do {
                        String name = c.getString(c.getColumnIndex("Name"));
                        String type = c.getString(c.getColumnIndex("Type"));
                        String image = c.getString(c.getColumnIndex("Image"));
                        String description = c.getString(c.getColumnIndex("Description"));
                        int plotSize = c.getInt(c.getColumnIndex("PlotSize"));
                        int careFrequency = c.getInt(c.getColumnIndex("CareFreq"));
                        int expertise = c.getInt(c.getColumnIndex("Expertise"));
                        String monthByMonth = c.getString(c.getColumnIndex("MonthByMonth"));


                        PlantType pType = null;
                        switch (type) {
                            case "Vegetable":
                                pType = PlantType.VEGETABLE;
                                break;
                            case "Fruit":
                                pType = PlantType.FRUIT;
                                break;
                            case "Herb":
                                pType = PlantType.HERB;
                                break;

                        }

                        PlotSize pPlotSize = null;
                        switch (plotSize) {
                            case 1:
                                pPlotSize = PlotSize.SMALL;
                                break;
                            case 2:
                                pPlotSize = PlotSize.MEDIUM;
                                break;
                            case 3:
                                pPlotSize = PlotSize.LARGE;
                                break;
                        }

                        CareFrequency pCareFreq = null;
                        switch (careFrequency) {
                            case 1:
                                pCareFreq = CareFrequency.OCCASIONALLY;
                                break;
                            case 2:
                                pCareFreq = CareFrequency.MONTHLY;
                                break;
                            case 3:
                                pCareFreq = CareFrequency.FORTNIGHTLY;
                                break;
                            case 4:
                                pCareFreq = CareFrequency.WEEKLY;
                                break;
                            case 5:
                                pCareFreq = CareFrequency.DAILY;
                                break;
                        }

                        Expertise pExpertise = null;
                        switch (expertise) {
                            case 1:
                                pExpertise = Expertise.BEGINNER;
                                break;
                            case 2:
                                pExpertise = Expertise.EASY;
                                break;
                            case 3:
                                pExpertise = Expertise.MEDIUM;
                                break;
                            case 4:
                                pExpertise = Expertise.ADVANCED;
                                break;
                            case 5:
                                pExpertise = Expertise.EXPERT;
                                break;
                        }

                        Plant p = new Plant(name, pType, image, description, pPlotSize, pCareFreq, pExpertise, monthByMonth);

                        plantList.add(p);
                    } while(c.moveToNext());
                }
            }

        } catch (SQLException se) {

        } finally {
            if(db!=null) {
                db.execSQL("DELETE FROM Plants");
                db.close();
            }
            return plantList;
        }

    }



}
