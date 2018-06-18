package bilaPani

import java.awt.Color

abstract class Square {

    abstract val color: Color

    abstract val isWalkable: Boolean

}

class EmptySquare: Square() {

    override val color: Color
        get() = Color.WHITE

    override val isWalkable: Boolean
        get() = true

}

class Wall: Square() {

    override val color: Color
        get() = Color.DARK_GRAY

    override val isWalkable: Boolean
        get() = false

}

class TV: Square() {

    var x = 0
    var y = 0

    override val color: Color
        get() = Color.RED

    override val isWalkable: Boolean
        get() = true

}

class Path(xCoor: Int, yCoor: Int, previousNode: Path?): Square() {

    var x = xCoor
    var y = yCoor

    val previous: Path? = previousNode

    constructor(): this(0, 0, null)

    override val color: Color
        get() = Color.ORANGE

    override val isWalkable: Boolean
        get() = true

    fun equals(other: Path): Boolean {
        return (x == other.x && y == other.y)
    }

}

class Lady: Square() {

    var x = 0
    var y = 0

    override val color: Color
        get() = Color.CYAN

    override val isWalkable: Boolean
        get() = true

}