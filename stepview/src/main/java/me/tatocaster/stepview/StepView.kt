package me.tatocaster.stepview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ArrayRes
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.text.*
import android.util.Log
import androidx.core.content.res.ResourcesCompat


class StepView : View {

    private var titles: Array<String>? = null

    var radius: Int = 0
    private var pageStrokeAlpha: Int = 0
    private var pageTitleId: Int = 0
    private var pageActiveTitleColor: Int = 0
    private var pageInActiveTitleColor: Int = 0
    private var titleTextSize: Float = 0.toFloat()
    private var mLineHeight: Float = 0.toFloat()
    private var strokeWidth: Int = 0
    private var currentStepPosition: Int = 0
    private var stepsCount = 1
    private var bgColor: Int = 0
    private var stepColor: Int = 0
    private var currentColor: Int = 0
    private var textColor: Int = 0
    private var secondaryTextColor: Int = 0
    private var secondaryTextEnabled: Boolean = false
    private var textTypeFace: Int = 0
    private var textBottomAlign: Boolean = true

    private var pointY: Int = 0
    private var startX: Int = 0
    private var endX: Int = 0
    private var stepDistance: Int = 0
    private var offset: Float = 0.toFloat()
    private var offsetPixel: Int = 0
    private var pagerScrollState: Int = 0

    private var paint: Paint? = null
    private var pStoke: Paint? = null
    private var pText: TextPaint? = null
    private var tText: TextPaint? = null
    private val textBounds = Rect()
    private var onClickListener: OnClickListener? = null
    private val hsvCurrent = FloatArray(3)
    private val hsvBG = FloatArray(3)
    private val hsvProgress = FloatArray(3)

