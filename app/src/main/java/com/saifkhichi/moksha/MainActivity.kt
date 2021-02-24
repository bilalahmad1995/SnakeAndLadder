package com.saifkhichi.moksha

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.saifkhichi.moksha.models.*
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val board = GBoard()
    private val ai = AI("Computer", R.id.computerPiece)
    private val user = User("Player", R.id.userPiece)

    private lateinit var dieUser: Die
    private lateinit var dieAI: Die
    private val SIZE = Point()
    private var isOver = false

    private var dx = 0f
    private var dy = 0f

    private var currentPlayer: GPlayer = user
        set(value) {
            field = value
            when (value) {
                is AI -> {
                    dieAI.show()
                    dieUser.hide()
                    setStatus("${value.name}'s turn.")

                    rollDice(dieAI, ai) {
                        takeTurn(ai, it)
                    }
                }
                is User -> {
                    dieAI.hide()
                    dieUser.show()
                    setStatus("Your turn. Roll the dice!")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dieUser = Die(findViewById(R.id.dice))
        dieUser.show()

        dieAI = Die(findViewById(R.id.die_ai))
        dieAI.hide()

        currentPlayer = user
        setStatus("Your turn. Roll the dice!")

        findViewById<TextView>(R.id.computerName).text = ai.name
        findViewById<TextView>(R.id.playerName).text = user.name

        windowManager.defaultDisplay.getSize(SIZE)
        val boardContainer = findViewById<View>(R.id.board_container)
        boardContainer.post {
            dx = boardContainer.x
            dy = (0.015 * boardContainer.width).toFloat()
            SIZE.x = (0.985 * boardContainer.width).roundToInt()
            SIZE.y = (boardContainer.height * 1.6f).roundToInt()
        }
    }

    fun takeTurn(v: View?) {
        if (!isOver) {
            if (currentPlayer is User) {
                rollDice(dieUser, user) { value ->
                    takeTurn(user, value)
                }
            }
        }
    }

    private fun takeTurn(player: GPlayer, steps: Int) {
        // Move the player
        if (!player.canMove(steps)) return onMoved(0) // TODO: Move forward, then backwards
        val lastPosition = player.position
        val newPosition = lastPosition + steps

        // Run animation before moving the player
        moveBySteps(player, lastPosition, newPosition) {
            player.move(steps)

            // Check if the player has won
            if (player.hasWon()) {
                setStatus("You ${if (player is User) "won" else "lost"}! Game over.")

                // Update UI
                when (player) {
                    is AI -> findViewById<TextView>(R.id.computerPosition)
                    is User -> findViewById(R.id.playerPosition)
                }.text = "${player.position}"

                isOver = true
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
                    }.text = "${player.position}"

                    // Check post-conditions
                    onMoved(steps)
                }
            }
        }
    }

    private fun onMoved(lastRoll: Int) {
        if (!isOver) {
            currentPlayer = when (lastRoll) {
                6 -> currentPlayer
                else -> when (currentPlayer) {
                    is User -> ai
                    is AI -> user
                }
            }
        }
    }

    private fun getX(position: Int): Int {
        var col = position % 10
        col = if (position % 10 == 0) 10 else col
        var row = position / 10
        row = if (position % 10 == 0) row - 1 else row
        col = if (row % 2 != 0) 10 - col else col - 1
        return (dx + col / 10f * SIZE.x).toInt()
    }

    private fun getY(position: Int): Int {
        var row = position / 10
        row = if (position % 10 == 0) row else row + 1
        return (SIZE.y - (row / 10f * SIZE.x) - dy).roundToInt()
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
                setStatus("Moving ${if (player is User) "your" else "computer's"} piece...")
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

    private fun rollDice(die: Die, player: GPlayer, callback: (Int) -> Unit) {
        setStatus("${player.name} rolling the dice...")
        die.roll { value ->
            setStatus("${player.name} rolled ${value}...")
            callback(value)
        }
    }

    private fun setStatus(status: String) = runOnUiThread {
        findViewById<TextView>(R.id.gameStatus).text = ">> $status"
    }

}