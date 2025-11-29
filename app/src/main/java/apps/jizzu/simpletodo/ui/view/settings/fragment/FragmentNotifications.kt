package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.Manifest
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.FragmentNotificationsBinding
import apps.jizzu.simpletodo.service.alarm.AlarmReceiver
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper

class FragmentNotifications : BaseSettingsFragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var mPreferenceHelper: PreferenceHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_notifications))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferenceHelper = PreferenceHelper.getInstance()
        initGeneralNotificationSwitch()
        initNotificationSoundButton()
    }

    private fun initGeneralNotificationSwitch() {
        binding.swGeneralNotification.setOnTouchListener { _, event -> event.actionMasked == MotionEvent.ACTION_MOVE }
        binding.swGeneralNotification.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)

        binding.swGeneralNotification.setOnClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, binding.swGeneralNotification.isChecked)

            // On Android 13+ request runtime POST_NOTIFICATIONS permission when user enables notifications
            if (binding.swGeneralNotification.isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val context = context
                if (context != null && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission from the hosting activity but route the result to this fragment
                    (activity as? apps.jizzu.simpletodo.ui.view.base.BaseActivity)?.requestPerms(Manifest.permission.POST_NOTIFICATIONS, this)
                }
            }

            callback?.onGeneralNotificationStateChanged()
        }

        binding.clGeneralNotification.setOnClickListener {
            binding.swGeneralNotification.isChecked = !binding.swGeneralNotification.isChecked
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, binding.swGeneralNotification.isChecked)
            callback?.onGeneralNotificationStateChanged()
        }
    }

    private fun initNotificationSoundButton() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID)

            if (notificationChannel == null) {
                val channel = NotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID, context?.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_HIGH).apply {
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            binding.clNotificationSound.setOnClickListener {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, AlarmReceiver.NOTIFICATION_CHANNEL_ID)
                startActivity(intent)
            }
        } else {
            binding.llNotifications.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE)
            binding.tvNotificationSoundTitle.setTextColor(Color.TRANSPARENT)
            binding.tvNotificationSoundSummary.setTextColor(Color.TRANSPARENT)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == apps.jizzu.simpletodo.ui.view.base.BaseActivity.PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                if (permissions[i] == Manifest.permission.POST_NOTIFICATIONS) {
                    if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted — update notification state
                        callback?.onGeneralNotificationStateChanged()
                    } else {
                        // Permission denied — revert switch, update preference and show snackbar directing to settings
                        binding.swGeneralNotification.isChecked = false
                        mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, false)
                        (activity as? apps.jizzu.simpletodo.ui.view.base.BaseActivity)?.showNoPermissionSnackbar(binding.root,
                                getString(R.string.permission_notification_snackbar_no_permission),
                                getString(R.string.permission_notification_toast))
                    }
                    break
                }
            }
        }
    }

    interface GeneralNotificationClickListener {
        fun onGeneralNotificationStateChanged()
    }

    companion object {
        var callback: GeneralNotificationClickListener? = null
    }
}