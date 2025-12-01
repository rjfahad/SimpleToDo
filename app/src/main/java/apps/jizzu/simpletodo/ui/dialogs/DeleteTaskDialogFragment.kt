package apps.jizzu.simpletodo.ui.dialogs

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.databinding.DialogDefaultBinding
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.dialogs.base.BaseDialogFragment
import apps.jizzu.simpletodo.vm.DeleteTaskViewModel

class DeleteTaskDialogFragment(val task: Task) : BaseDialogFragment() {
    private lateinit var mViewModel: DeleteTaskViewModel
    private lateinit var binding: DialogDefaultBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { mViewModel = createViewModel(it.application) }
        initDialog()
    }

    private fun initDialog() {
        binding.tvDialogMessage.setText(R.string.dialog_message)
        binding.tvConfirm.setText(R.string.action_delete)
        binding.tvConfirm.setOnClickListener {
            mViewModel.deleteTask(task)
            if (task.date != 0L) {
                val alarmHelper = AlarmHelper.getInstance()
                alarmHelper.removeAlarm(task.timeStamp)
                alarmHelper.removeNotification(task.timeStamp, activity!!.applicationContext)
            }
            activity?.finish()
        }
        binding.tvCancel.setOnClickListener { dismiss() }
    }

    private fun createViewModel(application: Application) = ViewModelProvider(this).get(DeleteTaskViewModel(application)::class.java)
}
