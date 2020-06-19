package fmi1701681011.pu_todor_quad_equasion

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_history.*
import kotlin.math.abs

class HistoryActivity: AppCompatActivity() {
    private var x1: Float = 0f
    private var x2: Float = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> x1 = event.x
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX = x2 - x1

                val scale = resources.displayMetrics.density
                val distance = (100 * scale + 0.5f).toInt()

                if (abs(deltaX) > distance && deltaX > 0) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val db = CalculationsDB(this)

        val calculations = db.selectAll()
        val calculationsArray: Array<String> = Array<String>(calculations.count()) {""}

        if (calculations.count() == 0) {
            txvEmptyHistory.visibility = View.VISIBLE
        }

        // Generate message for each calculation.
        for ((index, calculation) in calculations.withIndex()) {
            var equasion: String = calculation.a.toString() + "x^2 "
            equasion = equasion + (if (calculation.b!! >= 0) "+ " else "- ") + abs(calculation.b!!) + "x "
            equasion = equasion + (if (calculation.c!! >= 0) "+ " else "- ") + abs(calculation.c!!) + " = 0"

            calculationsArray[index] = "$equasion\n${calculation.result}"
        }

        val arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, calculationsArray)
        lvHistory.adapter = arrayAdapter

        // Go to main activity.
        btnGoToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Delete history.
        btnDeleteHistory.setOnClickListener {
            db.deleteAll()
            lvHistory.adapter = null
            txvEmptyHistory.visibility = View.VISIBLE

            Toast.makeText(this, "History deleted.", Toast.LENGTH_SHORT).apply {
                val scale = resources.displayMetrics.density
                val dpAsPixels = (80 * scale + 0.5f).toInt()

                // Move the notification above the "Solve" button.
                setGravity(Gravity.BOTTOM, 0, dpAsPixels)
                show()
            }
        }
    }
}