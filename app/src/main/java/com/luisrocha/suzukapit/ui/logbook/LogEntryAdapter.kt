package com.luisrocha.suzukapit.ui.logbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.local.LogEntry
import com.luisrocha.suzukapit.databinding.ItemEntryBinding

/** Adaptador propio de la bitácora. Avisa por lambdas cuando se edita o elimina una fila. */
class LogEntryAdapter(
    private val onEdit: (LogEntry) -> Unit,
    private val onDelete: (LogEntry) -> Unit
) : ListAdapter<LogEntry, LogEntryAdapter.EntryViewHolder>(DIFF) {

    class EntryViewHolder(val binding: ItemEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val binding = ItemEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = getItem(position)
        val ctx = holder.itemView.context
        with(holder.binding) {
            tvTitle.text = entry.title
            tvCircuit.text = ctx.getString(R.string.entry_circuit_date, entry.circuit, entry.date)
            tvDriver.text = ctx.getString(R.string.entry_driver, entry.driver)
            tvRating.text = "★".repeat(entry.rating) + "☆".repeat(5 - entry.rating)
            tvNotes.text = entry.notes
            btnEdit.setOnClickListener { onEdit(entry) }
            btnDelete.setOnClickListener { onDelete(entry) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<LogEntry>() {
            override fun areItemsTheSame(old: LogEntry, new: LogEntry) = old.id == new.id
            override fun areContentsTheSame(old: LogEntry, new: LogEntry) = old == new
        }
    }
}
