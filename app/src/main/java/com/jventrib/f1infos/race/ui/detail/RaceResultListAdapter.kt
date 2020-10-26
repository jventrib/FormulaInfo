package com.jventrib.f1infos.race.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.jventrib.f1infos.AppContainer
import com.jventrib.f1infos.common.ui.OffsetCircleCropTransformation
import com.jventrib.f1infos.databinding.ItemRaceResultBinding
import com.jventrib.f1infos.race.model.Race
import com.jventrib.f1infos.race.model.db.RaceResultWithDriver

class RaceResultListAdapter internal constructor(
    private val context: Context,
    private val listener: (Race, ItemRaceResultBinding) -> Unit
) : RecyclerView.Adapter<RaceResultListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var raceResults = emptyList<RaceResultWithDriver>()

    inner class ViewHolder(val binding: ItemRaceResultBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRaceResultBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = raceResults.toList()[position]
        holder.binding.textDriverName.text =
            "${current.raceResult.position}: ${current.driver.givenName} ${current.driver.familyName}"
//        holder.binding.textConstructor.text = current.constructor.name

        current.driver.image?.let {
            holder.binding.imageDriver.load(it) {
                transformations(listOf(OffsetCircleCropTransformation()))
            }

        } ?: let {
            holder.binding.imageDriver.clear()
            holder.binding.imageDriver.setImageDrawable(null)
        }
//        ViewCompat.setTransitionName(holder.binding.root, "race_card${current.round}")
//        ViewCompat.setTransitionName(holder.binding.imageFlag, "race_image_flag${current.round}")
//        ViewCompat.setTransitionName(holder.binding.textRaceDate, "text_race_date${current.round}")

//        holder.itemView.setOnClickListener { listener(current, holder.binding) }
    }


    internal fun setRaceResult(list: List<RaceResultWithDriver>) {
        this.raceResults = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = raceResults.size
}