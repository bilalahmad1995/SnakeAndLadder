package com.saifkhichi.moksha

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saifkhichi.moksha.models.AI
import com.saifkhichi.moksha.models.GBoard
import com.saifkhichi.moksha.models.GPlayer
import com.saifkhichi.moksha.models.User
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val board = GBoard()
    private val ai = AI("Computer", R.id.computerPiece)
    private val user = User("Player", R.id.userPiece)

    private var dice: ImageView? = null
    private val SIZE = Point()
    private var isOver = false

    private var currentPlayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dice = findViewById(R.id.dice)
        currentPlayer = 1
        windowManager.defaultDisplay.getSize(SIZE)
        findViewById<View>(R.id.board).post { SIZE.y = findViewById<View>(R.id.board).height }
    }

    fun takeTurn(v: View?) {
        if (!isOver) {
            val diceValue = rollDice(user)
            if (currentPlayer == 1) {
                takeTurn(user, diceValue)
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
                takeTurn(ai, rollDice(ai))
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

    private fun rollDice(player: GPlayer): Int {
        setStatus("${player.name} rolling dice...")
        val diceImages = arrayOf(
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four,
            R.drawable.five,
            R.drawable.six,
        )

        val value = (Math.random() * 6).toInt()
        this.dice?.setImageResource(diceImages[value])
        setStatus("${player.name} rolled ${value + 1}...")
        return value + 1
    }

    private fun setStatus(status: String) = runOnUiThread {
        findViewById<TextView>(R.id.gameStatus).text = status
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        ImageView img_animation = (ImageView) findViewById(R.id.maze);
//        int[] viewCoords = new int[2];
//        img_animation.getLocationOnScreen(viewCoords);
//
//        int touchX = (int) event.getX();
//        int touchY = (int) event.getY();
//
//        int imageX = touchX - viewCoords[0]; // viewCoords[0] is the X coordinate
//        int imageY = touchY - viewCoords[1]; // viewCoords[1] is the y coordinate
//
//        // float x = event.getX();
//        //float y = event.getY();
//        Toast.makeText(MainActivity.this,"X = "+imageX+" and y ="+imageY,Toast.LENGTH_SHORT).show();
//        return super.onTouchEvent(event);
//    }

}