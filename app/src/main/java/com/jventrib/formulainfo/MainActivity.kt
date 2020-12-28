package com.jventrib.formulainfo

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.jventrib.formulainfo.about.AboutFragment
import com.jventrib.formulainfo.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalStoreApi
@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

//    private lateinit var spinner: Spinner

//    private lateinit var navController: NavController


//    private val viewModel: MainViewModel by viewModels {
//        (application as Application).appContainer.getViewModelFactory(::MainViewModel)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        navController = getNavController()
//        supportActionBar?.setHomeButtonEnabled(true)

    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu, menu)
//
//        val item: MenuItem = menu.findItem(R.id.spinner)
//        spinner = item.actionView as Spinner
//        spinner.adapter = ArrayAdapter(
//            applicationContext, R.layout.spinner_season, seasonList
//        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
//        spinner.onItemSelectedListener = this
//        return true
//    }




//    private fun getNavController() =
//        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
}