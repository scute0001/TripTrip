package com.emil.triptrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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
import com.emil.triptrip.ui.login.UserManager
import com.emil.triptrip.util.Util
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

private const val IS_READ = 1
private const val NOT_READ = 0

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var exitFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

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
                    val arrayList = viewModel.navToNotificationList.value?.toTypedArray()
                    //navigation to notification page here
                    arrayList?.let { notificationArray ->
                        findNavController(R.id.navHostFragment).navigate(NavigationDirections.actionGlobalNotificationFragment(notificationArray))
                    }
                }
                R.id.message -> {
                    viewModel.selectTripId.value?.let {tripId->
                        findNavController(R.id.navHostFragment).navigate(NavigationDirections.actionGlobalChatRoomFragment(tripId))
                    }
                }
                R.id.setting -> viewModel.navToModifyTripFlag.value = true
            }
            false
        }

        // set current user and set snapshot for get notification
        UserManager.user.observe(this, Observer {
            viewModel.setCurrentUser(it)
        })

        viewModel.notificationList.observe(this, Observer {notificationList ->
            val list = notificationList.filter { it.status == NOT_READ }.sortedBy { it.createTime }
            viewModel.navToNotificationList.value = list
            Log.d("Firebase", "main notification list is $list")
            if (list.size == 0) {
                binding.toolbar.menu.findItem(R.id.notification).setIcon(R.drawable.ic_baseline_notifications_active_24)
            } else {
                binding.toolbar.menu.findItem(R.id.notification).setIcon(R.drawable.ic_notification_important_24)
            }
        })

        setupDrawer()
        setupNavController()
    }

    // Set up Drawer
    private fun setupDrawer() {
        //set up toolbar
        val navController = this.findNavController(R.id.navHostFragment)

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.drawerNavView, navController)

        binding.drawerNavView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_my_notifications -> {
                    val arrayList = viewModel.navToNotificationList.value?.toTypedArray()
                    //navigation to notification page here
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    findNavController(R.id.navHostFragment).navigate(NavigationDirections.actionGlobalNotificationFragment(arrayList!!))
                    true
                }
                else -> false
            }
        }

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


    //  Set up [NavController.addOnDestinationChangedListener] to record the current fragment
    private fun setupNavController() {
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.addTripFragment -> resources.getString(R.string.add_new_trip)
                R.id.addSpotFragment -> resources.getString(R.string.add_new_spot)
                R.id.chatRoomFragment -> viewModel.currentFragmentType.value
                R.id.tripDetailFragment -> viewModel.currentFragmentType.value
                R.id.modifyTripFragment -> viewModel.currentFragmentType.value
                else -> resources.getString(R.string.app_name)
            }

            viewModel.isHome.value = navController.currentDestination?.id == R.id.myTripsFragment

            if (navController.currentDestination?.id == R.id.tripDetailFragment){
                binding.toolbar.menu.findItem(R.id.message).isVisible = true
                binding.toolbar.menu.findItem(R.id.setting).isVisible = true
                binding.toolbar.menu.findItem(R.id.notification).isVisible = false

            } else if(navController.currentDestination?.id == R.id.modifyTripFragment){
                binding.toolbar.menu.findItem(R.id.message).isVisible = false
                binding.toolbar.menu.findItem(R.id.setting).isVisible = false
                binding.toolbar.menu.findItem(R.id.notification).isVisible = false
            } else {
                binding.toolbar.menu.findItem(R.id.message).isVisible = false
                binding.toolbar.menu.findItem(R.id.setting).isVisible = false
                binding.toolbar.menu.findItem(R.id.notification).isVisible = true
            }
        }
    }

    override fun onBackPressed() {
        val backStackCount = fragmentManager.backStackEntryCount
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if ( exitFlag == true ) {
                super.onBackPressed()
            } else {
                if (viewModel.isHome.value == true) {
                    exitFlag = true
                    Toast.makeText(this, Util.getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({
                        exitFlag = false
                    }, 2000)
                } else {
                    super.onBackPressed()
                }
            }
        }
    }
}


