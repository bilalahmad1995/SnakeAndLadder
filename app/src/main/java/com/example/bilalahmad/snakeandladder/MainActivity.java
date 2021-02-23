package com.example.bilalahmad.snakeandladder;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView dice;

    private final Point SIZE = new Point();

    private boolean isOver = false;

    private int player;
    private int computer;
    private int currentPlayer;
    private final ArrayList<GameObject> snakes = new ArrayList<>();
    private final ArrayList<GameObject> ladders = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dice = findViewById(R.id.dice);

        snakes.add(new GameObject(22, 3));
        snakes.add(new GameObject(14, 8));
        snakes.add(new GameObject(31, 15));
        snakes.add(new GameObject(41, 20));
        snakes.add(new GameObject(58, 37));
        snakes.add(new GameObject(67, 50));
        snakes.add(new GameObject(77, 56));
        snakes.add(new GameObject(83, 80));
        snakes.add(new GameObject(92, 76));
        snakes.add(new GameObject(99, 5));

        ladders.add(new GameObject(23, 2));
        ladders.add(new GameObject(13, 9));
        ladders.add(new GameObject(93, 17));
        ladders.add(new GameObject(54, 29));
        ladders.add(new GameObject(51, 32));
        ladders.add(new GameObject(80, 39));
        ladders.add(new GameObject(78, 62));
        ladders.add(new GameObject(44, 64));
        ladders.add(new GameObject(96, 75));
        ladders.add(new GameObject(89, 70));

        player = 1;
        computer = 1;
        currentPlayer = 1;

        getWindowManager().getDefaultDisplay().getSize(SIZE);
        findViewById(R.id.board).post(new Runnable() {
            @Override
            public void run() {
                SIZE.y = findViewById(R.id.board).getHeight();
            }
        });
    }

    public void takeTurn(View v) {

        if (!isOver) {
            int diceValue = getDice();
            if (currentPlayer == 1) {
                playersTurn(diceValue);
            }
        }
    }

    private void computerTurn(int diceValue) {
        int oldPosition = computer;
        computer += diceValue;

        if (computer == 100) {
            Toast.makeText(this, "Computer wins!", Toast.LENGTH_SHORT).show();
            isOver = true;
        }

        // Goes off the board
        else if (computer > 100) {
            computer -= diceValue;
        }

        // Snakes and ladders
        else {

            for (GameObject snake : snakes) {
                if (snake.getHead() == computer) {
                    computer = snake.getTail();
                }
            }

            for (GameObject ladder : ladders) {
                if (ladder.getTail() == computer) {
                    computer = ladder.getHead();
                }
            }
        }
        ((TextView) findViewById(R.id.computerPosition)).setText("Computer\n" + computer);
        moveOnScreen(R.id.computerPiece, oldPosition, computer);

    }

    private void playersTurn(int diceValue) {
        int oldPosition = player;
        player += diceValue;
        // Wins
        if (player == 100) {
            Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show();
            isOver = true;
        }

        // Goes off the board
        else if (player > 100) {
            player -= diceValue;
        }

        // Snakes and ladders
        else {

            for (GameObject snake : snakes) {
                if (snake.getHead() == player) {
                    player = snake.getTail();
                }
            }

            for (GameObject ladder : ladders) {
                if (ladder.getTail() == player) {
                    player = ladder.getHead();
                }
            }

        }
        ((TextView) findViewById(R.id.playerPosition)).setText("Player\n" + player);
        moveOnScreen(R.id.userPiece, oldPosition, player);
    }

    private int getX(int position) {
        int col = position % 10;
        col = (position % 10 == 0) ? 10 : col;

        int row = position / 10;
        row = (position % 10 == 0) ? row - 1 : row;

        col = (row % 2 != 0) ? (10 - col) : col - 1;
        return  (int) (col / 10.f * SIZE.x);
    }

    private int getY(int position) {
        int row = position / 10;
        row = (position % 10 == 0) ? row : row + 1;
        return SIZE.y - (int) (row / 10.f * SIZE.x);
    }

    private void moveOnScreen(int id, int oldPosition, int newPosition) {

        ((TextView) findViewById(R.id.gameStatus)).setText("P" + currentPlayer + " rolling dice ...");

        int x1 = getX(oldPosition);
        int y1 = getY(oldPosition);

        int x2 = getX(newPosition);
        int y2 = getY(newPosition);

        TranslateAnimation animation = new TranslateAnimation(x1, x2, y1, y2);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((TextView) findViewById(R.id.gameStatus)).setText("Moving player " + currentPlayer + "...");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (currentPlayer == 1) {
                    currentPlayer = 2;
                    ((TextView) findViewById(R.id.gameStatus)).setText("Computer's Turn");

                    computerTurn(getDice());
                } else if (currentPlayer == 2) {
                    currentPlayer = 1;
                    ((TextView) findViewById(R.id.gameStatus)).setText("Player's Turn");
                }

                if (isOver) {
                    ((TextView) findViewById(R.id.gameStatus)).setText("Game Over!");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setFillAfter(true);
        findViewById(id).setVisibility(View.VISIBLE);
        findViewById(id).startAnimation(animation);
    }

    public int getDice() {

        int dice = (int) (Math.random() * 6) + 1;

        switch (dice) {

            case 1:
                this.dice.setImageResource(R.drawable.one);
                break;
            case 2:
                this.dice.setImageResource(R.drawable.two);
                break;
            case 3:
                this.dice.setImageResource(R.drawable.three);
                break;
            case 4:
                this.dice.setImageResource(R.drawable.four);
                break;
            case 5:
                this.dice.setImageResource(R.drawable.five);
                break;
            case 6:
                this.dice.setImageResource(R.drawable.six);
                break;

        }

        return dice;
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

    private class GameObject {

        private final int head;
        private final int tail;

        private GameObject(int head, int tail) {
            this.head = head;
            this.tail = tail;
        }


        public int getHead() {
            return head;
        }


        public int getTail() {
            return tail;
        }
    }
}
