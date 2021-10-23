package fi.lauriari.helsinkiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.fragments.BrowseFragment
import fi.lauriari.helsinkiapp.fragments.FavoritesFragment
import fi.lauriari.helsinkiapp.fragments.SearchFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.near_me -> {
                    val currentFrag =
                        supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    if (currentFrag !is BrowseFragment) {
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<BrowseFragment>(R.id.fragmentContainerView)
                        }
                    }

                }
                R.id.search -> {
                    val currentFrag =
                        supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    if (currentFrag !is SearchFragment) {
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<SearchFragment>(R.id.fragmentContainerView)
                        }
                    }

                }
                R.id.favorites -> {
                    val currentFrag =
                        supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    if (currentFrag !is FavoritesFragment) {
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace<FavoritesFragment>(R.id.fragmentContainerView)
                        }
                    }
                }

            }
            true
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
