package pl.wsei.pam.lab03

import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val tileList: MutableList<Tile> = mutableListOf()

    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_24,
        R.drawable.baseline_star_24,
        R.drawable.baseline_favorite_24,
        R.drawable.baseline_home_24,
        R.drawable.baseline_pets_24,
        R.drawable.baseline_face_24,
        R.drawable.baseline_catching_pokemon_24,
        R.drawable.baseline_flash_on_24,
        R.drawable.baseline_ac_unit_24,
        R.drawable.baseline_bolt_24,
        R.drawable.baseline_local_fire_department_24,
        R.drawable.baseline_anchor_24,
        R.drawable.baseline_alarm_24,
        R.drawable.baseline_beach_access_24,
        R.drawable.baseline_cake_24,
        R.drawable.baseline_directions_car_24,
        R.drawable.baseline_emoji_emotions_24,
        R.drawable.baseline_sports_esports_24
    )

    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = {}
    private val matchedPair: Stack<Tile> = Stack()
    private var logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)
    private var inputLocked: Boolean = false

    init {
        createNewBoard()
    }

    private fun createNewBoard() {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            it.addAll(icons.subList(0, cols * rows / 2))
            it.addAll(icons.subList(0, cols * rows / 2))
            it.shuffle()
        }

        buildBoard(shuffledIcons, IntArray(cols * rows) { -1 })
    }

    private fun buildBoard(resources: List<Int>, state: IntArray) {
        gridLayout.removeAllViews()
        tiles.clear()
        tileList.clear()
        matchedPair.clear()
        inputLocked = false

        gridLayout.columnCount = cols
        gridLayout.rowCount = rows

        logic = MemoryGameLogic(cols * rows / 2)

        var index = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    it.setImageResource(deckResource)
                    it.isSoundEffectsEnabled = false
                    gridLayout.addView(it)
                }

                val tile = addTile(btn, resources[index])

                if (state[index] != -1) {
                    tile.revealed = true
                    tile.removeOnClickListener()
                }

                index++
            }
        }

        logic.restoreMatches(state.count { it != -1 } / 2)
    }

    private fun onClickTile(v: View) {
        if (inputLocked) return

        val tile = tiles[v.tag.toString()] ?: return
        if (tile.revealed) return

        matchedPair.push(tile)

        val matchResult = logic.process {
            tile.tileResource
        }

        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun lockBoard() {
        inputLocked = true
    }

    fun unlockBoard() {
        inputLocked = false
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    private fun addTile(button: ImageButton, resourceImage: Int): Tile {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
        tileList.add(tile)
        return tile
    }

    fun getState(): IntArray {
        return tileList.map {
            if (it.revealed && !it.button.hasOnClickListeners()) it.tileResource else -1
        }.toIntArray()
    }

    fun setState(state: IntArray) {
        val usedIcons = state.filter { it != -1 }.distinct()
        val hiddenPairsCount = state.count { it == -1 } / 2

        val remainingIcons = icons
            .filter { it !in usedIcons }
            .take(hiddenPairsCount)

        val hiddenPool = mutableListOf<Int>().also {
            it.addAll(remainingIcons)
            it.addAll(remainingIcons)
            it.shuffle()
        }

        val resourcesForBoard = mutableListOf<Int>()
        for (i in state.indices) {
            if (state[i] != -1) {
                resourcesForBoard.add(state[i])
            } else {
                resourcesForBoard.add(hiddenPool.removeAt(0))
            }
        }

        buildBoard(resourcesForBoard, state)
    }
}