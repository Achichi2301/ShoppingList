package com.example.shopponglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.shopponglist.R
import com.example.shopponglist.databinding.ActivityNewNoteBinding
import com.example.shopponglist.entites.NoteItem
import com.example.shopponglist.fragments.NoteFragment
import com.example.shopponglist.utils.HtmlManager
import com.example.shopponglist.utils.MyTouchListener
import com.example.shopponglist.utils.TimeManager
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var defPref: SharedPreferences
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences((this))
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        onCLickColorPicker()
        actionMenuCallback()
    }

    private fun onCLickColorPicker() = with(binding){
        imRed.setOnClickListener{
            setColorForSelectedText(R.color.picker_red)
        }
        imBlue.setOnClickListener{
            setColorForSelectedText(R.color.picker_blue)
        }
        imOrange.setOnClickListener{
            setColorForSelectedText(R.color.picker_orange)
        }
        imPurple.setOnClickListener{
            setColorForSelectedText(R.color.picker_purple)
        }
        imYellow.setOnClickListener{
            setColorForSelectedText(R.color.picker_yellow)
        }
        imGreen.setOnClickListener{
            setColorForSelectedText(R.color.picker_green)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        binding.colorPicker.setOnTouchListener(MyTouchListener())
    }


    private fun getNote(){
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if(sNote != null) {
            note = sNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding){
            edTitle.setText(note?.title)
            edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nw_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save){
                setMainResult()
        } else if(item.itemId == android.R.id.home){
            finish()
        } else if(item.itemId == R.id.id_bold){
            setBoldForSelectedText()
        }  else if(item.itemId == R.id.id_color){
            if(binding.colorPicker.isShown) {
                closeColorPicker()
            } else {
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if(styles.isNotEmpty()){
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        edDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])
        edDescription.text.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this@NewNoteActivity, colorId)),
            startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            edDescription.text.trim()
            edDescription.setSelection(startPos)
    }

    private fun updateNote() : NoteItem? = with(binding){
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
            )
    }

    private fun setMainResult(){
        var editState = "new"
        val tempNote: NoteItem? = if(note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun createNewNote(): NoteItem{
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }



    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker(){
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker(){
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object  : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallback(){
        val actionCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }

        }
        binding.edDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "grey") == "grey"){
            R.style.Theme_NewNoteGrey
        } else {
            R.style.Theme_NewNotePink
        }
    }

}