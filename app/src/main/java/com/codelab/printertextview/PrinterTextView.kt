package com.codelab.printertextview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView

class PrinterTextView : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    enum class PrinterSpeed(val gap: Long) {
        SLOW(200),
        NORMAL(100),
        FAST(50),
    }

    private val TAG = "PrinterTextView"

    private var currentPrinterTask: PrinterTask? = null

    /**
     * @param animatedText
     * @param printerSpeed
     */
    fun printText(
        animatedText: String,
        printerSpeed: PrinterSpeed = PrinterSpeed.NORMAL,
    ) {
        if (currentPrinterTask != null && currentPrinterTask!!.isPrinting()) {
            Log.d(TAG, "printText: endLastPrint")
            currentPrinterTask?.endPrint() {
                Log.d(TAG, "printText: rest String: $it")
                append(it)
            }
        }
        currentPrinterTask = PrinterTask(animatedText, printerSpeed).apply {
            print {
                append(it)
            }
        }
    }

    private class PrinterTask(private val animatedText: String, private val speed: PrinterSpeed) {

        private val TAG = "PrinterTask"

        private val count = animatedText.length

        private var curIdx = -1

        private val printerAnimator: ValueAnimator

        private lateinit var onUpdate: (ch: String) -> Unit

        init {
            printerAnimator = ValueAnimator.ofInt(0, count - 1).apply {
                duration = count * speed.gap
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val idx = it.animatedValue as Int
                    if (curIdx != idx) {
                        val ch = animatedText[idx].toString()
                        curIdx = idx
                        onUpdate(ch)
                    }
                }
            }
        }

        fun print(onUpdate: (cur: String) -> Unit) {
            Log.d(TAG, "print: start")
            this.onUpdate = onUpdate
            printerAnimator.start()
        }

        fun endPrint(onEnd: (rest: String) -> Unit) {
            Log.d(TAG, "print: end")
            if (curIdx < count - 1) {
                Log.d(TAG, "print: onEnd, curIdx: $curIdx, count: $count")
                onEnd(animatedText.substring(curIdx))
            }
            printerAnimator.end()
        }

        fun isPrinting(): Boolean = printerAnimator.isStarted || printerAnimator.isRunning

    }

    fun endPrint() = currentPrinterTask?.endPrint(){
        Log.d(TAG, "endPrint: $it")
        append(it)
    }

}