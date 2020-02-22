package ss.anoop.awesometextinputlayout

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.FrameLayout

class AwesomeTextInputLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    private val defStyleRes: Int = 0
) : FrameLayout(
    context,
    attributeSet,
    defStyleRes
) {

    private val path = Path()

    private var cornerRadius = 16f

    private var collapsedTextHeight = 0f

    private var collapsedTextWidth = 0f

    private var collapsedTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            10f,
            context.resources.displayMetrics
    )

    private var animationDuration = 300

    private var animator: Animator? = null

    private var hint: String = ""

    private var editText: EditText? = null

    private val expandedHintPoint = PointF()

    private var spacing = dpToPx(2f, context)

    private var hintBaseLine = 0f

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 4f
        textSize = collapsedTextSize
    }

    init {
        val padding = dpToPx(8f, context).toInt()
        setPadding(padding, padding, padding, padding)
        setWillNotDraw(false)
        attributeSet?.let(::initAttrs)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        editText?.let {
            val editTextHeight = it.height
            expandedHintPoint.apply {
                x = paddingLeft.plus(it.paddingLeft).toFloat()
                y = it.top.plus(
                    editTextHeight.div(2f).plus(
                        it.textSize.div(2)
                    )
                )
            }
            if(changed) {
                updateBorderPath(-spacing)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        paint.style = Paint.Style.STROKE
        canvas.drawPath(path, paint)
        paint.style = Paint.Style.FILL
        canvas.drawText(
            hint,
            expandedHintPoint.x,
            hintBaseLine,
            paint
        )
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (child is EditText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                child.background = null
            } else {
                child.setBackgroundDrawable(null)
            }

            child.setOnFocusChangeListener { _, hasFocus ->
                animateHint(
                    child,
                    hasFocus
                )
            }

            hint = child.hint.toString()
            val rect = Rect()
            paint.getTextBounds(
                hint,
                0,
                hint.length,
                rect
            )
            collapsedTextHeight = rect.height().toFloat()
            collapsedTextWidth = rect.width()
                .plus(spacing.times(2))
            editText = child
        }
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun updateBorderPath(
        textWidth: Float
    ) {
        path.reset()
        val strokeHalf = paint.strokeWidth.div(2)
        path.apply {
            moveTo(
                expandedHintPoint.x
                    .minus(spacing),
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                )
            )

            lineTo(
                cornerRadius,
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                )
            )
            quadTo(
                strokeHalf,
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                ),
                strokeHalf,
                cornerRadius.plus(
                    collapsedTextHeight.div(2)
                )
            )

            lineTo(
                strokeHalf,
                height.minus(
                    strokeHalf + cornerRadius
                )
            )

            quadTo(
                strokeHalf,
                height.minus(strokeHalf),
                cornerRadius,
                height.minus(strokeHalf)
            )

            lineTo(
                width.minus(
                    cornerRadius
                ),
                height.minus(strokeHalf)
            )

            quadTo(
                width.minus(
                    strokeHalf
                ),
                height.minus(
                    strokeHalf
                ),
                width.minus(
                    strokeHalf
                ),
                height.minus(
                    cornerRadius
                )
            )

            lineTo(
                width.minus(
                    strokeHalf
                ),
                cornerRadius
            )

            quadTo(
                width.minus(
                    strokeHalf
                ),
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                ),
                width.minus(
                    cornerRadius
                ),
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                )
            )

            lineTo(
                expandedHintPoint.x.plus(
                        textWidth
                ),
                strokeHalf.plus(
                    collapsedTextHeight.div(2)
                )
            )
        }
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        with(
            context.obtainStyledAttributes(
                attributeSet,
                R.styleable.AwesomeTextInputLayout,
                defStyleRes,
                0
            )
        ) {
            paint.color = getColor(
                R.styleable.AwesomeTextInputLayout_borderColor,
                paint.color
            )
            paint.strokeWidth = getDimension(
                R.styleable.AwesomeTextInputLayout_borderWidth,
                paint.strokeWidth
            )
            collapsedTextSize = getDimension(
                R.styleable.AwesomeTextInputLayout_animatedTextSize,
                paint.textSize
            ).also {
                paint.textSize = it
            }
            cornerRadius = getDimension(
                R.styleable.AwesomeTextInputLayout_cornerRadius,
                cornerRadius
            )
            animationDuration = getInteger(
                R.styleable.AwesomeTextInputLayout_animationDuration,
                animationDuration
            )
        }
    }

    private fun animateHint(
        editText: EditText,
        hasFocus: Boolean
    ) {

        if(editText.editableText.isNotBlank()){
            return
        }

        animator = if (hasFocus) {
            hint = editText.hint.toString()
            editText.hint = ""
            getHintAnimator(
                editText.textSize,
                collapsedTextSize,
                expandedHintPoint.y,
                collapsedTextHeight,
                -spacing,
                collapsedTextWidth
            )
        } else {
            getHintAnimator(
                collapsedTextSize,
                editText.textSize,
                collapsedTextHeight,
                expandedHintPoint.y,
                collapsedTextWidth,
                -spacing
            ).apply {
                addListener(object : DefaultAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        editText.hint = hint
                        hint = ""
                    }
                })
            }
        }

        animator?.start()
    }

    private fun getHintAnimator(
        fromTextSize: Float,
        toTextSize: Float,
        fromY: Float,
        toY: Float,
        fromTextWidth: Float,
        toTextWidth: Float
    ): Animator {
        val textSizeAnimator = ValueAnimator.ofFloat(
            fromTextSize,
            toTextSize
        ).apply {
            duration = animationDuration.toLong()
            interpolator = LinearInterpolator()
            addUpdateListener {
                paint.textSize = it.animatedValue as Float
                invalidate()
            }
        }

        val translateAnimator = ValueAnimator.ofFloat(
            fromY,
            toY
        ).apply {
            duration = animationDuration.toLong()
            interpolator = LinearInterpolator()
            addUpdateListener {
                hintBaseLine = it.animatedValue as Float
                invalidate()
            }
        }

        val textWidthAnimator = ValueAnimator.ofFloat(
            fromTextWidth,
            toTextWidth
        ).apply {
            duration = animationDuration.toLong()
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateBorderPath(it.animatedValue as Float)
                invalidate()
            }
        }

        return AnimatorSet().apply {
            playTogether(
                textSizeAnimator,
                translateAnimator,
                textWidthAnimator
            )
        }
    }

    private fun dpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
}