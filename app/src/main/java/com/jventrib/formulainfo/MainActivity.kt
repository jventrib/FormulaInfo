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
import androidx.navigation.findNavController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    private val seasonList = (1950..2020).toList().reversed()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val item: MenuItem = menu!!.findItem(R.id.spinner)
        val spinner = item.actionView as Spinner
        spinner.adapter = ArrayAdapter(
            applicationContext, R.layout.spinner_season, seasonList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val application = application as Application
        val viewModel: MainViewModel by viewModels {
            application.appContainer.getViewModelFactory(::MainViewModel)
        }
        viewModel.setSeason(2018)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setSeason(seasonList[position])
                val findNavController = findNavController(R.id.nav_host_fragment)
                findNavController.navigateUp()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        return true
    }
}