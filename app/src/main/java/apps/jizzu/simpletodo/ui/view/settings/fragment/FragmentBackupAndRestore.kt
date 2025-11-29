package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentBackupAndRestoreBinding
import apps.jizzu.simpletodo.ui.view.settings.activity.SettingsActivity
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment

class FragmentBackupAndRestore : BaseSettingsFragment() {
    private lateinit var binding: FragmentBackupAndRestoreBinding
    private lateinit var mSettingsActivity: SettingsActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBackupAndRestoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_backup_and_restore))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSettingsActivity = activity as SettingsActivity
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.clCreateBackup.setOnClickListener {
            // Use SAF to create backup file
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, "SimpleToDo_Backup.ser")
            }
            startActivityForResult(intent, CREATE_BACKUP_REQUEST_CODE)
        }

        binding.clRestoreBackup.setOnClickListener {
            // Use SAF to select backup file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
            }
            startActivityForResult(intent, RESTORE_BACKUP_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data!!
            when (requestCode) {
                CREATE_BACKUP_REQUEST_CODE -> {
                    (activity as? SettingsActivity)?.createBackup(uri)
                }
                RESTORE_BACKUP_REQUEST_CODE -> {
                    (activity as? SettingsActivity)?.restoreBackup(uri)
                }
            }
        }
    }

    companion object {
        private const val CREATE_BACKUP_REQUEST_CODE = 100
        private const val RESTORE_BACKUP_REQUEST_CODE = 101
    }
}