package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.MenuItem
// import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.ActivityChangelogBinding
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.utils.PreferenceHelper
import daio.io.dresscode.matchDressCode

class ChangelogActivity : BaseActivity() {

    private lateinit var binding: ActivityChangelogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        binding = ActivityChangelogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar(getString(R.string.whats_new_title), R.drawable.round_close_black_24)
        binding.btnConfirm.setOnClickListener { onBackPressed() }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else false

    override fun onBackPressed() {
        super.onBackPressed()
        PreferenceHelper.getInstance().putInt(PreferenceHelper.VERSION_CODE, 1)
    }
}