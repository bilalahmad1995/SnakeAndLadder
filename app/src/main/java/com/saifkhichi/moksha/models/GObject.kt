package com.saifkhichi.moksha.models


/**
 * GObject represents a single game object, such as snakes and
 * ladders. The objects usually have a starting and ending
 * point.
 *
 * @author saikfhichi96
 * @since 1.0.0
 */
sealed class GObject constructor(val from: Int, val to: Int) {
    protected fun move(player: GPlayer) {
        player.move(to - from)
    }
}

class Snake(head: Int, tail: Int) : GObject(from = head, to = tail) {
    fun bite(player: GPlayer) {
        super.move(player)
    }
}

class Ladder(foot: Int, top: Int) : GObject(from = foot, to = top) {
    fun ascend(player: GPlayer) {
        super.move(player)
    }
}