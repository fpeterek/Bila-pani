package bilaPani

import java.awt.Dimension
import javax.swing.JFrame

class Window(board: Board, width: Int, height: Int): JFrame("Bílá paní") {

    init {

        add(board)

        addKeyListener(board)

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        size = Dimension(width, height)
        isResizable = false
        isVisible = true

    }

}