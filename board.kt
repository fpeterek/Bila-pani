package bilaPani

import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JPanel
import kotlin.collections.HashSet

class Board: JPanel(), KeyListener  {

    private var xSize = 32
    private var ySize = 32

    private val squareSide = 20

    private val window = Window(this, xSize * 20, ySize * 20)

    private val lock: ReentrantLock = ReentrantLock()

    private var board: LinkedList<LinkedList<Square>> = LinkedList()

    private var checkedBoards: HashSet<Path> = HashSet()
    private var validPath: LinkedList<Path> = LinkedList()

    private val lady = Lady()
    private val tv = TV()

    init {
        reset()
    }

    private fun reset() {

        checkedBoards = HashSet()
        validPath = LinkedList()
        board = LinkedList()

        generateBoard()
        placeLady()
        placeTV()

        repaint()

        Thread.sleep(1000)

        findPath()

        repaint()

    }

    private fun placeLady() {

        val rand = Random()
        lady.x = rand.nextInt(xSize)
        lady.y = rand.nextInt(ySize)

    }

    private fun placeTV() {

        val rand = Random()
        tv.x = rand.nextInt(xSize)
        tv.y = rand.nextInt(ySize)


    }

    private fun generateBoard() {

        val rand = Random()

        for (y in 0..ySize) {

            board.add(LinkedList())

            for (x in 0..xSize) {
                board.last.add(

                        if (rand.nextInt(100) < 10) {
                            Wall()
                        } else {
                            EmptySquare()
                        }

                )
            }

        }

    }

    private fun findPath() {

        seek()
        repaint()

    }

    private fun wasChecked(path: Path): Boolean {

        for (checked in checkedBoards) {

            if (path.x == checked.x && path.y == checked.y) {
                return true
            }

        }

        return false

    }

    private fun seek() {

        lock.lock()

        var nextSet: HashSet<Path> = HashSet()
        var currentSet: HashSet<Path> = HashSet()

        val p = Path()

        p.x = lady.x
        p.y = lady.y

        currentSet.add(p)

        while (true) {

            for (path in currentSet) {

                val adjacent = getAdjacent(path)

                for (ad in adjacent) {

                    if (! wasChecked(ad)) {
                        nextSet.add(ad)
                        checkedBoards.add(ad)
                    }

                }

            }

            if (nextSet.size == 0) {
                println("Path could not be found")
                break
            }

            for (path in nextSet) {
                if (path.x == tv.x && path.y == tv.y) {
                    println("Found")
                    return reconstructPath(path)
                }
            }

            currentSet = nextSet
            nextSet = HashSet()
            repaint()
            Thread.sleep(100)

        }

        lock.unlock()

    }

    private fun reconstructPath(last: Path) {

        if (last.previous == null) {
            return
        }

        validPath.push(last)
        reconstructPath(last.previous)

    }

    private fun getAdjacent(p: Path): HashSet<Path> {

        val set: HashSet<Path> = HashSet()

        val left: Path? = if (p.x == 0) {
            null
        } else if (! board[p.y][p.x - 1].isWalkable) {
            null
        } else {
            val path = Path(p.x - 1, p.y, p)
            path
        }

        val right: Path? = if (p.x == xSize - 1) {
            null
        } else if (! board[p.y][p.x + 1].isWalkable) {
            null
        } else {
            val path = Path(p.x + 1, p.y, p)
            path
        }

        val up: Path? = if (p.y == 0) {
            null
        } else if (! board[p.y - 1][p.x].isWalkable) {
            null
        } else {
            val path = Path(p.x, p.y - 1, p)
            path
        }

        val down: Path? = if (p.y == ySize - 1) {
            null
        } else if (! board[p.y + 1][p.x].isWalkable) {
            null
        } else {
            val path = Path(p.x, p.y + 1, p)
            path
        }

        if (up != null && !checkedBoards.contains(up)) {
            set.add(up)
        }
        if (down != null && !checkedBoards.contains(down)) {
            set.add(down)
        }
        if (left != null && !checkedBoards.contains(left)) {
            set.add(left)
        }
        if (right != null && !checkedBoards.contains(right)) {
            set.add(right)
        }

        return set

    }

