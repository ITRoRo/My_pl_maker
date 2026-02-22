package com.example.myplmaker.player.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.myplmaker.R
import androidx.core.content.withStyledAttributes

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var isPlaying: Boolean = false

    private var playDrawable = AppCompatResources.getDrawable(context, R.drawable.play)
    private var pauseDrawable = AppCompatResources.getDrawable(context, R.drawable.stop)

    private val dstRect = Rect()

    private var clickListener: ((Boolean) -> Unit)? = null

    init {
        if (attrs != null) {
            context.withStyledAttributes(
                attrs,
                R.styleable.PlaybackButtonView,
                defStyleAttr,
                defStyleRes
            ) {

                val playResId = getResourceId(R.styleable.PlaybackButtonView_playSrc, 0)
                val pauseResId = getResourceId(R.styleable.PlaybackButtonView_pauseSrc, 0)

                if (playResId != 0) {
                    playDrawable = AppCompatResources.getDrawable(context, playResId)
                }
                if (pauseResId != 0) {
                    pauseDrawable = AppCompatResources.getDrawable(context, pauseResId)
                }

            }
        }
    }


    fun toggleState() {
        isPlaying = !isPlaying
        invalidate()
    }

    fun setPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        invalidate()
    }

    fun setOnPlaybackClickListener(listener: (Boolean) -> Unit) {
        clickListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dstRect.set(
            paddingLeft,
            paddingTop,
            w - paddingRight,
            h - paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val drawable = if (isPlaying) pauseDrawable else playDrawable
        drawable ?: return

        drawable.bounds = dstRect
        drawable.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                toggleState()
                clickListener?.invoke(isPlaying)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}