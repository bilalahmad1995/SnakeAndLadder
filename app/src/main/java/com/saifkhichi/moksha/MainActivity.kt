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
import java.util.*

class MainActivity : AppCompatActivity() {
    private var dice: ImageView? = null
    private val SIZE = Point()
    private var isOver = false
    private var player = 0
    private var computer = 0
    private var currentPlayer = 0
    private val snakes = ArrayList<GameObject>()
    private val ladders = ArrayList<GameObject>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dice = findViewById(R.id.dice)
        snakes.add(GameObject(22, 3))
        snakes.add(GameObject(14, 8))
        snakes.add(GameObject(31, 15))
        snakes.add(GameObject(41, 20))
        snakes.add(GameObject(58, 37))
        snakes.add(GameObject(67, 50))
        snakes.add(GameObject(77, 56))
        snakes.add(GameObject(83, 80))
        snakes.add(GameObject(92, 76))
        snakes.add(GameObject(99, 5))
        ladders.add(GameObject(23, 2))
        ladders.add(GameObject(13, 9))
        ladders.add(GameObject(93, 17))
        ladders.add(GameObject(54, 29))
        ladders.add(GameObject(51, 32))
        ladders.add(GameObject(80, 39))
        ladders.add(GameObject(78, 62))
        ladders.add(GameObject(44, 64))
        ladders.add(GameObject(96, 75))
        ladders.add(GameObject(89, 70))
        player = 1
        computer = 1
        currentPlayer = 1
        windowManager.defaultDisplay.getSize(SIZE)
        findViewById<View>(R.id.board).post { SIZE.y = findViewById<View>(R.id.board).height }
    }

    fun takeTurn(v: View?) {
        if (!isOver) {
            val diceValue = getDice()
            if (currentPlayer == 1) {
                playersTurn(diceValue)
            }
        }
    }

    private fun computerTurn(diceValue: Int) {
        val oldPosition = computer
        computer += diceValue
        if (computer == 100) {
            Toast.makeText(this, "Computer wins!", Toast.LENGTH_SHORT).show()
            isOver = true
        } else if (computer > 100) {
            computer -= diceValue
        } else {
            for (snake in snakes) {
                if (snake.head == computer) {
                    computer = snake.tail
                }
            }
            for (ladder in ladders) {
                if (ladder.tail == computer) {
                    computer = ladder.head
                }
            }
        }
        (findViewById<View>(R.id.computerPosition) as TextView).text = "Computer\n$computer"
        moveOnScreen(R.id.computerPiece, oldPosition, computer)
    }

    private fun playersTurn(diceValue: Int) {
        val oldPosition = player
        player += diceValue
        // Wins
        if (player == 100) {
            Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show()
            isOver = true
        } else if (player > 100) {
            player -= diceValue
        } else {
            for (snake in snakes) {
                if (snake.head == player) {
                    player = snake.tail
                }
            }
            for (ladder in ladders) {
                if (ladder.tail == player) {
                    player = ladder.head
                }
            }
        }
        (findViewById<View>(R.id.playerPosition) as TextView).text = "Player\n$player"
        moveOnScreen(R.id.userPiece, oldPosition, player)
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

    private fun moveOnScreen(id: Int, oldPosition: Int, newPosition: Int) {
        (findViewById<View>(R.id.gameStatus) as TextView).text = "P$currentPlayer rolling dice ..."
        val x1 = getX(oldPosition)
        val y1 = getY(oldPosition)
        val x2 = getX(newPosition)
        val y2 = getY(newPosition)
        val animation = TranslateAnimation(x1.toFloat(), x2.toFloat(), y1.toFloat(), y2.toFloat())
        animation.duration = 500
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                (findViewById<View>(R.id.gameStatus) as TextView).text = "Moving player $currentPlayer..."
            }

            override fun onAnimationEnd(animation: Animation) {
                if (currentPlayer == 1) {
                    currentPlayer = 2
                    (findViewById<View>(R.id.gameStatus) as TextView).text = "Computer's Turn"
                    computerTurn(getDice())
                } else if (currentPlayer == 2) {
                    currentPlayer = 1
                    (findViewById<View>(R.id.gameStatus) as TextView).text = "Player's Turn"
                }
                if (isOver) {
                    (findViewById<View>(R.id.gameStatus) as TextView).text = "Game Over!"
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        animation.fillAfter = true
        findViewById<View>(id).visibility = View.VISIBLE
        findViewById<View>(id).startAnimation(animation)
    }

    fun getDice(): Int {
        val dice = (Math.random() * 6).toInt() + 1
        when (dice) {
            1 -> this.dice!!.setImageResource(R.drawable.one)
            2 -> this.dice!!.setImageResource(R.drawable.two)
            3 -> this.dice!!.setImageResource(R.drawable.three)
            4 -> this.dice!!.setImageResource(R.drawable.four)
            5 -> this.dice!!.setImageResource(R.drawable.five)
            6 -> this.dice!!.setImageResource(R.drawable.six)
        }
        return dice
    }

    //
    //        public boolean onTouchEvent(MotionEvent event) {
    //
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
    private inner class GameObject constructor(val head: Int, val tail: Int)

}