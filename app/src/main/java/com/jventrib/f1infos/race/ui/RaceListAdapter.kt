package com.jventrib.f1infos.race.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.R
import com.jventrib.f1infos.race.model.Race
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RaceListAdapter internal constructor(
    val context: Context
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>()

    inner class RaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raceNameItemView: TextView = itemView.findViewById(R.id.nameTextView)
        val raceDateItemView: TextView = itemView.findViewById(R.id.dateTextView)
        val flagItemView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return RaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val current = races.toList()[position]
        holder.raceNameItemView.text = current.raceName
        current.datetime?.let {
            holder.raceDateItemView.text =
                ZonedDateTime.ofInstant(it, ZoneId.systemDefault()).format(
                    customDateTimeFormatter
                )
            holder.raceDateItemView.typeface =
                if (it.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
        current.circuit.location.flag?.let {
            Glide
                .with(context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.flagItemView)
        }
    }

    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}