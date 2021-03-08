package com.hlxh.interactivevideotool.ui.edit

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hlxh.interactivevideotool.R
import com.hlxh.interactivevideotool.model.Episode
import com.hlxh.interactivevideotool.model.Option
import com.hlxh.interactivevideotool.util.dip2Px

import kotlinx.android.synthetic.main.fragment_edit_options.*

/**
 * @author sunjingkai
 * @since 2020/7/1
 * @lastModified by sunjingkai on 2020/7/1
 */

class EditOptionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_options, container, false)
    }

    private val latestOptionText = mutableListOf<String>()
    private val latestOption = mutableListOf<Option>()
    private val optionViewList = mutableListOf<DraggableTextView>()

    private lateinit var episode: Episode

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val episodeId = arguments?.getString(EditConfigureFragment.KEY_EPISODE_ID) ?: ""
        val scriptId = arguments?.getString("script_id") ?: ""
        //val script = ScriptDataSource.scripts.value!!.first { it.id == scriptId }
        episode = Episode()


//        val episode = ScriptRepository.getEpisode(episodeId)
//        episode = Episode(
//            question = Question(
////                optionList = listOf(Option(x = 100f, y = 100f), Option(x = 200f, y = 100f)),
//                optionList = mutableListOf(Option(), Option()),
//                optionTextList = mutableListOf("repeat3", "to 1")
//            )
//        )

        btnSave.setOnClickListener {
            saveEpisode(episode)
            requireActivity().finish()
        }

        backgroundImage.setImageBitmap(EditConfigureFragment.frame)

        var optionPositionNotDefined = true

        episode.interact.let {
            it?.optionTextList?.zip(it.optionList)
        }?.forEachIndexed { index, (text, option) ->
            val optionView = createOptionText(index, text).apply {
                if (option.x != -1f && option.y != -1f) {
                    x = option.x * getScreenWidth(context)
                    y = option.y * getScreenHeight(context)
                    optionPositionNotDefined = false
                }
            }
            latestOptionText.add(text)
            latestOption.add(option)
            optionViewList.add(optionView)
            optionRoot.addView(optionView)
        }

        if (optionPositionNotDefined) optionRoot.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
    }

    private fun saveEpisode(episode: Episode) {
        episode.interact?.optionList?.apply {
            clear()
            addAll(latestOption)
        }
        episode.interact?.optionTextList?.apply {
            clear()
            addAll(latestOptionText)
        }
    }

    private fun createOptionText(index: Int, optionText: String): DraggableTextView {
        return DraggableTextView(requireContext())
            .apply {
                setTextColor(Color.BLACK)
                textSize = context.dip2Px(10).toFloat()
                setBackgroundResource(R.drawable.shape_draggable_text_border)
                text = optionText
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0,0,0, context.dip2Px(30))
                }
                with(context) {
                    setPadding(dip2Px(10),0,dip2Px(10),0)
                    minWidth = dip2Px(130)
                }
                gravity = Gravity.CENTER
                setListener(object : DraggableTextView.Listener {
                    override fun onDragEnd(x: Float, y: Float) {
                        handleOptionDragged(index, x, y)
                    }

                    override fun onSingleTap() {
                        showEditDialog(index, this@apply.text)
                    }

                })
            }
    }

    private fun DraggableTextView.handleOptionDragged(index: Int, x: Float, y: Float) {
        val lastOption = latestOption[index]
        if (optionViewList.any { it !== this && checkOptionOverlapped(it) }) {
            revertLastDrag()
            return
        }
        latestOption[index] = lastOption.copy(x = x / getScreenWidth(context),
            y = y / getScreenHeight(context)
        )
    }

    private fun showEditDialog(index: Int, text: CharSequence) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_edit_option_text)
        val btnComplete = dialog.findViewById<Button>(R.id.btnConfirm)!!
        val editText = dialog.findViewById<EditText>(R.id.etOption)!!
        editText.setText(text)
        editText.setSelection(text.length)
        btnComplete.setOnClickListener {
            val edited = editText.text.toString()
            if (edited.isBlank()) {
                Toast.makeText(requireContext(), "选项不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            latestOptionText[index] = edited
            optionViewList[index].text = edited
            dialog.hide()
        }
        dialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(resources.getColor(android.R.color.transparent));
        dialog.show()

        view?.postDelayed({
            val imManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 300)
    }
}

class EditOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_fragment_root)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.rootView, EditOptionsFragment().apply {
                arguments = intent.extras
            })
            .commit()
    }

    companion object {
        fun start(context: Context, episodeId: String, scriptId: String) {
            context.startActivity(Intent(context, EditOptionsActivity::class.java)
                .apply {
                    putExtras(Bundle().also {
                        it.putString(EditConfigureFragment.KEY_EPISODE_ID, episodeId)
                        it.putString("script_id", scriptId)
                    })
                })
        }
    }
}

fun getScreenHeight(context: Context): Int {
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    if (display == null) {
        return 0
    }
    display.getSize(size)
    return size.y
}

/**
 * 获取屏幕宽度
 */
fun getScreenWidth(context: Context): Int {
    val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}
