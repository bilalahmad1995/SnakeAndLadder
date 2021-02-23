package com.saifkhichi.moksha.models

/**
 * GBoard is the game board.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class GBoard {

    val snakes = ArrayList<Snake>()
    val ladders = ArrayList<Ladder>()

    init {
        snakes.add(Snake(14, 8))
        snakes.add(Snake(22, 3))
        snakes.add(Snake(31, 15))
        snakes.add(Snake(41, 20))
        snakes.add(Snake(58, 37))
        snakes.add(Snake(67, 50))
        snakes.add(Snake(77, 56))
        snakes.add(Snake(83, 80))
        snakes.add(Snake(92, 76))
        snakes.add(Snake(99, 5))

        ladders.add(Ladder(2, 23))
        ladders.add(Ladder(9, 13))
        ladders.add(Ladder(17, 93))
        ladders.add(Ladder(29, 54))
        ladders.add(Ladder(32, 51))
        ladders.add(Ladder(39, 80))
        ladders.add(Ladder(44, 64))
        ladders.add(Ladder(62, 78))
        ladders.add(Ladder(70, 89))
        ladders.add(Ladder(75, 96))
    }

}