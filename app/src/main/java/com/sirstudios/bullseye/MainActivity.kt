package com.sirstudios.bullseye

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.allViews
import com.sirstudios.bullseye.databinding.ActivityMainBinding
import com.sirstudios.bullseye.databinding.DialogMenuBinding
import com.sirstudios.bullseye.extensions.onSelected
import kotlin.math.floor
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private val gameDataManager = GameDataManager(this)
    lateinit var gameData: GameData

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var dialogBinding: DialogMenuBinding

    companion object {
        const val INTENT_GAMEDATA_KEY = "gameData"
        const val GAMEDATA_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //supportActionBar?.hide()
        installSplashScreen()

        gameData = gameDataManager.readData()
        Log.d("gameDataStatus", gameData.status())

        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        dialogBinding = DialogMenuBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        updateScoreboard(isTopScoreViewVisible = gameData.isScoreboardDisplaySetAsTop)

        with(mainBinding) {
            restartGame(this@with)

            hitMeButton.setOnClickListener {
                Log.i("Button click event", "You clicked the \"Hit Me!\" button")
                val pointsGotten = getPoints()
                gameData.totalScore += pointsGotten
                gameData.currentRound += 1
                displayResult(gameData.currentTarget(), gameData.sliderValue, pointsGotten, gameData.totalScore)
            }

            seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        gameData.sliderValue = progress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // TODO("Not yet implemented")
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        // TODO("Not yet implemented")
                    }

                }
            )

            startOverButton?.setOnClickListener {
                /*Log.d("gameDataStatus", gameData.status())*/restartGame(this@with)
            }

            optionsButton?.setOnClickListener {
                val goToOptionsMenu = Intent(this@MainActivity, MenuDialog::class.java)
                    .putExtra(INTENT_GAMEDATA_KEY, gameData)
                startActivityForResult(goToOptionsMenu, GAMEDATA_REQUEST_CODE)
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            GAMEDATA_REQUEST_CODE -> data?.let {
                // Log.d("passedDataStatus", data?.getParcelableExtra<GameData>(INTENT_GAMEDATA_KEY)?.status() ?: "Nothing Passed!!")
                val newData = it.getParcelableExtra<GameData>(INTENT_GAMEDATA_KEY)
                newData?.let { passedData ->
                    gameData = passedData
                    if (!gameData.isScoreboardDisplaySetAsTop) updateScoreboard(1, false)
                    else updateScoreboard(0, true)
                    gameDataManager.saveData(gameData)
                }
                val shouldRestart = it.getBooleanExtra("shouldRestart", false)
                val shouldExit = it.getBooleanExtra("shouldExit", false)
                // Log.d("shouldRestart", "$shouldRestart- $shouldExit")
                if (shouldRestart) restartGame(mainBinding)
                if (shouldExit) {
                    tryToSaveGame()
                    finish()
                    exitProcess (0)
                }
            }
        }
        //Log.d("passedDataStatus", data?.getParcelableExtra<GameData>(INTENT_GAMEDATA_KEY)?.status() ?: "ELSE -> \nREQUEST_CODE: $requestCode\nRESULT_CODE: $resultCode")
        //        Toast.makeText(this, "onActivityResult()!!", Toast.LENGTH_LONG).show()
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Method being overridden to avoid loosing the data coming from the Intent as replied Bundle.
    override fun onBackPressed() {
        this.moveTaskToBack(true)
    }
    override fun onResume() {
        //Toast.makeText(this, "onResume()!!", Toast.LENGTH_SHORT).show()
        tryToSaveGame(notify = true)
        super.onResume()
    }

    override fun onPause() {
        //Toast.makeText(this, "onPause()!!", Toast.LENGTH_SHORT).show()
        tryToSaveGame(notify = true, true)
        super.onPause()
    }

    override fun onRestart() {
        //Toast.makeText(this, "onRestart()!!", Toast.LENGTH_SHORT).show()
        tryToSaveGame(notify = true)
        super.onRestart()
    }

    override fun onDestroy() {
        //Toast.makeText(this, "onDestroy()!!", Toast.LENGTH_SHORT).show()
        tryToSaveGame(notify = true, isEmergency = true)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return menu?.hasVisibleItems() ?: true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_main_settings -> {
                with(dialogBinding) {
                    /*TODO("I need to research and learn more to understand what is the real cause behind the following issue:" +
                                "The specified child already has a parent. You must call removeView() on the child's parent first")
*/
                    val menuDialog: AlertDialog = AlertDialog
                        .Builder(this@MainActivity)
                        .create()
                    Log.d("parentOxfDialog", "${dialogBinding.root.rootView.allViews.map { it.id }.toList()}")


                    if (!gameData.isScoreboardDisplaySetAsTop) {
                        menuDialog.setView(root)
                        gameData.isScoreboardDisplaySetAsTop = true
                    }

                    dialogBinding.root.removeView(menuDialog.listView)
                    roundScorePositionSpinner.adapter =
                        ArrayAdapter.createFromResource(
                            mainBinding.root.context,
                            R.array.round_score_position_array,
                            com.google.android.material.R.layout.support_simple_spinner_dropdown_item
                        ).apply {
                            setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
                        }
                    roundScorePositionSpinner.onSelected { position, _ ->
                        updateScoreboard(position, position == 0)
                    }
                        /*.also {
                            roundScorePositionSpinner.adapter = it
                            AdapterView
                                .inflate(dialogBuilder.context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dialogBinding.root)
                                .also {
                                }
                        }*/
                    menuDialog.show()
                    menuDialog.window?.exitTransition?.startDelay = 1000


                    /*These two buttons (close/continue) do the same, they shouldn't coexist. Potential rework?*/
                    continueGameButton.setOnClickListener {
                        menuDialog.dismiss()
                        menuDialog.cancel()
                    }
                    restartGameButton.setOnClickListener { restartGame(mainBinding); menuDialog.dismiss() }
                    exitGameButton.setOnClickListener { menuDialog.dismiss(); finish(); exitProcess(0) }

                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun updateScoreboard(position: Int? = null, isTopScoreViewVisible: Boolean) {
        with(mainBinding) {
            if (position == 0 || isTopScoreViewVisible) {
                score.visibility = View.VISIBLE
                currentRoundTextView.visibility = View.VISIBLE
                scoreGroupLayout?.visibility = View.GONE
                roundGroupLayout?.visibility = View.GONE

                score.text = getString(R.string.score_value, gameData.totalScore)
                currentRoundTextView.text = getString(R.string.round_value, gameData.currentRound)
            }
            else {
                score.visibility = View.GONE
                currentRoundTextView.visibility = View.GONE
                scoreGroupLayout?.visibility = View.VISIBLE
                roundGroupLayout?.visibility = View.VISIBLE

                scoreValueBottomTextview?.text = getString(R.string.score_value, gameData.totalScore)
                roundValueBottomTextview?.text = getString(R.string.round_value, gameData.currentRound)
            }
        }
    }

    private fun updateGame(position: Int? = null, isTopScoreViewVisible: Boolean) {
        updateScoreboard(position, isTopScoreViewVisible)
        gameData
    }

    @Deprecated("This function has been deprecated.", ReplaceWith("restartApp"), DeprecationLevel.WARNING)
    private fun setUpApp(context: ActivityMainBinding) {
        with(context) {
            targetTextView
                .text = gameData.setNewTarget().toString()
            /*score.text = getString(R.string.score_value, gameData.totalScore)
            gameData.currentRoundTextView.text = getString(R.string.score_value, gameData.currentRound)*/
            //TODO("Work around the spinner not being set up correctly since the UI won't get updated as expected due to the spinner not having a \"default\" value")
            updateScoreboard(0, true)
        }
    }

    /**
     * Restarts the game through the "Start Over" button, doing a partial restart of the Score, Rounds and sets a new Target.
     *
     * @param context the context that hosts the views that should have their values reset
     * @return Unit - therefore this function doesn't return anything.
     * **/
    private fun restartGame(context: ActivityMainBinding) {
        with(context) {
            gameData.currentRound = 1
            gameData.totalScore = 0
            gameData.setNewTarget()
            seekBar.progress = 50
            gameData.sliderValue = seekBar.progress

            /*gameData.currentRoundTextView.text = getString(R.string.round_value, gameData.currentRound)
            score.text = getString(R.string.score_value, gameData.totalScore)*/
            targetTextView.text = gameData.currentTarget().toString()

            updateScoreboard(isTopScoreViewVisible = context.score.visibility == View.VISIBLE)
        }
    }
    /**
     * Accurately determines the points the player will get, after an exhaustive calculation of the hit's precision against the target's value.
     *
     * @return the amount of points the player got based on how close its hit was from the target for that particular round.
     * */
    private fun pointsCalculation() : Int = gameData.currentTarget().apply {

        //Note: slicerValue == hit && gameData.newTarget() == target
        /*Toast.makeText(this@MainActivity, "hit: $hit, target: $target", Toast.LENGTH_LONG).show()
        Log.d("trackingPoints", "hit: $hit\ntarget: $target")*/
        return analyzeHit(gameData.sliderValue.toDouble(), this.toDouble()).second
    }

    /**
     * Defective - Analyzes the hit the player did based on some fixed percentages.
     *
     * @param hit the slicerValue, or in other words the hit the player did.
     * @param target the gameData.newTarget(), or in other words the value the player should aim to hit.
     *
     * @see R.string.result_dialog_title0
     * @see R.string.result_dialog_title1
     * @see R.string.result_dialog_title2
     * @see R.string.result_dialog_title3
     * @see R.string.result_dialog_title4
     * @see R.string.result_dialog_title5
     * @see R.string.result_dialog_title6
     * @see R.string.result_dialog_title7
     * @see R.string.result_dialog_title8
     * @see R.string.result_dialog_title9
     * @see R.string.result_dialog_title10
     * @see R.string.result_dialog_title11
     * @see R.string.result_dialog_title12
     *
     * @return A pair containing the customized name assigned based on the player's hit, and also returns the points gotten.
     * **/
    private fun analyzeHit(hit: Double, target: Double): Pair<String, Int> {
        /*
            If the target is lower than 50, it will get subtracted from the maxTarget value, so to evenly calculate the points
            instead of having a wider error margin when the target is lower than 50 points due to the percentage calculation basis.
        */
        val (localTarget: Double, localHit: Double) =
            if (target < 50) (100 - target) to (100 - hit)
            else target to hit
        return when {
            localHit > (localTarget * 1.975)
                    || localHit < (localTarget * 0.025) -> getString(R.string.result_dialog_title12) to -200
            localHit > (localTarget * 1.60)
                    || localHit < (localTarget * 0.025) -> getString(R.string.result_dialog_title11) to -150
            localHit > (localTarget * 1.50)
                    || localHit < (localTarget * 0.70) -> getString(R.string.result_dialog_title10) to -125
            localHit > (localTarget * 1.40)
                    || localHit < (localTarget * 0.60) -> getString(R.string.result_dialog_title9) to -80
            localHit > (localTarget * 1.31)
                    || localHit < (localTarget * 0.69) -> getString(R.string.result_dialog_title8) to -30
            localHit >= floor((localTarget * 1.30))
                    || localHit <= floor((localTarget * 0.70)) -> getString(R.string.result_dialog_title7) to 1
            localHit >= floor((localTarget * 1.20))
                    || localHit <= floor((localTarget * 0.80)) -> getString(R.string.result_dialog_title6) to 5
            localHit >= floor((localTarget * 1.15))
                    || localHit <= floor((localTarget * 0.85)) -> getString(R.string.result_dialog_title5) to 15
            localHit >= floor((localTarget * 1.10))
                    || localHit <= floor((localTarget * 0.90)) -> getString(R.string.result_dialog_title4) to 50
            localHit >= floor((localTarget * 1.05))
                    || localHit <= floor((localTarget * 0.95)) -> getString(R.string.result_dialog_title3) to 80
            localHit >= floor((localTarget * 1.025))
                    || localHit <= floor((localTarget * 0.975)) -> getString(R.string.result_dialog_title2) to 125
            localHit >= (localTarget * 1.0001)
                    || localHit <= (localTarget * 0.9999) -> getString(R.string.result_dialog_title1) to 150
            else -> getString(R.string.result_dialog_title0) to 200
        }
    }
        // maxScore - (gameData.newTarget() - sliderValue)
            /* Code doesn't work, always tends to 0.
            sliderValue < (gameData.newTarget() * sensibility.first) -> ((score ?: 100) * -1) * (sliderValue / gameData.newTarget().toDouble())
            sliderValue > (gameData.newTarget() * sensibility.second) -> ((score ?: 100) * -1) * (gameData.newTarget() / sliderValue.toDouble())
            gameData.newTarget() > sliderValue -> (score ?: -100) * (sliderValue / gameData.newTarget().toDouble())
            gameData.newTarget() < sliderValue -> (score ?: -100) * (gameData.newTarget() / sliderValue.toDouble())
            else -> -100*/

        /*if (gameData.newTarget() > sliderValue) (score ?: 100) * (sliderValue / gameData.newTarget())
                                                    else (score ?: 100) * (sliderValue / gameData.newTarget())*/

    private fun getPoints(): Int = pointsCalculation()

    /**
     * Displays a Dialog with a detailed result of the player's try.
     *
     * @param pointsGotten It should be the points the player got during his try.
     *
     * @return this function does not return something.**/
    private fun displayResult(targetValue: Int, sliderValue: Int, pointsGotten: Int, totalScore: Int) {
        val dialogTitle: String
            = analyzeHit(gameData.sliderValue.toDouble(), targetValue.toDouble()).first
        val dialogMessage: String
            //  = getString(R.string.result_dialog_message)
            = getString(R.string.result_dialog_message, targetValue, sliderValue, pointsGotten, totalScore)
        val builder: AlertDialog.Builder
            =   AlertDialog.Builder(this)

        builder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton(R.string.result_dialog_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                partialRestartAfterTry(mainBinding)
            }
            .create()
            .show()
    }

    /**
     * Restarts the Target after the player's try
     *
     * @param context the context that hosts the views that should have their values reset
     * @return Unit - therefore this function doesn't return anything.
     * **/
    private fun partialRestartAfterTry(context: ActivityMainBinding) {
        with(context) {
            updateScoreboard(isTopScoreViewVisible = context.score.visibility == View.VISIBLE)
            seekBar.progress = 50
            gameData.setNewTarget()
            targetTextView.text = gameData.setNewTarget().toString()
        }

    }

    private fun tryToSaveGame(notify: Boolean = true, isEmergency: Boolean = false) {
        /*val result = */with(gameDataManager) { if (isEmergency) emergencyDataSaving(gameData) else saveData(gameData) }
        //if (result && notify) Toast.makeText(this, "Autosaved!", Toast.LENGTH_SHORT).show()
    }
}
