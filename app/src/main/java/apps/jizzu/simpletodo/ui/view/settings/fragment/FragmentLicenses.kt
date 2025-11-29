package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentLicensesBinding
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment

class FragmentLicenses : BaseSettingsFragment() {
    private lateinit var binding: FragmentLicensesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLicensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_licenses))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        binding.clKotterKnife.setOnClickListener { openUri(KOTTER_KNIFE_PAGE) }
        binding.clCircularAnim.setOnClickListener { openUri(CIRCULAR_ANIM_PAGE) }
        binding.clRxJava.setOnClickListener { openUri(RX_JAVA_PAGE) }
        binding.clRxKotlin.setOnClickListener { openUri(RX_KOTLIN_PAGE) }
        binding.clDressCode.setOnClickListener { openUri(DRESS_CODE_PAGE) }
        binding.clMaterialIntro.setOnClickListener { openUri(MATERIAL_INTRO_PAGE) }
    }

    private fun openUri(uri: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

    private companion object {
        private const val KOTTER_KNIFE_PAGE = "https://github.com/JakeWharton/kotterknife"
        private const val CIRCULAR_ANIM_PAGE = "https://github.com/XunMengWinter/CircularAnim"
        private const val RX_JAVA_PAGE = "https://github.com/ReactiveX/RxJava"
        private const val RX_KOTLIN_PAGE = "https://github.com/ReactiveX/RxKotlin"
        private const val DRESS_CODE_PAGE = "https://github.com/Daio-io/dresscode"
        private const val MATERIAL_INTRO_PAGE = "https://github.com/heinrichreimer/material-intro"
    }
}
