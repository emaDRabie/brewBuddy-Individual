package emad.space.brewbuddy.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import emad.space.brewbuddy.R
import emad.space.brewbuddy.ui.MainActivity
import emad.space.brewbuddy.onboarding.pref.PreferenceHelper

class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (PreferenceHelper.getUserName(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_on_boarding)

    }
}