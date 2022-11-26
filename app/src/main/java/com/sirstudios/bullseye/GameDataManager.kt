package com.sirstudios.bullseye

import android.content.Context
import androidx.preference.PreferenceManager
import android.widget.Toast

class GameDataManager(private val context: Context) {
    fun saveData(data: GameData): Boolean = dataSaver(data, false)

    fun emergencyDataSaving(data: GameData): Boolean = dataSaver(data, true)

    private fun dataSaver(data: GameData, isEmergency: Boolean): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
        return try {
            sharedPreferences.putInt("sliderValue", data.sliderValue)
            sharedPreferences.putInt("totalScore", data.totalScore)
            sharedPreferences.putInt("currentRound", data.currentRound)
            sharedPreferences.putBoolean("isScoreboardDisplaySetAsTop", data.isScoreboardDisplaySetAsTop)
            if (isEmergency) sharedPreferences.commit() else sharedPreferences.apply()
            true
        } catch (e: AccessDeniedException) {
            Toast.makeText(context, "$e", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun readData(): GameData {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gameData: GameData = GameData()
        val storedData = sharedPreferences.all

        gameData.sliderValue = (storedData["sliderValue"] as Int?) ?: 50
        gameData.totalScore = (storedData["totalScore"] as Int?) ?: 0
        gameData.currentRound = (storedData["currentRound"] as Int?) ?: 1
        gameData.isScoreboardDisplaySetAsTop = (storedData["isScoreboardDisplaySetAsTop"] as Boolean?) ?: true

        return gameData
    }
}