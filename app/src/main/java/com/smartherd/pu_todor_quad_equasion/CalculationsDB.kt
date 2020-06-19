package com.smartherd.pu_todor_quad_equasion

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CalculationsDB(context: Context) : SQLiteOpenHelper(context, "calculations.db", null, 1) {

    var context : Context? = null

    init {
        this.context = context
    }

    companion object {
        const val CALCULATION = "calculation"
        const val DB_VERSION = 1

        const val CREATE_TABLE_CALCULATION = "CREATE TABLE calculation(id INTEGER PRIMARY KEY, a INTEGER, b INTEGER, c INTEGER, result VARCHAR)"
        const val SELECT_CALCULATION = "SELECT * FROM calculation ORDER BY id DESC"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL(CREATE_TABLE_CALCULATION)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    // Add a calculation record.
    fun insertCalculation(a: Int, b: Int, c: Int, result: String): Boolean {
        val contentValues = ContentValues()
        contentValues.put("a", a)
        contentValues.put("b", b)
        contentValues.put("c", c)
        contentValues.put("result", result)

        val rowId = writableDatabase.insert(CALCULATION, null, contentValues)
        return rowId > 0
    }

    // Delete history.
    fun deleteAll() {
        writableDatabase.delete(CALCULATION, null, null)
    }

    // Get all calculations.
    fun selectAll() : ArrayList<CalculationModel> {
        val calculations: ArrayList<CalculationModel> = ArrayList()
        val cursor = readableDatabase.rawQuery(SELECT_CALCULATION, null, null)

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val calculation = CalculationModel()

                calculation.id = cursor.getInt(cursor.getColumnIndex("id"))
                calculation.a = cursor.getInt(cursor.getColumnIndex("a"))
                calculation.b = cursor.getInt(cursor.getColumnIndex("b"))
                calculation.c = cursor.getInt(cursor.getColumnIndex("c"))
                calculation.result = cursor.getString(cursor.getColumnIndex("result"))

                calculations.add(calculation)
                cursor.moveToNext()
            }
        }

        cursor.close()
        return calculations
    }
}
