package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)
        supportActionBar?.title = "Lab02"
    }

    fun onClickBoardSize(v: View) {
        val tag = v.tag as String
        val tokens = tag.split(" ")

        val rows = tokens[0].toInt()
        val columns = tokens[1].toInt()

        val intent = Intent(this, Lab03Activity::class.java)
        intent.putExtra("rows", rows)
        intent.putExtra("columns", columns)
        startActivity(intent)
    }
}