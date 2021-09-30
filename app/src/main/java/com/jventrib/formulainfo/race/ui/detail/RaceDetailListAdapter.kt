package com.jventrib.formulainfo.race.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.facedetection.CenterOnFaceTransformation
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.common.ui.loadImage
import com.jventrib.formulainfo.databinding.ItemRaceResultBinding
import com.jventrib.formulainfo.race.model.db.FullRaceResult
import logcat.logcat

class RaceResultListAdapter : RecyclerView.Adapter<RaceResultListAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var raceResults = emptyList<FullRaceResult>()

    override fun getItemCount() = raceResults.size

    inner class ViewHolder(val binding: ItemRaceResultBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            ItemRaceResultBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = raceResults.toList()[position]
        holder.binding.textPosition.text = "${current.raceResult.position}:"
        holder.binding.textDriverName.text =
            "${current.driver?.givenName} ${current.driver?.familyName}"
        holder.binding.textConstructor.text = current.constructor?.name
        holder.binding.textDriverPoints.text = "${current.raceResult.points.fmt()} pts"
        holder.binding.textDriverGrid.text = "Started " + (current.raceResult.grid).toString()
        val positionGain = current.raceResult.grid - current.raceResult.position
        val positionGainString = (when {
            positionGain > 0 -> "+$positionGain"
            positionGain < 0 -> positionGain
            else -> "-"
        })
        val positionGainColor =
            when {
                positionGain > 0 -> Color.GREEN
                positionGain < 0 -> Color.RED
                else -> Color.BLACK
            }
        holder.binding.textDriverGainLoss.text = "($positionGainString)"
        holder.binding.textDriverGainLoss.setTextColor(positionGainColor)
        current.raceResult.time?.let { holder.binding.textTime.text = it.time }

//        if (current.constructor.image != null && current.constructor.image != "NONE") {
//            holder.binding.textConstructor.loadBackground(current.constructor.image)
//        } else
//            holder.binding.textConstructor.background = null

        val colorId = context.resources.getIdentifier(
            current.constructor?.id,
            "color",
            context.packageName
        )

        try {
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(colorId, null)
            } else {
                @Suppress("DEPRECATION")
                context.resources.getColor(colorId)
            }
            holder.binding.spaceConstructorColor.setBackgroundColor(color)
        } catch (e: Resources.NotFoundException) {
            val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.resources.getColor(R.color.light_grey, null)
            } else {
                @Suppress("DEPRECATION")
                context.resources.getColor(R.color.light_grey)
            }
            holder.binding.spaceConstructorColor.setBackgroundColor(color)
            logcat {"Constructor ${current.constructor?.id} color resource not found"}

        }

        current.driver?.image?.let {
            holder.binding.imageDriver.loadImage(it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    transformations(
                        listOf(
                            CenterOnFaceTransformation(zoom = 80),
                            CircleCropTransformation()
                        )
                    )
                }
            }
        } ?: let {
            holder.binding.imageDriver.clear()
            holder.binding.imageDriver.setImageDrawable(null)
        }
//        ViewCompat.setTransitionName(holder.binding.root, "race_card${current.raceResult.round}")
//        ViewCompat.setTransitionName(holder.binding.imageFlag, "race_image_flag${current.round}")
//        ViewCompat.setTransitionName(holder.binding.textRaceDate, "text_race_date${current.round}")

//        holder.itemView.setOnClickListener { listener(current, holder.binding) }
    }


    internal fun setRaceResult(list: List<FullRaceResult>) {
        this.raceResults = list
        notifyDataSetChanged()
    }


    private fun Float.fmt(): String {
        if (this == toLong().toFloat())
            return String.format("%d", toLong())
        else
            return String.format("%s", this)
    }

}

