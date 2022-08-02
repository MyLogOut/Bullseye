package com.sirstudios.bullseye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sirstudios.bullseye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        with(mainBinding.hitMeButton) {
            this.setOnClickListener {
                this.width = (this.width * 1.2).toInt()
                this.height = (this.height * 1.2).toInt()
            }
        }
    }
}