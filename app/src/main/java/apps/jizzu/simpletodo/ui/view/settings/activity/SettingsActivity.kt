package apps.jizzu.simpletodo.ui.view.settings.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.databinding.ActivitySettingsBinding
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.service.widget.WidgetProvider
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.ui.view.settings.fragment.FragmentSettings
import apps.jizzu.simpletodo.ui.view.settings.fragment.FragmentUI
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.vm.TaskListViewModel
import daio.io.dresscode.matchDressCode
import java.io.*
import java.util.*

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar(getString(R.string.settings), null, binding.toolbar.root)
        openSettingsFragment()
    }

    fun setToolbarTitle(title: String) {
        tvToolbarTitle.text = title
        checkScreenResolution()
    }

    private fun checkScreenResolution() {
        val displayMetrics = DisplayMetrics()
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        if (width <= 480 || height <= 800) {
            tvToolbarTitle.textSize = 18F
        }
    }

    private fun openSettingsFragment() {
        if (!FragmentUI.isThemeChanged) {
            supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, FragmentSettings()).commit()
        } else FragmentUI.isThemeChanged = false
    }

    override fun onResume() {
        super.onResume()
        setToolbarTitle(getString(R.string.settings))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onPause() {
        super.onPause()

        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this)
                .getAppWidgetIds(ComponentName(this, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    fun createBackup(uri: Uri) {
        val viewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        Thread {
            try {
                val tasks = viewModel.getAllTasks()
                
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    ObjectOutputStream(outputStream).use { objectOutputStream ->
                        objectOutputStream.writeObject(tasks)
                    }
                }
                runOnUiThread {
                    toast(getString(R.string.backup_create_message_success))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    toast(getString(R.string.backup_create_message_failure))
                }
            }
        }.start()
    }

    fun restoreBackup(uri: Uri) {
        val viewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        val alarmHelper = AlarmHelper.getInstance()
        Thread {
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    ObjectInputStream(inputStream).use { objectInputStream ->
                        @Suppress("UNCHECKED_CAST")
                        val restoredTasks = objectInputStream.readObject() as List<Task>
                        
                        viewModel.deleteAllTasks()
                        Thread.sleep(100)
                        for (task in restoredTasks) {
                            viewModel.saveTask(task)
                            if (task.date != 0L && task.date > Calendar.getInstance().timeInMillis) {
                                alarmHelper.setAlarm(task)
                            }
                        }
                    }
                }
                runOnUiThread {
                    toast(getString(R.string.backup_restore_message_success))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    toast(getString(R.string.backup_restore_message_failure))
                }
            }
        }.start()
    }
}
