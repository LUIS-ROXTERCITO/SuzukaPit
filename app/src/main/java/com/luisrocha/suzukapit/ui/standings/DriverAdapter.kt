package com.luisrocha.suzukapit.ui.standings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.model.Driver
import com.luisrocha.suzukapit.databinding.ItemDriverBinding

/** Adaptador propio del RecyclerView de pilotos (usa ViewBinding en cada fila). */
class DriverAdapter : ListAdapter<Driver, DriverAdapter.DriverViewHolder>(DIFF) {

    class DriverViewHolder(val binding: ItemDriverBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverViewHolder {
        val binding = ItemDriverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DriverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DriverViewHolder, position: Int) {
        val driver = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvPosition.text = driver.position.toString()
            tvName.text = driver.name
            tvTeam.text = ctx.getString(R.string.driver_team_nationality, driver.team, driver.nationality)
            tvPoints.text = ctx.getString(R.string.driver_points, driver.points)
            tvWins.text = ctx.getString(R.string.driver_wins, driver.wins)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Driver>() {
            override fun areItemsTheSame(old: Driver, new: Driver) = old.name == new.name
            override fun areContentsTheSame(old: Driver, new: Driver) = old == new
        }
    }
}