    /*

    private fun seek() {

        var movedHorizontally = when {
            currentPath.last.x < tv.x -> moveRight()
            currentPath.last.x > tv.x -> moveLeft()
            else -> false
        }

        var movedVertically = when {
            currentPath.last.y > tv.y -> moveUp()
            currentPath.last.y < tv.y -> moveDown()
            else -> false
        }

        if (! (movedHorizontally || movedVertically)) {

            var movedHorizontally = when {
                currentPath.last.x > tv.x -> moveRight()
                currentPath.last.x < tv.x -> moveLeft()
                else -> false
            }

            var movedVertically = false

            if (! movedHorizontally) {

                movedVertically = when {
                    currentPath.last.y < tv.y -> moveUp()
                    currentPath.last.y > tv.y -> moveDown()
                    else -> false
                }

            }

            if (! (movedHorizontally || movedVertically)) {
                println("Error")
                return
            }

        }

        if (currentPath.last.x == tv.x && currentPath.last.y == tv.y) {
            validPath = currentPath
            println("Path found")
            return
        }

        repaint()
        Thread.sleep(250)
        seek()

    }

    private fun moveLeft(): Boolean {

        val last = currentPath.last

        if (! board[last.y][last.x - 1].isWalkable) {
            return false
        }

        val p = Path()
        p.x = last.x - 1
        p.y = last.y

        for (ps in currentPath) {
            if (p == ps) {
                return false
            }
        }

        currentPath.add(p)

        return true

    }

    private fun moveRight(): Boolean {

        val last = currentPath.last

        if (! board[last.y][last.x + 1].isWalkable) {
            return false
        }

        val p = Path()
        p.x = last.x + 1
        p.y = last.y

        for (ps in currentPath) {
            if (p == ps) {
                return false
            }
        }

        currentPath.add(p)

        return true

    }

    private fun moveUp(): Boolean {

        val last = currentPath.last

        if (! board[last.y - 1][last.x].isWalkable) {
            return false
        }

        val p = Path()
        p.x = last.x
        p.y = last.y - 1

        for (ps in currentPath) {
            if (p == ps) {
                return false
            }
        }

        currentPath.add(p)

        return true

    }

    private fun moveDown(): Boolean {

        val last = currentPath.last

        if (! board[last.y + 1][last.x].isWalkable) {
            return false
        }

        val p = Path()
        p.x = last.x
        p.y = last.y + 1

        for (ps in currentPath) {
            if (p == ps) {
                return false
            }
        }

        currentPath.add(p)

        return true

    }

    */

    override fun paint(g: Graphics?) {

        if (g == null) {
            return
        }

        super.paint(g)

        for ((y, list) in board.withIndex()) {

            for ((x, square) in list.withIndex()) {

                g.color = square.color
                g.fillRect(x * squareSide, y * squareSide, squareSide, squareSide)

            }

        }

        for (p in checkedBoards) {

            g.color = Color.PINK
            g.fillRect(p.x * squareSide, p.y * squareSide, squareSide, squareSide)

        }

        for (p in validPath) {

            g.color = p.color
            g.fillRect(p.x * squareSide, p.y * squareSide, squareSide, squareSide)

        }

        for ((y, list) in board.withIndex()) {

            for ((x, _) in list.withIndex()) {

                g.color = Color.BLACK
                g.drawRect(x * squareSide, y * squareSide, squareSide, squareSide)

            }

        }

        g.color = lady.color
        g.fillRect(lady.x * squareSide, lady.y * squareSide, squareSide, squareSide)

        g.color = tv.color
        g.fillRect(tv.x * squareSide, tv.y * squareSide, squareSide, squareSide)

    }

    override fun keyPressed(e: KeyEvent?) {

        if (e == null) {
            return
        }

        when (e.keyCode) {

            KeyEvent.VK_ESCAPE -> System.exit(0)
            KeyEvent.VK_SPACE -> reset()

        }

    }

    override fun keyReleased(e: KeyEvent?) {

    }

    override fun keyTyped(e: KeyEvent?) {

    }

}