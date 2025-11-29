package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentUserInterfaceBinding
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import daio.io.dresscode.dressCodeStyleId

class FragmentUI : BaseSettingsFragment() {

    private var _binding: FragmentUserInterfaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserInterfaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_user_interface))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimationSwitch()
    }

    private fun initAnimationSwitch() {
        val preferenceHelper = PreferenceHelper.getInstance()

        binding.swAnimation.setOnTouchListener { _: View?, event: MotionEvent -> event.actionMasked == MotionEvent.ACTION_MOVE }
        binding.swAnimation.isChecked = preferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)

        binding.swAnimation.setOnClickListener {
            preferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, binding.swAnimation.isChecked)
        }

        binding.clAnimations.setOnClickListener {
            binding.swAnimation.isChecked = !binding.swAnimation.isChecked
            preferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, binding.swAnimation.isChecked)
        }

        binding.clChooseTheme.setOnClickListener {
            val listItems = resources.getStringArray(R.array.app_theme)
            var selectedItemPosition = when (requireActivity().dressCodeStyleId) {
                R.style.AppTheme_Light -> 0
                R.style.AppTheme_Dark -> 1
                R.style.AppTheme_Black -> 2
                else -> 0
            }

            val builder = when (requireActivity().dressCodeStyleId) {
                R.style.AppTheme_Light -> AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle_Light)
                R.style.AppTheme_Dark -> AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle_Dark)
                else -> AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle_Dark)
            }
            builder.apply {
                setTitle(getString(R.string.app_theme_dialog_title))
                setSingleChoiceItems(listItems, selectedItemPosition) { dialogInterface, i ->
                    selectedItemPosition = i

                    activity?.dressCodeStyleId = when (selectedItemPosition) {
                        0 -> R.style.AppTheme_Light
                        1 -> R.style.AppTheme_Dark
                        2 -> R.style.AppTheme_Black
                        else -> R.style.AppTheme_Light
                    }
                    isThemeChanged = true
                    dialogInterface.dismiss()
                }
            }
            builder.create().apply {
                window?.attributes?.windowAnimations = R.style.DialogAnimation
                show()
                window?.setLayout(resources.getDimensionPixelSize(R.dimen.dialog_picker_width), ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    companion object {
        var isThemeChanged = false
    }
}