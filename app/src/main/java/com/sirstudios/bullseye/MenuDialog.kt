package com.sirstudios.bullseye

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.sirstudios.bullseye.databinding.DialogMenuBinding
import com.sirstudios.bullseye.extensions.onSelected
import kotlin.system.exitProcess

class MenuDialog : AppCompatActivity() {
    lateinit var dialogMenu: DialogMenuBinding
    lateinit var gameData: GameData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogMenu = DialogMenuBinding.inflate(layoutInflater)
        gameData = intent.getParcelableExtra(MainActivity.INTENT_GAMEDATA_KEY) ?: GameData(this)

        thisActivity@title = getString(R.string.options_title_text)

        with(dialogMenu) {
            setContentView(root)
            roundScorePositionSpinner.adapter =
                ArrayAdapter.createFromResource(
                    root.context,
                    R.array.round_score_position_array,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item
                ).apply {
                    setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
                }
            roundScorePositionSpinner.onSelected { position, _ ->
                gameData.isScoreboardDisplaySetAsTop = position == 0
            }
            val selection = if (gameData.isScoreboardDisplaySetAsTop) 0 else 1
            roundScorePositionSpinner.setSelection(selection)
            /*.also {
                roundScorePositionSpinner.adapter = it
                AdapterView
                    .inflate(dialogBuilder.context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dialogBinding.root)
                    .also {
                    }
            }*/

            /*These two buttons (close/continue) do the same, they shouldn't coexist. Potential rework?*/
            continueGameButton.setOnClickListener { intentReply() }
            restartGameButton.setOnClickListener { intentReply(shouldRestart = true) }

            exitGameButton.setOnClickListener { intentReply(shouldExit = true) }
        }
    }

    /**
     * Using and overriding onOptionsItemSelected instead of onBackPressed() to preserve the proper resultCode
     * override fun onBackPressed() {
            intentReply()
        }
    **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> { intentReply(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun intentReply(shouldRestart: Boolean = false, shouldExit: Boolean = false) {
        val bundle = Bundle()

        bundle.putParcelable(MainActivity.INTENT_GAMEDATA_KEY, gameData)
        bundle.putBoolean("shouldRestart", shouldRestart)
        bundle.putBoolean("shouldExit", shouldExit)

        setResult(Activity.RESULT_OK, Intent().putExtras(bundle))
        finish()
    }
}