    private var clickable = false
    private var withViewpager: Boolean = false
    private var viewPagerChangeListener: ViewPagerOnChangeListener? = null
    private var disablePageChange: Boolean = false
    private var onTabSelectedListener: TabLayout.OnTabSelectedListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs)
    }


    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {

        initAttributes(context, attributeSet)

        paint = Paint()
        pStoke = Paint()
        pText = TextPaint()
        tText = TextPaint()

        paint!!.color = stepColor
        paint!!.flags = Paint.ANTI_ALIAS_FLAG
        paint!!.strokeWidth = mLineHeight
        if (textTypeFace > 0)
            paint!!.typeface = ResourcesCompat.getFont(context, textTypeFace)

        pStoke!!.color = stepColor
        pStoke!!.strokeWidth = strokeWidth.toFloat()
        pStoke!!.style = Paint.Style.STROKE
        pStoke!!.flags = Paint.ANTI_ALIAS_FLAG


        /**
         * @titleTextSize must not be greater than 19
         */

        tText!!.textSize = titleTextSize
        tText!!.color = pageInActiveTitleColor
        tText!!.textAlign = Paint.Align.LEFT
        tText!!.flags = Paint.ANTI_ALIAS_FLAG
        tText!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        pText!!.color = textColor
        pText!!.textSize = radius * 1.2f
        pText!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        pText!!.textAlign = Paint.Align.CENTER
        pText!!.flags = Paint.ANTI_ALIAS_FLAG
        minimumHeight = radius * 7
        Color.colorToHSV(currentColor, hsvCurrent)
        Color.colorToHSV(bgColor, hsvBG)
        Color.colorToHSV(stepColor, hsvProgress)
        invalidate()
    }

    private fun initAttributes(context: Context, attributeSet: AttributeSet?) {
        val attr = context.obtainStyledAttributes(attributeSet, R.styleable.StepView, 0, 0)
            ?: return

        try {
            titleTextSize =
                attr.getDimension(R.styleable.StepView_svTitleTextSize, dp2px(DEFAULT_TITLE_SIZE.toFloat()))
                    .toInt().toFloat()
            radius =
                attr.getDimension(R.styleable.StepView_svRadius, dp2px(DEFAULT_STEP_RADIUS.toFloat())).toInt()
            strokeWidth =
                attr.getDimension(R.styleable.StepView_svStrokeWidth, dp2px(DEFAULT_STOKE_WIDTH.toFloat()))
                    .toInt()
            stepsCount = attr.getInt(R.styleable.StepView_svStepCount, DEFAULT_STEP_COUNT)
            mLineHeight = attr.getDimension(R.styleable.StepView_svLineHeight, DEFAULT_LINE_HEIGHT)
            stepColor = attr.getColor(
                R.styleable.StepView_svStepColor,
                ContextCompat.getColor(context, DEFAULT_STEP_COLOR)
            )
            currentColor = attr.getColor(
                R.styleable.StepView_svCurrentStepColor,
                ContextCompat.getColor(context, DEFAULT_CURRENT_STEP_COLOR)
            )
            bgColor = attr.getColor(
                R.styleable.StepView_svBackgroundColor,
                ContextCompat.getColor(context, DEFAULT_BACKGROUND_COLOR)
            )
            textColor = attr.getColor(
                R.styleable.StepView_svTextColor,
                ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
            )
            secondaryTextColor = attr.getColor(
                R.styleable.StepView_svSecondaryTextColor,
                ContextCompat.getColor(context, DEFAULT_SECONDARY_TEXT_COLOR)
            )
            pageInActiveTitleColor = attr.getColor(
                R.styleable.StepView_svInActiveTitleColor,
                ContextCompat.getColor(context, DEFAULT_INACTIVE_TITLE)
            )
            pageActiveTitleColor = attr.getColor(
                R.styleable.StepView_svActiveTitleColor,
                ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
            )
            pageTitleId = attr.getResourceId(R.styleable.StepView_svTitles, NO_ID)
            pageStrokeAlpha = attr.getInt(R.styleable.StepView_svStrokeAlpha, DEFAULT_STROKE_ALPHA)
            clickable = attr.getBoolean(R.styleable.StepView_svClickableTitle, false)
            secondaryTextEnabled = attr.getBoolean(R.styleable.StepView_svSecondaryTextEnabled, false)
            textTypeFace = attr.getResourceId(R.styleable.StepView_svTextTypeFace, 0)
            textBottomAlign = attr.getBoolean(R.styleable.StepView_svTextBottom, true)

        } finally {
            attr.recycle()
        }
    }

    private fun addOnTabSelectedListener(onTabSelectedListener: TabLayout.OnTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener
    }


    private fun dp2px(dp: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }

    fun getStepsCount(): Int {
        return stepsCount
    }

    fun setStepsCount(stepsCount: Int) {
        this.stepsCount = stepsCount
        invalidate()
    }

    fun getCurrentStepPosition(): Int {
        return currentStepPosition
    }

    fun setCurrentStepPosition(currentStepPosition: Int) {
        this.currentStepPosition = currentStepPosition
        invalidate()
    }


    override fun isClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    fun setupWithViewPager(viewPager: ViewPager) {
        val adapter = viewPager.adapter
            ?: throw IllegalArgumentException("ViewPager does not have a PagerAdapter set")
        if (viewPagerChangeListener == null) {
            viewPagerChangeListener = ViewPagerOnChangeListener(this)
        }
        withViewpager = true
        // First we'll add Steps.
        setStepsCount(adapter.count)

        // Now we'll add our page change listener to the ViewPager
        viewPager.addOnPageChangeListener(viewPagerChangeListener!!)

        // Now we'll add a selected listener to set ViewPager's currentStepPosition item
        setOnClickListener(ViewPagerOnSelectedListener(viewPager))

        viewPager.setOnTouchListener { v, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                (v as ViewPager).addOnPageChangeListener(viewPagerChangeListener!!)
                disablePageChange = false
            }
            false
        }

        // Make sure we reflect the currently set ViewPager item
        if (adapter.count > 0) {
            val curItem = viewPager.currentItem
            if (getCurrentStepPosition() != curItem) {
                setCurrentStepPosition(curItem)
                invalidate()
            }
        }
    }


    fun setTitles(@ArrayRes id: Int) {
        //Set page titles through java
        this.pageTitleId = id
    }

    fun setTitles(titles: Array<String>) {
        this.titles = titles
    }

    override fun onDraw(canvas: Canvas) {
        if (stepsCount <= 1) {
            visibility = GONE
            return
        }
        super.onDraw(canvas)
        var pointX = startX
        val pointOffset: Int

        /** draw Line  */
        for (i in 0 until stepsCount - 1) {
            when {
                i < currentStepPosition -> {
                    paint!!.color = stepColor
                    canvas.drawLine(
                        pointX.toFloat(),
                        pointY.toFloat(),
                        (pointX + stepDistance).toFloat(),
                        pointY.toFloat(),
                        paint!!
                    )
                }
                i == currentStepPosition -> {
                    paint!!.color = bgColor
                    canvas.drawLine(
                        pointX.toFloat(),
                        pointY.toFloat(),
                        (pointX + stepDistance).toFloat(),
                        pointY.toFloat(),
                        paint!!
                    )
                }
                else -> {
                    paint!!.color = bgColor
                    canvas.drawLine(
                        pointX.toFloat(),
                        pointY.toFloat(),
                        (pointX + stepDistance).toFloat(),
                        pointY.toFloat(),
                        paint!!
                    )
                }
            }
            pointX += stepDistance
        }

        /**draw progress Line   */
        if (offsetPixel != 0 && pagerScrollState == 1) {
            pointOffset = startX + currentStepPosition * stepDistance
            val drawOffset = pointOffset + offsetPixel
            if (drawOffset in startX..endX) {
                if (offsetPixel < 0) {
                    paint!!.color = bgColor
                } else {
                    paint!!.color = stepColor
                }
                canvas.drawLine(
                    pointOffset.toFloat(),
                    pointY.toFloat(),
                    drawOffset.toFloat(),
                    pointY.toFloat(),
                    paint!!
                )
            }
        }

        /**draw Circle  */
        pointX = startX
        for (i in 0 until stepsCount) {
            if (i < currentStepPosition) {
                //draw previous step
                paint!!.color = stepColor
                canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), paint!!)

                //draw transition
                if (i == currentStepPosition - 1 && offsetPixel < 0 && pagerScrollState == 1) {
                    pStoke!!.alpha = pageStrokeAlpha
                    pStoke!!.strokeWidth = (strokeWidth - Math.round(strokeWidth * offset)).toFloat()
                    canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), pStoke!!)
                }

                pText!!.color = secondaryTextColor

                tText!!.color = pageInActiveTitleColor

            } else if (i == currentStepPosition) {
                //draw current step
                if (offsetPixel == 0 || pagerScrollState == 0) {
                    //set stroke default
                    paint!!.color = currentColor
                    pStoke!!.strokeWidth = Math.round(strokeWidth.toFloat()).toFloat()
                    pStoke!!.alpha = pageStrokeAlpha
                } else if (offsetPixel < 0) {
                    pStoke!!.strokeWidth = Math.round(strokeWidth * offset).toFloat()
                    pStoke!!.alpha = Math.round(offset * 11f)
                    paint!!.color = getColorToBG(offset)
                } else {
                    //set stroke transition
                    paint!!.color = getColorToProgress(offset)
                    pStoke!!.strokeWidth = (strokeWidth - Math.round(strokeWidth * offset)).toFloat()
                    pStoke!!.alpha = 255 - Math.round(offset * pageStrokeAlpha)
                }
                canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), paint!!)
                canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), pStoke!!)
                pText!!.color = textColor

                tText!!.color = pageActiveTitleColor

            } else {
                //draw next step
                paint!!.color = bgColor
                canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), paint!!)
                pText!!.color = secondaryTextColor

                tText!!.color = pageInActiveTitleColor

                //draw transition
                if (i == currentStepPosition + 1 && offsetPixel > 0 && pagerScrollState == 1) {
                    pStoke!!.strokeWidth = Math.round(strokeWidth * offset).toFloat()
                    pStoke!!.alpha = Math.round(offset * pageStrokeAlpha)
                    canvas.drawCircle(pointX.toFloat(), pointY.toFloat(), radius.toFloat(), pStoke!!)
                }
            }
            //Draw title text
            if (pageTitleId != NO_ID) {

                titles = context.resources.getStringArray(pageTitleId)

                //Draw titles
                var textStartX = pointX.toFloat()
                when (i) {
                    0 -> {
                        tText!!.textAlign = Paint.Align.LEFT
                        textStartX -= radius
                    }
                    stepsCount - 1 -> {
                        tText!!.textAlign = Paint.Align.RIGHT
                        textStartX += radius
                    }
                    else -> {
                        tText!!.textAlign = Paint.Align.CENTER
                    }
                }

                if (textBottomAlign)
                    canvas.drawMultilineText(
                        titles!![i],
                        tText!!,
                        measuredWidth / stepsCount,
                        textStartX,
                        pointY + radius / 2 + dp2px(10f)
                    )
                else
                    canvas.drawMultilineText(
                        titles!![i],
                        tText!!,
                        measuredWidth / stepsCount,
                        textStartX,
                        pointY - radius * 4 - dp2px(10f)
                    )
            }

            if (secondaryTextEnabled)
                drawTextCentred(canvas, pText!!, (i + 1).toString(), pointX.toFloat(), pointY.toFloat())


            pointX += stepDistance
        }

    }


    private fun drawTextCentred(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint)
    }

    private fun getColorToBG(offSet: Float): Int {
        var offset = offSet
        offset = Math.abs(offset)
        val hsv = FloatArray(3)
        hsv[0] = hsvBG[0] + (hsvCurrent[0] - hsvBG[0]) * offset
        hsv[1] = hsvBG[1] + (hsvCurrent[1] - hsvBG[1]) * offset
        hsv[2] = hsvBG[2] + (hsvCurrent[2] - hsvBG[2]) * offset
        return Color.HSVToColor(hsv)
    }

    private fun getColorToProgress(offSet: Float): Int {
        var offset = offSet
        offset = Math.abs(offset)
        val hsv = FloatArray(3)
        hsv[0] = hsvCurrent[0] + (hsvProgress[0] - hsvCurrent[0]) * offset
        hsv[1] = hsvCurrent[1] + (hsvProgress[1] - hsvCurrent[1]) * offset
        hsv[2] = hsvCurrent[2] + (hsvProgress[2] - hsvCurrent[2]) * offset
        return Color.HSVToColor(hsv)
    }

    private fun setOffset(offset: Float, position: Int) {
        this.offset = offset
        offsetPixel = Math.round(stepDistance * offset)
        if (currentStepPosition > position) {
            offsetPixel -= stepDistance
        } else {
            currentStepPosition = position
        }

        invalidate()
    }

    private fun setPagerScrollState(pagerScrollState: Int) {
        this.pagerScrollState = pagerScrollState
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!clickable)
            return super.onTouchEvent(event)
        var pointX = startX
        val xTouch: Int
        val yTouch: Int
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                xTouch = event.getX(0).toInt()
                yTouch = event.getY(0).toInt()
                for (i in 0 until stepsCount) {
                    if (Math.abs(xTouch - pointX) < radius + 5 && Math.abs(yTouch - pointY) < radius + 5) {
                        if (!withViewpager) {
                            setCurrentStepPosition(i)
                        }

                        if (onClickListener != null) {
                            onClickListener!!.onClick(i)
                        }
                    }
                    pointX += stepDistance
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pointY = if (!textBottomAlign)
            height * 3 / 4
        else
            height / 4

        startX = radius * 2
        endX = width - radius * 2
        stepDistance = (endX - startX) / (stepsCount - 1)
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )

        pointY = if (!textBottomAlign)
            height * 3 / 4
        else
            height / 4

        startX = radius * 2
        endX = width - radius * 2
        stepDistance = (endX - startX) / (stepsCount - 1)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }

        if (result < desiredSize) {
            Log.e("StepView", "The view is too small, the content might get cut")
        }
        return result
    }

    inner class ViewPagerOnChangeListener(private val stepIndicator: StepView) :
        ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (!disablePageChange) {
                stepIndicator.setOffset(positionOffset, position)
            }
        }

        override fun onPageSelected(position: Int) {
            if (!disablePageChange) {
                stepIndicator.setCurrentStepPosition(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            stepIndicator.setPagerScrollState(state)
        }

    }

    inner class ViewPagerOnSelectedListener(private val mViewPager: ViewPager) : OnClickListener {

        override fun onClick(position: Int) {
            disablePageChange = true
            setCurrentStepPosition(position)
            mViewPager.currentItem = position
        }
    }


    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.mLineHeight = this.mLineHeight
        ss.radius = this.radius
        ss.strokeWidth = this.strokeWidth
        ss.currentStepPosition = this.currentStepPosition
        ss.stepsCount = this.stepsCount
        ss.backgroundColor = this.bgColor
        ss.stepColor = this.stepColor
        ss.currentColor = this.currentColor
        ss.textColor = this.textColor
        ss.secondaryTextColor = this.secondaryTextColor
        ss.pageActiveTitleColor = this.pageActiveTitleColor
        ss.pageInActiveTitleColor = this.pageInActiveTitleColor
        ss.pageTitleId = this.pageTitleId
        ss.titleClickable = if (this.clickable) 1 else 0
        ss.textTypeFace = this.textTypeFace
        ss.textBottomAlign = if (this.textBottomAlign) 1 else 0
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        this.mLineHeight = state.mLineHeight
        this.radius = state.radius
        this.strokeWidth = state.strokeWidth
        this.currentStepPosition = state.currentStepPosition
        this.stepsCount = state.stepsCount
        this.bgColor = state.backgroundColor
        this.stepColor = state.stepColor
        this.currentColor = state.currentColor
        this.textColor = state.textColor
        this.secondaryTextColor = state.secondaryTextColor
        this.titleTextSize = state.titleTextSize.toFloat()
        this.pageActiveTitleColor = state.pageActiveTitleColor
        this.pageInActiveTitleColor = state.pageInActiveTitleColor
        this.pageTitleId = state.pageTitleId
        this.clickable = state.titleClickable != 0
        this.textTypeFace = state.textTypeFace
        this.textBottomAlign = state.textBottomAlign != 0
    }

    internal class SavedState : BaseSavedState {
        var radius: Int = 0
        var mLineHeight: Float = 0.toFloat()
        var strokeWidth: Int = 0
        var currentStepPosition: Int = 0
        var stepsCount: Int = 0
        var backgroundColor: Int = 0
        var stepColor: Int = 0
        var currentColor: Int = 0
        var textColor: Int = 0
        var secondaryTextColor: Int = 0
        var titleTextSize: Int = 0
        var pageStrokeAlpha: Int = 0
        var pageTitleId: Int = 0
        var titleClickable: Int = 0

        var pageActiveTitleColor: Int = 0
        var pageInActiveTitleColor: Int = 0
        var textTypeFace: Int = 0
        var textBottomAlign: Int = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            mLineHeight = `in`.readFloat()
            radius = `in`.readInt()
            strokeWidth = `in`.readInt()
            currentStepPosition = `in`.readInt()
            stepsCount = `in`.readInt()
            backgroundColor = `in`.readInt()
            stepColor = `in`.readInt()
            currentColor = `in`.readInt()
            textColor = `in`.readInt()
            secondaryTextColor = `in`.readInt()
            titleTextSize = `in`.readInt()
            pageActiveTitleColor = `in`.readInt()
            pageInActiveTitleColor = `in`.readInt()
            pageStrokeAlpha = `in`.readInt()
            pageTitleId = `in`.readInt()
            titleClickable = `in`.readInt()
            textTypeFace = `in`.readInt()
            textBottomAlign = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeFloat(mLineHeight)
            dest.writeInt(radius)
            dest.writeInt(strokeWidth)
            dest.writeInt(currentStepPosition)
            dest.writeInt(stepsCount)
            dest.writeInt(backgroundColor)
            dest.writeInt(stepColor)
            dest.writeInt(currentColor)
            dest.writeInt(textColor)
            dest.writeInt(secondaryTextColor)
            dest.writeInt(titleTextSize)
            dest.writeInt(pageActiveTitleColor)
            dest.writeInt(pageInActiveTitleColor)
            dest.writeInt(pageStrokeAlpha)
            dest.writeInt(pageTitleId)
            dest.writeInt(titleClickable)
            dest.writeInt(textTypeFace)
            dest.writeInt(textBottomAlign)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_STEP_RADIUS = 10   //DP
        private const val DEFAULT_STOKE_WIDTH = 6  //DP
        private const val DEFAULT_STEP_COUNT = 4  //DP
        private val DEFAULT_BACKGROUND_COLOR = R.color.sv_background_color
        private val DEFAULT_STEP_COLOR = R.color.sv_step_color
        private val DEFAULT_CURRENT_STEP_COLOR = R.color.sv_step_color
        private val DEFAULT_INACTIVE_TITLE = R.color.sv_background_color
        private val DEFAULT_TEXT_COLOR = R.color.sv_step_color
        private val DEFAULT_SECONDARY_TEXT_COLOR = R.color.sv_background_color
        const val DEFAULT_LINE_HEIGHT = 6.0f
        const val DEFAULT_STROKE_ALPHA = 100
        private const val DEFAULT_TITLE_SIZE = 12
    }

}