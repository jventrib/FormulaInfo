package com.jventrib.formulainfo

import android.os.Bundle
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
import com.jventrib.formulainfo.about.AboutFragment
import com.jventrib.formulainfo.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var spinner: Spinner
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val seasonList = (1950..2020).toList().reversed()

    private val viewModel: MainViewModel by viewModels {
        (application as Application).appContainer.getViewModelFactory(::MainViewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        navController = getNavController()
        binding.myToolbar.setupWithNavController(navController)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val item: MenuItem = menu.findItem(R.id.spinner)
        spinner = item.actionView as Spinner
        spinner.adapter = ArrayAdapter(
            applicationContext, R.layout.spinner_season, seasonList
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinner.onItemSelectedListener = this
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_action_about) {
            val aboutDestination = getNavDestination<AboutFragment>()
            if (navController.currentDestination != aboutDestination) {
                navController.navigate(
                    NavGraphDirections.actionGlobalAboutFragment()
                )
            }

            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setSeason(seasonList[position])
        @Suppress("ControlFlowWithEmptyBody")
        while (navController.navigateUp()) {
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private inline fun <reified T> getNavDestination() =
        navController.graph.first { (it as FragmentNavigator.Destination).className == T::class.java.name }

    private fun getNavController() =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
}