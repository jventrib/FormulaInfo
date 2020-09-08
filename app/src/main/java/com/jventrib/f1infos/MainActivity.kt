package com.jventrib.f1infos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jventrib.f1infos.race.ui.RaceListAdapter
import com.jventrib.f1infos.race.ui.RaceViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var raceViewModel: RaceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RaceListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        raceViewModel = ViewModelProvider(this).get(RaceViewModel::class.java)

        raceViewModel.allRaces.observe(this, Observer { races ->
            // Update the cached copy of the words in the adapter.
            races?.let { adapter.setRaces(it) }
        })
    }
}