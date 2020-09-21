package com.jventrib.f1infos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dropbox.android.external.store4.StoreResponse
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
        raceViewModel.allRaces.observe(this, { response ->
            when (response) {
                is StoreResponse.Data -> {
                    Log.d(this.localClassName, "Resource.Status.SUCCESS: ${response.value}")
//                    progress_bar.visibility = View.GONE
                    adapter.setRaces(response.value)
                }
                is StoreResponse.Error.Exception -> {
                    Log.e(this.localClassName, "Error: ${response.errorMessageOrNull()}", response.error)
                    Toast.makeText(this, response.errorMessageOrNull(), Toast.LENGTH_SHORT).show()
                }
                is StoreResponse.Loading ->
                    Log.d(this.localClassName, "Resource.Status.LOADING")
//                    progress_bar.visibility = View.VISIBLE
            }

//            races?.let { adapter.setRaces(it.data!!) }
        })
    }
}