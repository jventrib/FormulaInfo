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
        raceViewModel.allRaces.observe(this, {
            when (it) {
                is StoreResponse.Data -> {
                    Log.d(this.localClassName, "Resource.Status.SUCCESS")
//                    progress_bar.visibility = View.GONE
                    if (!it.value.isNullOrEmpty()) adapter.setRaces(ArrayList(it.value))
                }
                is StoreResponse.Error ->
                    Toast.makeText(this, it.errorMessageOrNull(), Toast.LENGTH_SHORT).show()

                is StoreResponse.Loading ->
                    Log.d(this.localClassName, "Resource.Status.LOADING")
//                    progress_bar.visibility = View.VISIBLE
            }

//            races?.let { adapter.setRaces(it.data!!) }
        })
    }
}