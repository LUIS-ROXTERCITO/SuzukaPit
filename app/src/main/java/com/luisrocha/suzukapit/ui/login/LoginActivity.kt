package com.luisrocha.suzukapit.ui.login

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.databinding.ActivityLoginBinding
import com.luisrocha.suzukapit.ui.main.MainActivity
import com.luisrocha.suzukapit.util.SessionManager

/**
 * PRIMERA PANTALLA de la app (launcher). Autentica con huella usando BiometricPrompt
 * y contempla: sin sensor, sin huella registrada, huella no reconocida,
 * cancelación del usuario y bloqueo por intentos fallidos.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBiometricPrompt()
        binding.btnLogin.setOnClickListener { checkAndAuthenticate() }
        binding.btnEnroll.setOnClickListener { openEnrollment() }

        // Al abrir la app se lanza el lector de huella automáticamente
        if (savedInstanceState == null) checkAndAuthenticate()
    }

    /** Crea el BiometricPrompt con los callbacks de éxito, fallo y error. */
    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    SessionManager.login()
                    goToMain()
                }

                // La huella se leyó pero no coincide (el diálogo sigue abierto)
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showStatus(getString(R.string.login_not_recognized))
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val message = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED ->
                            getString(R.string.login_cancelled)
                        BiometricPrompt.ERROR_LOCKOUT ->
                            getString(R.string.login_lockout)
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            getString(R.string.login_lockout_permanent)
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            binding.btnEnroll.visibility = View.VISIBLE
                            getString(R.string.login_none_enrolled)
                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT ->
                            getString(R.string.login_no_hardware)
                        BiometricPrompt.ERROR_HW_UNAVAILABLE ->
                            getString(R.string.login_hw_unavailable)
                        BiometricPrompt.ERROR_TIMEOUT ->
                            getString(R.string.login_timeout)
                        else ->
                            getString(R.string.login_error_generic, errString)
                    }
                    showStatus(message)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.login_prompt_title))
            .setSubtitle(getString(R.string.login_prompt_subtitle))
            .setNegativeButtonText(getString(R.string.cancel))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .build()
    }

    /** Revisa el estado del sensor ANTES de mostrar el lector de huella. */
    private fun checkAndAuthenticate() {
        binding.btnEnroll.visibility = View.GONE
        when (BiometricManager.from(this).canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                showStatus(getString(R.string.login_no_hardware))
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                showStatus(getString(R.string.login_hw_unavailable))
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showStatus(getString(R.string.login_none_enrolled))
                binding.btnEnroll.visibility = View.VISIBLE
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                showStatus(getString(R.string.login_update_required))
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                showStatus(getString(R.string.login_unsupported))
            else ->
                showStatus(getString(R.string.login_unknown_status))
        }
    }

    /** Lleva al usuario a los ajustes del sistema para registrar una huella. */
    private fun openEnrollment() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG
            )
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun showStatus(message: String) {
        binding.tvStatus.text = message
        binding.tvStatus.visibility = View.VISIBLE
    }

    /** finish() quita el login de la pila: con "atrás" ya no se regresa a él ni se evita. */
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
