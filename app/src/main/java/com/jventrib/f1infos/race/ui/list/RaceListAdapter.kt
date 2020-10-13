package com.jventrib.f1infos.race.ui.list

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import com.jventrib.f1infos.databinding.ItemRaceBinding
import com.jventrib.f1infos.race.model.Race
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceListAdapter internal constructor(
    val context: Context,
    private val listener: (Race) -> Unit
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>()

    inner class RaceViewHolder(binding: ItemRaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val raceNameItemView: TextView = binding.textRaceName
        val raceDateItemView: TextView = binding.textRaceDate
        val flagItemView: ImageView = binding.imageFlag
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val binding = ItemRaceBinding.inflate(inflater, parent, false)
        return RaceViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val current = races.toList()[position]
        holder.raceNameItemView.text = current.raceName
        val raceDT = current.sessions.race
        holder.raceDateItemView.text =
            ZonedDateTime.ofInstant(raceDT, ZoneId.systemDefault()).format(
                customDateTimeFormatter
            )
        holder.raceDateItemView.typeface =
            if (raceDT.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        current.circuit.location.flag?.let {
            val s = "https://www.countryflags.io/$it/flat/64.png"
            Glide.with(context)
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.flagItemView)
        }
        holder.itemView.setOnClickListener { listener(current) }
    }


    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}