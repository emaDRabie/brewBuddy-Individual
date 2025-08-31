package emad.space.brewbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.ActivityMainBinding
import emad.space.brewbuddy.onboarding.OnBoardingActivity
import emad.space.brewbuddy.onboarding.pref.PreferenceHelper

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var popup: PopupMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host)

        val iconGroup = findViewById<ImageView>(R.id.icon_group)
        iconGroup.setOnClickListener { view ->
            // If a popup is already present, close it first (acts as a toggle)
            popup?.dismiss()

            val themed = ContextThemeWrapper(view.context, R.style.PopupMenuOverlay)
            val p = PopupMenu(themed, view)
            popup = p

            // Activate the icon while the popup is visible
            iconGroup.isActivated = true

            p.menuInflater.inflate(R.menu.menu_header_dropdown, p.menu)
            p.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile -> {
                        Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.action_settings -> {
                        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.action_help -> {
                        Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show(); true
                    }
                    R.id.action_logout -> {
                        PreferenceHelper.deleteUserName(this)
                        startActivity(
                            Intent(this, OnBoardingActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            // Reset icon when popup closes
            p.setOnDismissListener {
                iconGroup.isActivated = false
                popup = null
            }

            // Force-show icons (AppCompat PopupMenu)
            try {
                val f = p.javaClass.getDeclaredField("mPopup")
                f.isAccessible = true
                val helper = f.get(p)
                val m = helper.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                m.invoke(helper, true)
            } catch (_: Exception) { /* no-op */ }

            p.show()
        }

        // Bottom nav
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemReselectedListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isBottomSheet = destination.id == R.id.coffeeDetailBottomSheet
            binding.bottomNav.isVisible = !isBottomSheet
            binding.header.root.isVisible = !isBottomSheet

            val userName = getUserDisplayName()
            val title = when (destination.id) {
                R.id.homeFragment -> getString(R.string.header_good_day_with_name, userName)
                R.id.menuFragment -> getString(R.string.header_what_drink)
                R.id.ordersFragment -> getString(R.string.header_orders)
                R.id.favoritesFragment -> getString(R.string.header_favorites)
                else -> destination.label ?: getString(R.string.app_name)
            }
            binding.header.tvHeaderTitle.text = title
        }
    }

    private fun getUserDisplayName(): String = PreferenceHelper.getUserName(this).orEmpty()

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}