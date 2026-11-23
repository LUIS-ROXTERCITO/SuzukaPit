package com.luisrocha.suzukapit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luisrocha.suzukapit.databinding.FragmentSettingsBinding
import com.luisrocha.suzukapit.ui.main.MainActivity
import com.luisrocha.suzukapit.util.ThemeManager

/** Pantalla 4: modo oscuro y cierre de sesión. */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Primero se pone el estado guardado y DESPUÉS se registra el listener
        binding.switchDark.isChecked = ThemeManager.isDark(requireContext())
        binding.switchDark.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDark(requireContext(), isChecked)
        }

        binding.btnLogout.setOnClickListener {
            (requireActivity() as MainActivity).confirmLogout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
