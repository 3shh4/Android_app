package pl.wsei.pam.lab03

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import java.util.Random

class   Lab03Activity : AppCompatActivity() {

    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView

    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePlayer: MediaPlayer
    private var isSound: Boolean = true

    private var rows: Int = 2
    private var columns: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)
        supportActionBar?.title = "Lab03"

        mBoard = findViewById(R.id.boardGrid)

        rows = intent.getIntExtra("rows", 2)
        columns = intent.getIntExtra("columns", 2)

        mBoardModel = MemoryBoardView(mBoard, columns, rows)

        mBoardModel.setOnGameChangeListener { e ->
            when (e.state) {
                GameStates.Matching -> {
                    e.tiles.forEach { it.revealed = true }
                }

                GameStates.Match -> {
                    e.tiles.forEach { it.revealed = true }

                    mBoardModel.lockBoard()

                    val b1 = e.tiles[0].button
                    val b2 = e.tiles[1].button

                    if (isSound) completionPlayer.start()

                    animatePairedButtons(b1, b2) {
                        e.tiles.forEach { it.removeOnClickListener() }
                        mBoardModel.unlockBoard()
                    }
                }

                GameStates.NoMatch -> {
                    e.tiles.forEach { it.revealed = true }

                    mBoardModel.lockBoard()

                    val b1 = e.tiles[0].button
                    val b2 = e.tiles[1].button

                    if (isSound) negativePlayer.start()

                    animateWrongPair(b1, b2) {
                        e.tiles.forEach { it.revealed = false }
                        mBoardModel.unlockBoard()
                    }
                }

                GameStates.Finished -> {
                    e.tiles.forEach {
                        it.revealed = true
                        it.removeOnClickListener()
                    }

                    Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (savedInstanceState != null) {
            val state = savedInstanceState.getIntArray("board_state")
            if (state != null) {
                mBoardModel.setState(state)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("board_state", mBoardModel.getState())
        outState.putInt("rows", rows)
        outState.putInt("columns", columns)
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        if (::completionPlayer.isInitialized) {
            completionPlayer.release()
        }
        if (::negativePlayer.isInitialized) {
            negativePlayer.release()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.board_activity_sound -> {
                isSound = !isSound

                if (isSound) {
                    Toast.makeText(this, getString(R.string.sound_on), Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_up_24)
                } else {
                    Toast.makeText(this, getString(R.string.sound_off), Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_off_24)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun animatePairedButtons(
        firstButton: ImageButton,
        secondButton: ImageButton,
        action: () -> Unit
    ) {
        val random = Random()

        fun createSet(button: ImageButton): AnimatorSet {
            button.pivotX = random.nextFloat() * button.width.coerceAtLeast(1)
            button.pivotY = random.nextFloat() * button.height.coerceAtLeast(1)

            val rotation = ObjectAnimator.ofFloat(button, "rotation", 0f, 1080f)
            val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.6f, 0f)
            val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.6f, 0f)
            val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

            return AnimatorSet().apply {
                duration = 600
                interpolator = DecelerateInterpolator()
                playTogether(rotation, scaleX, scaleY, fade)
            }
        }

        val firstSet = createSet(firstButton)
        val secondSet = createSet(secondButton)

        AnimatorSet().apply {
            playTogether(firstSet, secondSet)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    firstButton.scaleX = 1f
                    firstButton.scaleY = 1f
                    firstButton.alpha = 0f
                    firstButton.rotation = 0f

                    secondButton.scaleX = 1f
                    secondButton.scaleY = 1f
                    secondButton.alpha = 0f
                    secondButton.rotation = 0f

                    action()
                }
            })
            start()
        }
    }

    private fun animateWrongPair(
        firstButton: ImageButton,
        secondButton: ImageButton,
        action: () -> Unit
    ) {
        fun createShake(button: ImageButton): AnimatorSet {
            val rotateLeft = ObjectAnimator.ofFloat(button, "rotation", 0f, -12f)
            val rotateRight = ObjectAnimator.ofFloat(button, "rotation", -12f, 12f)
            val rotateCenter = ObjectAnimator.ofFloat(button, "rotation", 12f, -8f, 8f, 0f)

            return AnimatorSet().apply {
                duration = 350
                playSequentially(rotateLeft, rotateRight, rotateCenter)
            }
        }

        val firstSet = createShake(firstButton)
        val secondSet = createShake(secondButton)

        AnimatorSet().apply {
            playTogether(firstSet, secondSet)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    firstButton.rotation = 0f
                    secondButton.rotation = 0f
                    action()
                }
            })
            start()
        }
    }
}