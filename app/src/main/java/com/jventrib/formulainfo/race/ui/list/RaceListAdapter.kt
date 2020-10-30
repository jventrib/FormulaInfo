package com.jventrib.formulainfo.race.ui.list

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.jventrib.formulainfo.common.ui.customDateTimeFormatter
import com.jventrib.formulainfo.databinding.ItemRaceBinding
import com.jventrib.formulainfo.race.model.Race
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceListAdapter internal constructor(
    val context: Context,
    private val listener: (Race, ItemRaceBinding) -> Unit
) : RecyclerView.Adapter<RaceListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>()

    inner class ViewHolder(val binding: ItemRaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRaceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = races.toList()[position]
        holder.binding.textRaceName.text = current.raceName
        val raceDT = current.sessions.race
        holder.binding.textRaceDate.text =
            ZonedDateTime.ofInstant(raceDT, ZoneId.systemDefault()).format(
                customDateTimeFormatter
            )
        holder.binding.textRaceDate.typeface =
            if (raceDT.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        current.circuit.location.flag?.let {
            holder.binding.imageFlag.load(it)
        } ?: let {
            holder.binding.imageFlag.clear()
            holder.binding.imageFlag.setImageDrawable(null)
        }
        ViewCompat.setTransitionName(holder.binding.root, "race_card${current.round}")
        ViewCompat.setTransitionName(holder.binding.imageFlag, "race_image_flag${current.round}")
        ViewCompat.setTransitionName(holder.binding.textRaceDate, "text_race_date${current.round}")

        holder.itemView.setOnClickListener { listener(current, holder.binding) }
    }


    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}