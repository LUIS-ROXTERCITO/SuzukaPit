package com.luisrocha.suzukapit.ui.logbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.Circuits
import com.luisrocha.suzukapit.data.local.LogDbHelper
import com.luisrocha.suzukapit.data.local.LogEntry
import com.luisrocha.suzukapit.databinding.FragmentEntryFormBinding
import com.luisrocha.suzukapit.util.Validators
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EntryFormFragment : Fragment() {

    private var _binding: FragmentEntryFormBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: LogDbHelper
    private var entryId: Long = -1L
    private var existing: LogEntry? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = LogDbHelper(requireContext().applicationContext)
        entryId = arguments?.getLong("entryId", -1L) ?: -1L

        binding.actCircuit.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, Circuits.japanese)
        )

        if (entryId != -1L) {
            existing = db.getById(entryId)
            existing?.let { fillForm(it) }
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                getString(R.string.form_title_edit)
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun fillForm(entry: LogEntry) {
        binding.etTitle.setText(entry.title)
        binding.actCircuit.setText(entry.circuit, false)
        binding.etDriver.setText(entry.driver)
        binding.etNotes.setText(entry.notes)
        binding.ratingBar.rating = entry.rating.toFloat()
    }

    private fun save() {
        val title = binding.etTitle.text.toString()
        val circuit = binding.actCircuit.text.toString()
        val driver = binding.etDriver.text.toString()
        val notes = binding.etNotes.text.toString()
        val rating = binding.ratingBar.rating.toInt()

        // Cada validador regresa el id del error o null; se muestra bajo cada campo
        val titleError = Validators.title(title)
        val circuitError = Validators.circuit(circuit)
        val driverError = Validators.driver(driver)
        val notesError = Validators.notes(notes)
        val ratingError = Validators.rating(rating)

        binding.tilTitle.error = titleError?.let { getString(it) }
        binding.tilCircuit.error = circuitError?.let { getString(it) }
        binding.tilDriver.error = driverError?.let { getString(it) }
        binding.tilNotes.error = notesError?.let { getString(it) }
        binding.tvRatingError.apply {
            text = ratingError?.let { getString(it) }
            visibility = if (ratingError != null) View.VISIBLE else View.GONE
        }

        if (listOf(titleError, circuitError, driverError, notesError, ratingError).any { it != null }) {
            return // hay errores: no se guarda nada
        }

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val entry = LogEntry(
            id = existing?.id ?: 0,
            title = title.trim(),
            circuit = circuit,
            driver = driver.trim(),
            notes = notes.trim(),
            rating = rating,
            date = existing?.date ?: today
        )

        if (existing == null) {
            db.insert(entry)
            Toast.makeText(requireContext(), R.string.entry_saved, Toast.LENGTH_SHORT).show()
        } else {
            db.update(entry)
            Toast.makeText(requireContext(), R.string.entry_updated, Toast.LENGTH_SHORT).show()
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
