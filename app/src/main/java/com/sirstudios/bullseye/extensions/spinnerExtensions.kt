package com.sirstudios.bullseye.extensions

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

fun AppCompatSpinner.onSelected(onNothingSelected: (parent: AdapterView<*>?) -> Unit = {}, action: (position: Int, id: Long) -> Unit = {position, id ->}) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            action(position, id)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            onNothingSelected(parent)
        }
    }
}

fun AppCompatSpinner.selected(): Int = this.selectedItem.toString().toInt()