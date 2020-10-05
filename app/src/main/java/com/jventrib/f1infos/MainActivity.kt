package com.jventrib.f1infos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

/*
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
        })
*/
    }
}