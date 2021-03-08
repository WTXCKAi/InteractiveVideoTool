package com.hlxh.interactivevideotool.ui.create

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.ScriptDetail
import com.hlxh.interactivevideotool.model.TYPE_HACKER
import com.hlxh.interactivevideotool.model.TYPE_VICTIM
import com.hlxh.interactivevideotool.ui.edit.EditConfigureFragment
import com.hlxh.interactivevideotool.util.ToolsFragment
import com.hlxh.interactivevideotool.util.requestPermission
import kotlinx.android.synthetic.main.fragment_create.*
import java.util.*

class CreateFragment : AppCompatDialogFragment() {
    private var mLabel: String = ""
    private var mScriptType: Int = TYPE_VICTIM
    private var mPhotoUrl: String? = null
    private lateinit var mNavController: NavController
    private var mOnSaveBlock: ((ScriptDetail) -> Unit)? = null

    fun setOnSave(block: (ScriptDetail) -> Unit) {

        mOnSaveBlock = block
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mNavController = findNavController()
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onStart() {
        super.onStart()
        //调整dialog的大小，要通过调整window
        val window = requireDialog().window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            val windowParams: WindowManager.LayoutParams = window.attributes

            windowParams.dimAmount = 0.7f    //dialog外部背景透明度
            window.attributes = windowParams

            val dialog: Dialog = requireDialog()
            if (dialog != null) {
                val dm = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getRealMetrics(dm)
                dialog.window?.setLayout((dm.widthPixels * 0.85).toInt(), (dm.heightPixels * 0.8).toInt())
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.label_array,
        android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            label_choice_spinner.adapter = adapter
        }

        //监听点击事件
        label_choice_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mLabel = resources.getStringArray(R.array.label_array)[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        scene_victim_button.setOnClickListener {
            mScriptType = TYPE_VICTIM
        }

        scene_hacker_button.setOnClickListener {
            mScriptType = TYPE_HACKER
        }

        add_cover_button.setOnClickListener {
            goSelectPhoto()
        }

        cancel_button.setOnClickListener {
            dismiss()
        }

        save_button.setOnClickListener {
            val scriptTitle = script_title.text.toString().trim()
            if (scriptTitle.isBlank()) {
                Toast.makeText(requireContext(), "剧集标题不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mPhotoUrl == null) {
                showAlertDialog()
                return@setOnClickListener
            }
            saveScript()
            mNavController.navigate(R.id.navigation_mine)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //接收所选的照片信息
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == EditConfigureFragment.REQUEST_CODE_SELECT_VIDEO) {

            data?.data?.toPhotoPath(requireActivity())?.takeIf { it.isNotEmpty() }?.let { url ->
                mPhotoUrl = url
                Glide.with(add_cover_button)
                    .load(url)
                    .into(add_cover_button)
            }
        }
    }
    private fun goSelectPhoto() {
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ),
            REQUEST_CODE_SELECT_PHOTO
        )
    }

    companion object {
        const val REQUEST_CODE_SELECT_PHOTO = 1
    }

    private fun Uri.toPhotoPath(activity: Activity): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = activity.managedQuery(this, projection, null, null, null)
        val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    private fun showAlertDialog() {
        val message = "剧集封面尚未选择，确定创建？"
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("确定") { dialog, which ->
                saveScript()
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun saveScript() {
//        val id = UUID.randomUUID().toString() //随机生成唯一id
//        val title = input_title.text.toString().trim()
//        val label = mLabel
//        val type = mScriptType
//        val summary = content_summary_input.text.toString().trim()
//        val coverImageUrl = mPhotoUrl!!
//        mOnSaveBlock?.invoke(
//            ScriptDetail(id, type, coverImageUrl, label, title, summary)
//        )
//        Log.d("save", "id = $id, title = $title, label = $label, type = $type, summary = $summary, " +
//                "url = $coverImageUrl")
        //val simpleDateFormat: java.text.SimpleDateFormat = java.text.SimpleDateFormat("yyyy-mm-dd")
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val time = simpleDateFormat.format(Date(System.currentTimeMillis()))

        mOnSaveBlock?.invoke(
            ScriptDetail(
                id = UUID.randomUUID().toString(),  //随机生成唯一id
                title = input_title.text.toString().trim(),
                label = mLabel,
                type = mScriptType,
                summary = content_summary_input.text.toString().trim(),
                coverImageUrl = mPhotoUrl!!,
                date = time
                )
        )
        dismiss()
    }

}