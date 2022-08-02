package com.sirstudios.bullseye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.sirstudios.bullseye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.hitMeButton.setOnClickListener {
            Log.i("Button click event", "You clicked the \"Hit Me!\" button")
            displayResult()
        }
    }

    private fun displayResult() {
        val dialogTitle: String
            = getString(R.string.result_dialog_title)
        val dialogMessage: String
            = getString(R.string.result_dialog_message)
        val builder: AlertDialog.Builder
            =   AlertDialog.Builder(this)

        builder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton(R.string.result_dialog_button) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}