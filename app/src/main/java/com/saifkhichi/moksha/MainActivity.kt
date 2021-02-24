package com.saifkhichi.moksha

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saifkhichi.moksha.models.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val board = GBoard()
    private val ai = AI("Computer", R.id.computerPiece)
    private val user = User("Player", R.id.userPiece)

    private lateinit var die: Die
    private val SIZE = Point()
    private var isOver = false

    private var currentPlayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        die = Die(findViewById(R.id.dice))
        currentPlayer = 1

        windowManager.defaultDisplay.getSize(SIZE)
        findViewById<View>(R.id.board).post { SIZE.y = findViewById<View>(R.id.board).height }
    }

    fun takeTurn(v: View?) {
        if (!isOver) {
            if (currentPlayer == 1) {
                rollDice(user) { value ->
                    takeTurn(user, value)
                }
            }
        }
    }

    private fun takeTurn(player: GPlayer, steps: Int) {
        // Move the player
        if (player.canMove(steps)) {
            val lastPosition = player.position
            val newPosition = lastPosition + steps

            // Run animation before moving the player
            moveBySteps(player, lastPosition, newPosition) {
                player.move(steps)

                // Check if the player has won
                if (player.hasWon()) {
                    Toast.makeText(this, "${player.name} wins!", Toast.LENGTH_SHORT).show()
                    isOver = true
                    onOver()
                } else {
                    board.snakes.forEach { snake ->
                        if (player.isOn(snake)) {
                            snake.bite(player)
                        }
                    }

                    board.ladders.forEach { ladder ->
                        if (player.isOn(ladder)) {
                            ladder.ascend(player)
                        }
                    }

                    moveDirect(player, newPosition, player.position) {
                        // Update UI
                        when (player) {
                            is AI -> findViewById<TextView>(R.id.computerPosition)
                            is User -> findViewById(R.id.playerPosition)
                        }.text = "${player.name}\n${player.position}"

                        // Check post-conditions
                        onMoved()
                    }
                }
            }
        }
    }

    private fun onOver() {
        setStatus("Game Over!")
    }

    private fun onMoved() {
        if (isOver) onOver()
        else when (currentPlayer) {
            1 -> {
                currentPlayer = 2
                setStatus("${ai.name}'s Turn")
                rollDice(ai) { value ->
                    takeTurn(ai, value)
                }
            }
            2 -> {
                currentPlayer = 1
                setStatus("${user.name}'s Turn")
            }
        }
    }

    private fun getX(position: Int): Int {
        var col = position % 10
        col = if (position % 10 == 0) 10 else col
        var row = position / 10
        row = if (position % 10 == 0) row - 1 else row
        col = if (row % 2 != 0) 10 - col else col - 1
        return (col / 10f * SIZE.x).toInt()
    }

    private fun getY(position: Int): Int {
        var row = position / 10
        row = if (position % 10 == 0) row else row + 1
        return SIZE.y - (row / 10f * SIZE.x).toInt()
    }

    private fun moveDirect(player: GPlayer, from: Int, to: Int, onMoved: (p: Int) -> Unit) = runOnUiThread {
        val x1 = getX(from)
        val y1 = getY(from)
        val x2 = getX(to)
        val y2 = getY(to)
        val animation = TranslateAnimation(x1.toFloat(), x2.toFloat(), y1.toFloat(), y2.toFloat())

        val steps = abs(to - from)
        animation.duration = when (steps == 1) {
            true -> 250L
            else -> (250 * (1 + steps / 10)).toLong()
        }
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                setStatus("Moving player $currentPlayer...")
            }

            override fun onAnimationEnd(animation: Animation) {
                onMoved(to)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        animation.fillAfter = true
        findViewById<View>(player.view).visibility = View.VISIBLE
        findViewById<View>(player.view).startAnimation(animation)
    }

    private fun moveBySteps(player: GPlayer, current: Int, final: Int, onEnd: () -> Unit) {
        var next = if (final > current) current + 1 else current - 1
        moveDirect(player, current, next) { position ->
            if (position != final) {
                next = if (final > current) current + 1 else current - 1
                moveBySteps(player, position, final, onEnd)
            } else {
                onEnd()
            }
        }
    }

    private fun rollDice(player: GPlayer, callback: (Int) -> Unit) {
        setStatus("${player.name} rolling dice...")
        die.roll { value ->
            setStatus("${player.name} rolled ${value}...")
            callback(value)
        }
    }

    private fun setStatus(status: String) = runOnUiThread {
        findViewById<TextView>(R.id.gameStatus).text = status
    }

}