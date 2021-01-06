package com.jventrib.formulainfo.race.ui.list

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.jventrib.formulainfo.common.ui.customDateTimeFormatter
import com.jventrib.formulainfo.databinding.ItemRaceBinding
import com.jventrib.formulainfo.race.model.db.RaceFull
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceListListAdapter internal constructor(
    private val listener: (RaceFull, ItemRaceBinding) -> Unit
) : RecyclerView.Adapter<RaceListListAdapter.ViewHolder>() {

    internal var races = emptyList<RaceFull>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = races.size

    inner class ViewHolder(val binding: ItemRaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = races.toList()[position]
        holder.binding.textRaceName.text = current.race.raceName
        val raceDT = current.race.sessions.race
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

        ViewCompat.setTransitionName(holder.binding.root, "race_card${current.race.round}")

        holder.itemView.setOnClickListener { listener(current, holder.binding) }
    }

}