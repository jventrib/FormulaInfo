package com.jventrib.f1infos.race.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jventrib.f1infos.R
import com.jventrib.f1infos.race.model.Race

class RaceListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>() // Cached copy of words

    inner class RaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raceItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return RaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val current = races[position]
        holder.raceItemView.text = current.raceName
    }

    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}