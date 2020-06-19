package fmi1701681011.pu_todor_quad_equasion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

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

                if (abs(deltaX) > distance && deltaX < 0) {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = CalculationsDB(this)



        // Go to history activity.
        btnGoToHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Solve equasion logic.
        btnSolve.setOnClickListener {
            // Throw a Toast error if one or more of the number fields are empty.
            if (etEqA.text.toString() == "" || etEqB.text.toString() == "" || etEqC.text.toString() == "") {
                Toast.makeText(this, "\"A\", \"B\" and \"C\" should not be empty.", Toast.LENGTH_SHORT).apply {
                    val scale = resources.displayMetrics.density
                    val dpAsPixels = (80 * scale + 0.5f).toInt()

                    // Move the notification above the "Solve" button.
                    setGravity(Gravity.BOTTOM, 0, dpAsPixels)
                    show()
                }
            }
            else if (etEqA.text.toString() == "0") {
                Toast.makeText(this, "\"A\" must not be equal to zero.", Toast.LENGTH_SHORT).apply {
                    val scale = resources.displayMetrics.density
                    val dpAsPixels = (80 * scale + 0.5f).toInt()

                    // Move the notification above the "Solve" button.
                    setGravity(Gravity.BOTTOM, 0, dpAsPixels)
                    show()
                }
            }
            else {
                val a: Double = ((if (swMinusA.isChecked) "-" else "") + etEqA.text).toDouble()
                val b: Double = ((if (swMinusB.isChecked) "-" else "") + etEqB.text).toDouble()
                val c: Double = ((if (swMinusC.isChecked) "-" else "") + etEqC.text).toDouble()

                val root1: Double
                val root2: Double
                val result: String

                val determinant: Double = b * b - 4.0 * a * c

                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.HALF_UP

                // Two roots.
                if (determinant > 0) {
                    root1 = (-b + sqrt(determinant)) / (2 * a)
                    root2 = (-b - sqrt(determinant)) / (2 * a)

                    result = "X1 = ${df.format(root1)}; X2 = ${df.format(root2)}"
                }
                // One root.
                else if (determinant == 0.0) {
                    root1 = -b / (2 * a)
                    result = "X1/X2 = ${df.format(root1)}"
                }
                // No real roots.
                else {
                    result = "There are no real roots."
                }

                txvAnswer.text = result;
                db.insertCalculation(a.toInt(), b.toInt(), c.toInt(), result)
            }
        }

        /* Start functionality for input A. */
        fun changeA (s: String) {
            val sign = if (swMinusA.isChecked && s != "") "-" else ""
            val result = sign + (if (s != "") s else "A") + "x^2"
            val fullEq = txvEquasion.text.replace("^((-?\\d+)|(A))x\\^2".toRegex()) { var needle = it.value; result }
            txvEquasion.text = fullEq
        }

        etEqA.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeA(s.toString())
            }
        })

        swMinusA.setOnClickListener {
            changeA(etEqA.text.toString())
        }
        /* End functionality for input A. */

        /* Start functionality for input B. */
        fun changeB (s: String) {
            val sign = if (swMinusB.isChecked && s != "") " - " else " + "
            val result = "x^2" + sign + (if (s != "") s else "B") + "x"
            val fullEq = txvEquasion.text.replace("x\\^2(\\s[-+]?\\s((\\d+)|(B)))x".toRegex()) { var needle = it.value; result }
            txvEquasion.text = fullEq
        }

        etEqB.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeB(s.toString())
            }
        })

        swMinusB.setOnClickListener {
            changeB(etEqB.text.toString())
        }
        /* End functionality for input B. */

        /* Start functionality for input C. */
        fun changeC (s: String) {
            val sign = if (swMinusC.isChecked && s != "") " - " else " + "
            val result = "x" + sign + (if (s != "") s else "C")
            val fullEq = txvEquasion.text.replace("x\\s[-+]\\s((\\d+)|(C))".toRegex()) { var needle = it.value; result }
            txvEquasion.text = fullEq
        }

        etEqC.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                changeC(s.toString())
            }
        })

        swMinusC.setOnClickListener {
            changeC(etEqC.text.toString())
        }
        /* End functionality for input C. */
    }
}