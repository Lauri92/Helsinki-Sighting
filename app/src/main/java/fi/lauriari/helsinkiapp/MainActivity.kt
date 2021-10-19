package fi.lauriari.helsinkiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.browse -> Toast.makeText(this, "Browse", Toast.LENGTH_SHORT).show()
                R.id.search -> Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
                R.id.favorites -> Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()

            }
            true
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
