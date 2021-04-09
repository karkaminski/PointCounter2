package pl.karwojcik.pointcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    //klucze do zapisu danych w onSaveInstanceState. podczas obrotu ekranu
    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    //TAG do logów
    private val TAG = "MainActivity"

    //obiekty widoków
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var tapMeButton: Button

    //licznik punkow
    private var score = 0

    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 20000
    private var countDownInterval: Long = 1000
    private var timeLeft = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is $score ")

        //przypisanie kontrolek xml do obiektów widoków
        gameScoreTextView = findViewById(R.id.text_view_score)
        timeLeftTextView = findViewById(R.id.text_view_time)
        tapMeButton = findViewById(R.id.tap_me_button)

        //przypisanie działania do przycisku tabMeButton
        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        //sprawdzenie czy wystepuje savedInstaceState. Jeżeli tak to znaczy ze activity "odradza sie"
        //po obrocie urządzenia i wtedy score i timeLeft wczytywane są z savedInstaceState
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }
    }

    ///Metody Activity lifecycle z logowaniem. Mozna zaobserwowac w jakie stany przechodzi aplikacja podczas obracania urządzeniem
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    //koniec metdod Activity lifecycle

    //metoda zapisujaca stan activity podczas obrotu
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving score: $score and TimeLeft $timeLeft")
    }

    //zwiększenie ilosci punktow o 1. jezeli gra nie jest wystartowana, to start gry
    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }
        score++

        val newScore = getString(R.string.your_score, score)
        gameScoreTextView.text = newScore
    }

    //przywracanie stanu gry
    private fun restoreGame() {
        val restoredScore = getString(R.string.your_score, score)
        gameScoreTextView.text = restoredScore

        val restoredTime = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = restoredTime

        countDownTimer = object : CountDownTimer(
                (timeLeft * 1000).toLong(),
                countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true

    }

    //resetowanie gry, reset puntów, timera
    private fun resetGame() {
        score = 0
        timeLeft = 20
        val initialScore = getString(R.string.your_score, score)
        gameScoreTextView.text = initialScore

        val initialTimeLeft = getString(R.string.time_left, timeLeft)
        timeLeftTextView.text = initialTimeLeft

        gameStarted = false

        countDownTimer = object : CountDownTimer(
                initialCountDown,
                countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_left, timeLeft)
                timeLeftTextView.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }
    }


    //start gry, start timera
    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    //koniec gry, wyswietlenie komunikatu z punktami
    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over_message, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}