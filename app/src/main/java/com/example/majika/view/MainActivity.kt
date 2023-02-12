package com.example.majika.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.majika.R
import com.example.majika.databinding.ActivityMainBinding

const val CLICK = "ButtonTest"
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController
//
////        setupActionBarWithNavController((navController))
//        val navMenu: ImageView = findViewById(R.id.nav_menu)
//        navMenu.setOnClickListener {
//            Log.d(CLICK, "Menu")
//            navController.navigate(R.id.menuFragment)
//        }

        binding.navbarBottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_twibbon -> switchFragment(TwibbonFragment())
                R.id.nav_menu -> switchFragment(MenuFragment())
                R.id.nav_cart -> switchFragment(CartFragment())

                else -> {

                }


            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}