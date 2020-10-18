package com.jventrib.f1infos.race.ui.list

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
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
    private val listener: (Race, ItemRaceBinding) -> Unit
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>()

    inner class RaceViewHolder(val binding: ItemRaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val binding = ItemRaceBinding.inflate(inflater, parent, false)
        return RaceViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
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
            val s = "https://www.countryflags.io/$it/flat/64.png"
            Glide.with(context)
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.binding.imageFlag)
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