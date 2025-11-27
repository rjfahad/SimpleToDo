package apps.jizzu.simpletodo.ui.view.task

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.databinding.ActivityTaskNoteBinding
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import daio.io.dresscode.matchDressCode

class TaskNoteActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        binding = ActivityTaskNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar(getString(R.string.task_note), R.drawable.round_close_black_24)
        initScrollViewListener(binding.svTaskDetails)
        restoreData()
    }

    private fun restoreData() {
        val note = intent.getStringExtra("note")
        if (!note.isNullOrEmpty()) {
            binding.tvTaskNote.apply {
                setText(note)
                setSelection(note.length)
            }
        } else showKeyboard(binding.tvTaskNote)
    }

    private fun saveNote() {
        setResult(Activity.RESULT_OK, Intent().putExtra("note", binding.tvTaskNote.text.toString()))
        hideKeyboard(binding.tvTaskNote)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_note_menu, menu)

        menu.getItem(0).icon?.apply {
            mutate()
            setColorFilter(ContextCompat.getColor(this@TaskNoteActivity, R.color.blue),
                    PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(binding.tvTaskNote)
                onBackPressed()
            }
            R.id.action_save -> saveNote()
        }
        return super.onOptionsItemSelected(item)
    }
}