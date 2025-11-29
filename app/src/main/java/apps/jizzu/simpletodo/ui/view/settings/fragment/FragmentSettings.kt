package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentSettingsBinding
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.DeviceInfo
import apps.jizzu.simpletodo.utils.toast

class FragmentSettings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        binding.tvUI.setOnClickListener { openFragment(FragmentUI()) }
        binding.tvNotifications.setOnClickListener { openFragment(FragmentNotifications()) }
        binding.tvDateAndTime.setOnClickListener { openFragment(FragmentDateAndTime()) }
        binding.tvBackupAndRestore.setOnClickListener { openFragment(FragmentBackupAndRestore()) }
        binding.tvFeedback.setOnClickListener { sendFeedback() }
        binding.tvGitHub.setOnClickListener { openUri(GIT_HUB_PAGE) }
        binding.tvPrivacyPolicy.setOnClickListener { openUri(PRIVACY_POLICY_PAGE) }
        binding.tvLicenses.setOnClickListener { openFragment(FragmentLicenses()) }
    }

    private fun openFragment(fragment: BaseSettingsFragment) =
            fragmentManager?.beginTransaction()?.replace(R.id.flFragmentContainer, fragment)?.addToBackStack(null)?.commit()

    private fun sendFeedback() {
        val email = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.Builder().scheme(SCHEME).build()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.author_gmail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + DeviceInfo.deviceInfo
                    + "\n" + getString(R.string.feedback_app_version) + "version"
                    + "\n" + getString(R.string.feedback))
        }

        try {
            startActivity(Intent.createChooser(email, getString(R.string.settings_feedback)))
        } catch (exception: android.content.ActivityNotFoundException) {
            toast(getString(R.string.settings_no_email_apps))
        }
    }

    private fun openUri(uri: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (exception: android.content.ActivityNotFoundException) {
            toast(getString(R.string.settings_no_browser_apps))
        }
    }

    private companion object {
        const val GIT_HUB_PAGE = "https://github.com/rjfahad/SimpleToDo"
        const val PRIVACY_POLICY_PAGE = "https://github.com/rjfahad/SimpleToDo/blob/master/PRIVACY_POLICY.md"
        const val SCHEME = "mailto"
    }
}