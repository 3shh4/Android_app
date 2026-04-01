package pl.wsei.pam.lab01

import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class Lab01Activity : AppCompatActivity() {

    lateinit var mLayout: LinearLayout
    lateinit var mProgress: ProgressBar

    var mBoxes: MutableList<CheckBox> = mutableListOf()
    var mButtons: MutableList<Button> = mutableListOf()

    private val passed = BooleanArray(6) { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab01)

        supportActionBar?.title = "Lab01"

        mLayout = findViewById(R.id.main)

        for (i in 1..6) {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            row.orientation = LinearLayout.HORIZONTAL

            val checkBox = CheckBox(this).also {
                it.text = "Zadanie $i"
                it.isEnabled = false
            }

            val button = Button(this).also {
                it.text = "TESTUJ"
                it.setOnClickListener {
                    runTest(i)
                }
            }

            row.addView(checkBox)
            row.addView(button)

            mLayout.addView(row)

            mBoxes.add(checkBox)
            mButtons.add(button)
        }

        mProgress = ProgressBar(
            this,
            null,
            androidx.appcompat.R.attr.progressBarStyle,
            androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal
        )

        mProgress.max = 100
        mProgress.progress = 0

        mLayout.addView(mProgress)
    }

    private fun runTest(i: Int) {
        val ok = when (i) {
            1 -> (
                    task11(4, 6) in 0.666665..0.666667 &&
                            task11(7, -6) in -1.1666667..-1.1666665
                    )

            2 -> (
                    task12(7U, 6U) == "7 + 6 = 13" &&
                            task12(12U, 15U) == "12 + 15 = 27"
                    )

            3 -> (
                    task13(0.0, 5.4f) &&
                            !task13(7.0, 5.4f) &&
                            !task13(-6.0, -1.0f) &&
                            task13(6.0, 9.1f) &&
                            !task13(6.0, -1.0f) &&
                            task13(1.0, 1.1f)
                    )

            4 -> (
                    task14(-2, 5) == "-2 + 5 = 3" &&
                            task14(-2, -5) == "-2 - 5 = -7"
                    )

            5 -> (
                    task15("DOBRY") == 4 &&
                            task15("barDzo dobry") == 5 &&
                            task15("doStateczny") == 3 &&
                            task15("Dopuszczający") == 2 &&
                            task15("NIEDOSTATECZNY") == 1 &&
                            task15("XYZ") == -1
                    )

            6 -> (
                    task16(
                        mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                        mapOf("A" to 1U, "B" to 2U)
                    ) == 2U &&
                            task16(
                                mapOf("A" to 2U, "B" to 4U, "C" to 3U),
                                mapOf("F" to 1U, "G" to 2U)
                            ) == 0U &&
                            task16(
                                mapOf("A" to 23U, "B" to 47U, "C" to 30U),
                                mapOf("A" to 1U, "B" to 2U, "C" to 4U)
                            ) == 7U
                    )

            else -> false
        }

        if (ok) {
            mBoxes[i - 1].isChecked = true

            if (!passed[i - 1]) {
                passed[i - 1] = true
                mProgress.progress = (passed.count { it } * 100) / 6
            }
        }
    }

    private fun task11(a: Int, b: Int): Double {
        return a.toDouble() / b
    }

    private fun task12(a: UInt, b: UInt): String {
        return "$a + $b = ${a + b}"
    }

    fun task13(a: Double, b: Float): Boolean {
        return a >= 0 && a < b
    }

    fun task14(a: Int, b: Int): String {
        return if (b >= 0) {
            "$a + $b = ${a + b}"
        } else {
            "$a - ${abs(b)} = ${a + b}"
        }
    }

    fun task15(degree: String): Int {
        return when (degree.lowercase()) {
            "bardzo dobry" -> 5
            "dobry" -> 4
            "dostateczny" -> 3
            "dopuszczający" -> 2
            "niedostateczny" -> 1
            else -> -1
        }
    }

    fun task16(store: Map<String, UInt>, asset: Map<String, UInt>): UInt {
        var result: UInt? = null

        for ((key, need) in asset) {
            val have = store[key] ?: 0U
            val possible = have / need
            result = if (result == null) possible else minOf(result, possible)
        }

        return result ?: 0U
    }
}