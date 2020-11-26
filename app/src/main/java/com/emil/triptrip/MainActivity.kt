package com.emil.triptrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.emil.triptrip.databinding.ActivityMainBinding
import com.emil.triptrip.databinding.NavHeaderDrawerBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        setupDrawer()
    }




    // Set up Drawer
    private fun setupDrawer() {

        //set up toolbar
        val navController = this.findNavController(R.id.navHostFragment)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = null

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.drawerNavView, navController)

        binding.drawerLayout.fitsSystemWindows = true
        binding.drawerLayout.clipToPadding = false

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

        }.apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
        }

        // Setup header of drawer ui with data binding with user profile
        val bindingNavHeader = NavHeaderDrawerBinding.inflate(
                LayoutInflater.from(this), binding.drawerNavView, false)
        bindingNavHeader.lifecycleOwner = this
        binding.drawerNavView.addHeaderView(bindingNavHeader.root)

        // set toolbar drawer icon
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_format_list_bulleted_24)



    }
}


