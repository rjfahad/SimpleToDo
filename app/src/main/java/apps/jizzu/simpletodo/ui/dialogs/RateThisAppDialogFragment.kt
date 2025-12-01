package apps.jizzu.simpletodo.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.DialogRateThisAppBinding
import apps.jizzu.simpletodo.ui.dialogs.base.BaseDialogFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper

class RateThisAppDialogFragment : BaseDialogFragment() {

    private var _binding: DialogRateThisAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogRateThisAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initButtons() {
        val preferenceHelper = PreferenceHelper.getInstance()

        binding.tvRate.setOnClickListener {
            preferenceHelper.putBoolean(PreferenceHelper.IS_NEED_TO_SHOW_RATE_DIALOG_LATER, false)
            rateThisApp()
            dismiss()
        }
        binding.tvNeverShow.setOnClickListener {
            preferenceHelper.putBoolean(PreferenceHelper.IS_NEED_TO_SHOW_RATE_DIALOG_LATER, false)
            dismiss()
        }
        binding.tvShowLater.setOnClickListener {
            preferenceHelper.putInt(PreferenceHelper.LAUNCHES_COUNTER, 0)
            dismiss()
        }
    }

    private fun rateThisApp() {
        val appPackageName = activity?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$APP_PAGE_SHORT_LINK$appPackageName")))
        } catch (exception: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$APP_PAGE_LONG_LINK$appPackageName")))
        }
    }

    private companion object {
        const val APP_PAGE_SHORT_LINK = "market://details?id="
        const val APP_PAGE_LONG_LINK = "https://play.google.com/store/apps/details?id="
    }
}