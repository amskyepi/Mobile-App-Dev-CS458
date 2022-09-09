package com.example.othello

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

@SuppressLint("WrongViewCast")
class MainActivity : AppCompatActivity(), GridObserver {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val board = findViewById<GameActivity>(R.id.gameActivity)
        board.registerObserver(this)

        findViewById<TextView>(R.id.p1_score_text).text = "P1 (Black) Score : " + board.setPlayerScore(1).toString()
        findViewById<TextView>(R.id.p2_score_text).text = "P2 (White) Score : " + board.setPlayerScore(2).toString()
    }

    @SuppressLint("SetTextI18n")
    override fun gridUpdated() {
        val board = findViewById<GameActivity>(R.id.gameActivity)
        val p1Score = board.getPlayerScore(1)
        val p2Score = board.getPlayerScore(2)
        val gameState = board.gameState
        findViewById<TextView>(R.id.p1_score_text).text = "P1 (Black) Score : $p1Score"
        findViewById<TextView>(R.id.p2_score_text).text = "P2 (White) Score : $p2Score"
        if(gameState) {
            if ((p1Score + p2Score) == 64) {
                if (p1Score > p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                    "Player 1 wins!"
                if (p1Score < p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                    "Player 2 wins!"
                if (p1Score == p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                    "We have a tie!"
            } else {
                val currentPlayer = board.checkPlayer()
                findViewById<TextView>(R.id.inGameMessage).text = "Player $currentPlayer's turn"
            }
        }
        else {
            if (p1Score > p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                "Player 1 wins!"
            if (p1Score < p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                "Player 2 wins!"
            if (p1Score == p2Score) findViewById<TextView>(R.id.inGameMessage).text =
                "We have a tie!"
        }
    }

    @SuppressLint("SetTextI18n")
    fun resetBoard(view: View){
        val board = findViewById<GameActivity>(R.id.gameActivity)
        val currentPlayer = board.checkPlayer()
        board.resetBoard()
        board.invalidate()
        findViewById<Button>(R.id.reset_button).text = "RESET"
        findViewById<TextView>(R.id.p1_score_text).text = "P1 (Black) Score : " + board.setPlayerScore(1).toString()
        findViewById<TextView>(R.id.p2_score_text).text = "P2 (White) Score : " + board.setPlayerScore(2).toString()
        findViewById<TextView>(R.id.inGameMessage).text = "Player $currentPlayer's turn"
    }


}