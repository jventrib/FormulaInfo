package com.jventrib.f1infos.race.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jventrib.f1infos.R
import com.jventrib.f1infos.race.model.Race
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RaceListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>() // Cached copy of words

    inner class RaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raceNameItemView: TextView = itemView.findViewById(R.id.nameTextView)
        val raceDateItemView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return RaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val current = races[position]
        holder.raceNameItemView.text = current.raceName
        holder.raceDateItemView.text =
            ZonedDateTime.ofInstant(current.datetime, ZoneId.systemDefault()).format(
                DateTimeFormatter.ISO_DATE_TIME
            )
    }

    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}