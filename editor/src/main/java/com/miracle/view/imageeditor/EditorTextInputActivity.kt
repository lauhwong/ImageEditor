package com.miracle.view.imageeditor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.miracle.view.imageeditor.bean.InputTextData
import com.miracle.view.imageeditor.view.ColorSeekBar
import kotlinx.android.synthetic.main.activity_editor_text_input.*

/**
 * ## TextInput activity
 *
 * Created by lxw
 */
class EditorTextInputActivity : AppCompatActivity() {
    private val mResultCode = 301
    private var mTextColor = 0
    private var mTextInputId: String? = null

    companion object {
        private val EXTRA_CODE = "extraInput"
        fun intent(context: Context, data: InputTextData?): Intent {
            val intent = Intent(context, EditorTextInputActivity::class.java)
            intent.putExtra(EXTRA_CODE, data)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_text_input)
        AdjustResizeInFullScreen.assistActivity(this)
        initData()
        initListener()
    }

    private fun initData() {
        val readyData = intent.getSerializableExtra(EXTRA_CODE) as? InputTextData
        readyData?.let {
            mTextInputId = readyData.id
            etInput.setText(readyData.text ?: "")
            colorBarInput.setOnInitDoneListener(object : ColorSeekBar.OnInitDoneListener {
                override fun done() {
                    var position = 8
                    readyData.color?.let {
                        position = colorBarInput.getColorIndexPosition(it)
                    }
                    colorBarInput.setColorBarPosition(position)
                }
            })

        }
    }

    private fun initListener() {
        tvCancelInput.setOnClickListener {
            finish()
        }
        tvConfirmInput.setOnClickListener {
            val text = etInput.text.trim()
            if (text.isBlank()) {
                finish()
            } else {
                val intent = Intent()
                intent.putExtra(mResultCode.toString(), InputTextData(mTextInputId, text.toString(), mTextColor))
                setResult(mResultCode, intent)
                finish()
            }

        }
        colorBarInput.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                etInput.setTextColor(color)
                mTextColor = color
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.animation_top_to_bottom)
    }
}
