package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentDateAndTimeBinding
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import daio.io.dresscode.dressCodeStyleId

class FragmentDateAndTime : BaseSettingsFragment() {
    private lateinit var binding: FragmentDateAndTimeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDateAndTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_date_and_time))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.clDateFormat.setOnClickListener {
            showSingleChoiceDialog(R.array.date_format_list, getString(R.string.date_format_dialog_title),
                    PreferenceHelper.DATE_FORMAT_KEY)
        }

        binding.clTimeFormat.setOnClickListener {
            showSingleChoiceDialog(R.array.time_format_list, getString(R.string.time_format_dialog_title),
                    PreferenceHelper.TIME_FORMAT_KEY)
        }
    }

    private fun showSingleChoiceDialog(array: Int, title: String, formatKey: String) {
        val listItems = resources.getStringArray(array)
        val preferenceHelper = PreferenceHelper.getInstance()
        var selectedItemPosition = preferenceHelper.getInt(formatKey)

        val builder = when (activity?.dressCodeStyleId) {
            R.style.AppTheme_Light -> AlertDialog.Builder(activity as Context, R.style.AlertDialogStyle_Light)
            R.style.AppTheme_Dark -> AlertDialog.Builder(activity as Context, R.style.AlertDialogStyle_Dark)
            else -> AlertDialog.Builder(activity as Context, R.style.AlertDialogStyle_Dark)
        }
        builder.apply {
            setTitle(title)
            setSingleChoiceItems(listItems, selectedItemPosition) { dialogInterface, i ->
                selectedItemPosition = i
                preferenceHelper.putInt(formatKey, i)
                dialogInterface.dismiss()
            }
        }
        builder.create().apply {
            window?.attributes?.windowAnimations = R.style.DialogAnimation
            show()
            window?.setLayout(resources.getDimensionPixelSize(R.dimen.dialog_picker_width), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback?.onDateAndTimeFormatChanged()
    }

    interface DateAndTimeFormatCallback {
        fun onDateAndTimeFormatChanged()
    }

    companion object {
        var callback: DateAndTimeFormatCallback? = null
    }
}