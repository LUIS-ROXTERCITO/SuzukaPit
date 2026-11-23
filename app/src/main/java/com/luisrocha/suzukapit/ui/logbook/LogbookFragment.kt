package com.luisrocha.suzukapit.ui.logbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.local.LogDbHelper
import com.luisrocha.suzukapit.data.local.LogEntry
import com.luisrocha.suzukapit.databinding.FragmentLogbookBinding

/** Pantalla 2: lista de la bitácora (READ) con acciones de editar y eliminar (UPDATE/DELETE). */
class LogbookFragment : Fragment() {

    private var _binding: FragmentLogbookBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: LogDbHelper
    private lateinit var adapter: LogEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = LogDbHelper(requireContext().applicationContext)

        adapter = LogEntryAdapter(
            onEdit = { entry -> openForm(entry.id) },
            onDelete = { entry -> confirmDelete(entry) }
        )
        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.adapter = adapter
        binding.fabAdd.setOnClickListener { openForm(-1L) }
    }

    // Se recarga al volver del formulario para ver los cambios
    override fun onResume() {
        super.onResume()
        loadEntries()
    }

    private fun loadEntries() {
        val entries = db.getAll()
        adapter.submitList(entries)
        binding.tvEmpty.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openForm(entryId: Long) {
        findNavController().navigate(
            R.id.action_logbook_to_form,
            bundleOf("entryId" to entryId)
        )
    }

    private fun confirmDelete(entry: LogEntry) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_title)
            .setMessage(getString(R.string.delete_message, entry.title))
            .setPositiveButton(R.string.entry_delete) { _, _ ->
                db.delete(entry.id)
                loadEntries()
                Toast.makeText(requireContext(), R.string.entry_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
