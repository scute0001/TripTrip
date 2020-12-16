package com.emil.triptrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.emil.triptrip.databinding.ActivityMainBinding
import com.emil.triptrip.databinding.NavHeaderDrawerBinding
import com.emil.triptrip.ui.login.LoginViewModelFactory
import com.emil.triptrip.ui.login.UserManager

private const val IS_READ = 1
private const val NOT_READ = 0

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        val app = this.application
        val repository = (this.applicationContext as TripTripApplication).repository
        val viewModelFactory = MainActivityViewModelFactory(app, repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.viewModel = viewModel



        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.notification -> {
                    //navigation to notification page here

//                    viewModel.clickStatu.value?.let {
//                        viewModel.clickStatu.value = !it
//                    }
                }
            }
            false
        }

        // set current user and set snapshot for get notification
        UserManager.user.observe(this, Observer {
            viewModel.setCurrentUser(it)
        })

        viewModel.notificationList.observe(this, Observer {notificationList ->
            val list = notificationList.filter { it.status == NOT_READ }
            Log.d("TTTT", "main viewmodel $list")
            if (list.size == 0) {
                binding.toolbar.menu.findItem(R.id.notification).setIcon(R.drawable.ic_baseline_notifications_active_24)
            } else {
                binding.toolbar.menu.findItem(R.id.notification).setIcon(R.drawable.ic_baseline_notification_important_24)
            }
        })


        setupDrawer()
        setupNavController()
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
        bindingNavHeader.viewModel = viewModel
        binding.drawerNavView.addHeaderView(bindingNavHeader.root)






        // set toolbar drawer icon
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_format_list_bulleted_24)



    }


    /**
     * Set up [NavController.addOnDestinationChangedListener] to record the current fragment, it better than another design
     * which is change the [CurrentFragmentType] enum value by [MainViewModel] at [onCreateView]
     */
    private fun setupNavController() {
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.addTripFragment -> resources.getString(R.string.add_new_trip)
                R.id.addSpotFragment -> resources.getString(R.string.add_new_spot)
                else -> resources.getString(R.string.app_name)
            }

//            if (navDestination.id != R.id.tripDetailFragment)
//                viewModel.currentFragmentType.value = "TripTrip"
        }
    }
}


