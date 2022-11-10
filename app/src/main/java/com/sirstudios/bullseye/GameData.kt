package com.sirstudios.bullseye

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import kotlin.random.Random

class GameData(
    var sliderValue: Int = 0,
    var totalScore: Int = 0,
    var currentRound: Int = 1,
    var isScoreboardDisplaySetAsTop: Boolean = false
) : Parcelable {
    private var targetValue: Int = setNewTarget()

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    ) {
        targetValue = parcel.readInt()
    }

    init {
        this.targetValue = setNewTarget()
    }

    constructor(activity: Activity) : this() {
        val gameDataManager = GameDataManager(activity).readData()
        this.sliderValue = gameDataManager.sliderValue
        this.totalScore = gameDataManager.totalScore
        this.currentRound = gameDataManager.currentRound
        this.isScoreboardDisplaySetAsTop = gameDataManager.isScoreboardDisplaySetAsTop
        this.setNewTarget()
    }

    fun setNewTarget(): Int {
        this.targetValue = Random.nextInt(1, 100)
        return this.targetValue
    }

    fun currentTarget(): Int = targetValue
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(sliderValue)
        parcel.writeInt(totalScore)
        parcel.writeInt(currentRound)
        parcel.writeByte(if (isScoreboardDisplaySetAsTop) 1 else 0)
        parcel.writeInt(targetValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun status(): String {
        return """
            
            SliderValue: $sliderValue
            TotalScore: $totalScore
            TargetValue: $targetValue
            CurrentRound: $currentRound
            isScoreboardDisplaySetAsTop: $isScoreboardDisplaySetAsTop
        """.trimMargin()
    }

    companion object CREATOR : Parcelable.Creator<GameData> {
        override fun createFromParcel(parcel: Parcel): GameData {
            return GameData(parcel)
        }

        override fun newArray(size: Int): Array<GameData?> {
            return arrayOfNulls(size)
        }
    }
}