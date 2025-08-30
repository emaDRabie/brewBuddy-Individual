package emad.space.brewbuddy.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import emad.space.brewbuddy.R
import emad.space.brewbuddy.databinding.ActivityMainBinding
import emad.space.brewbuddy.onboarding.pref.PreferenceHelper

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host)

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

    private fun getUserDisplayName(): String {
        val name = PreferenceHelper.getUserName(this).toString()
        return name
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}