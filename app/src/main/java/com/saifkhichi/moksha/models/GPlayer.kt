package com.saifkhichi.moksha.models

/**
 * A game player.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
sealed class GPlayer(val name: String, val view: Int) {
    var position: Int = 1

    /**
     * Checks if the player can move the given number of [steps].
     */
    fun canMove(steps: Int): Boolean {
        return position + steps in 1..100
    }

    fun hasWon(): Boolean {
        return position == 100
    }

    fun isOn(obj: GObject): Boolean {
        return position == obj.from
    }

    fun move(steps: Int) {
        position += steps
    }
}

/**
 * An AI game player.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class AI(name: String, view: Int) : GPlayer(name, view)

/**
 * A user-controller game player.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class User(name: String, view: Int) : GPlayer(name, view)