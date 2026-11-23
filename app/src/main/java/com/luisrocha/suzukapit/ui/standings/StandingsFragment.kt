package com.luisrocha.suzukapit.ui.standings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.remote.F1Api
import com.luisrocha.suzukapit.databinding.FragmentStandingsBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Pantalla 1: clasificación de pilotos consumida desde una API REST. */
class StandingsFragment : Fragment() {

    private var _binding: FragmentStandingsBinding? = null
    private val binding get() = _binding!!
    private val adapter = DriverAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDrivers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDrivers.adapter = adapter
        binding.btnRetry.setOnClickListener { loadStandings() }
        loadStandings()
    }

    private fun loadStandings() {
        binding.progress.visibility = View.VISIBLE
        binding.errorGroup.visibility = View.GONE
        binding.rvDrivers.visibility = View.GONE

        // lifecycleScope se cancela solo cuando la vista se destruye
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val drivers = F1Api.getDriverStandings()
                binding.progress.visibility = View.GONE
                if (drivers.isEmpty()) {
                    showError(getString(R.string.standings_empty))
                } else {
                    adapter.submitList(drivers)
                    binding.rvDrivers.visibility = View.VISIBLE
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                showError(getString(R.string.standings_error))
            }
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.errorGroup.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // evita fugas de memoria
    }
}
