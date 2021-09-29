package ir.majidkhosravi.countdown.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import ir.majidkhosravi.countdown.R
import ir.majidkhosravi.countdown.utils.DimenUtils.dp2px
import ir.majidkhosravi.countdown.utils.DimenUtils.sp2px


class DonutProgressView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(context, attrs, defStyleAttr) {

    private var finishedPaint: Paint? = null
    private var unfinishedPaint: Paint? = null
    private var innerCirclePaint: Paint? = null

    protected var textPaint: TextPaint? = null
    protected var textPaintDigit: TextPaint? = null

    protected var suffixPaint: Paint? = null
    protected var innerBottomTextPaint: Paint? = null

    private val finishedOuterRect = RectF()
    private val unfinishedOuterRect = RectF()

    var attributeResourceId = 0
    var isShowText = false
    private var textSize = 0f
    private var textColor = 0
    private var innerBottomTextColor = 0
    private var progress = 0f
    private var max = 0
    private var finishedStrokeColor = 0
    private var unfinishedStrokeColor = 0
    private var startingDegree = 0
    private var finishedStrokeWidth = 0f
    private var unfinishedStrokeWidth = 0f
    private var innerBackgroundColor = 0
    private var prefixText: String? = ""
    private var suffixText: String? = ""
    private var text: String? = null
    private var innerBottomTextSize = 0f
    private var innerBottomText: String? = null
    private var innerBottomTextHeight = 0f
    private val default_stroke_width: Float
    private val default_finished_color: Int = Color.rgb(240, 40, 103)
    private val default_unfinished_color: Int = Color.rgb(85, 85, 85)
    private val default_text_color: Int = Color.rgb(255, 255, 255)
    private val default_inner_bottom_text_color: Int = Color.rgb(66, 145, 241)
    private val default_inner_background_color: Int = Color.TRANSPARENT
    private val default_max = 60
    private val default_startingDegree = -90
    private val default_text_size: Float
    private val default_inner_bottom_text_size: Float
    private val min_size: Int
    private var default_font_face: Int

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    protected fun initPainters() {
        if (isShowText) {
            val textSizeMeasured: Float = (width / 3.2).toFloat()
            textPaint = TextPaint()
            textPaintDigit = TextPaint()
            suffixPaint = TextPaint()
            innerBottomTextPaint = TextPaint()
            finishedPaint = Paint()
            unfinishedPaint = Paint()
            innerCirclePaint = Paint()

            textPaint?.let {
                it.color = textColor
                it.textSize = textSizeMeasured
                it.isAntiAlias = true
            }
            textPaintDigit?.let {
                it.color = textColor
                it.textSize = textSizeMeasured
                it.isAntiAlias = true
            }
            suffixPaint?.let {
                it.color = textColor
                it.textSize = (textSizeMeasured / 1.9).toFloat()
                it.isAntiAlias = true
            }
            innerBottomTextPaint?.let {
                it.color = innerBottomTextColor
                it.textSize = innerBottomTextSize
                it.isAntiAlias = true
            }
            setFontFace(default_font_face)
        }
        finishedPaint?.let {
            it.color = finishedStrokeColor
            it.style = Paint.Style.STROKE
            it.isAntiAlias = true
            it.strokeWidth = finishedStrokeWidth
        }
        unfinishedPaint?.let {
            it.color = unfinishedStrokeColor
            it.style = Paint.Style.STROKE
            it.isAntiAlias = true
            it.strokeWidth = unfinishedStrokeWidth
        }

        innerCirclePaint?.let {
            it.color = innerBackgroundColor
            it.isAntiAlias = true
        }
    }

    private fun setFontFace(typeface: Typeface?) {
        textPaint?.typeface = typeface
        textPaintDigit?.typeface = typeface
        suffixPaint?.typeface = typeface
        innerBottomTextPaint?.typeface = typeface
    }

    fun setFontFace(fontRes: Int) {
        if (default_font_face != fontRes) default_font_face = fontRes
        setFontFace(ResourcesCompat.getFont(context, fontRes))
    }

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()
        val a: TypedArray =
            context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    protected fun initByAttributes(attributes: TypedArray) {
        finishedStrokeColor =
            attributes.getColor(R.styleable.DonutProgress_donut_finished_color, fetchAccentColor())
        unfinishedStrokeColor = attributes.getColor(
            R.styleable.DonutProgress_donut_unfinished_color,
            default_unfinished_color
        )
        isShowText = attributes.getBoolean(R.styleable.DonutProgress_donut_show_text, true)
        attributeResourceId =
            attributes.getResourceId(R.styleable.DonutProgress_donut_inner_drawable, 0)
        setMax(attributes.getInt(R.styleable.DonutProgress_donut_max, default_max))
        setProgress(attributes.getFloat(R.styleable.DonutProgress_donut_progress, 0f))
        finishedStrokeWidth = attributes.getDimension(
            R.styleable.DonutProgress_donut_finished_stroke_width,
            default_stroke_width
        )
        unfinishedStrokeWidth = attributes.getDimension(
            R.styleable.DonutProgress_donut_unfinished_stroke_width,
            default_stroke_width
        )
        if (isShowText) {
            if (attributes.getString(R.styleable.DonutProgress_donut_prefix_text) != null) {
                prefixText = attributes.getString(R.styleable.DonutProgress_donut_prefix_text)
            }
            if (attributes.getString(R.styleable.DonutProgress_donut_suffix_text) != null) {
                suffixText = attributes.getString(R.styleable.DonutProgress_donut_suffix_text)
            }
            if (attributes.getString(R.styleable.DonutProgress_donut_text) != null) {
                text = attributes.getString(R.styleable.DonutProgress_donut_text)
            }
            textColor =
                attributes.getColor(R.styleable.DonutProgress_donut_text_color, default_text_color)
            textSize = attributes.getDimension(
                R.styleable.DonutProgress_donut_text_size,
                default_text_size
            )
            innerBottomTextSize = attributes.getDimension(
                R.styleable.DonutProgress_donut_inner_bottom_text_size,
                default_inner_bottom_text_size
            )
            innerBottomTextColor = attributes.getColor(
                R.styleable.DonutProgress_donut_inner_bottom_text_color,
                default_inner_bottom_text_color
            )
            innerBottomText =
                attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text)
        }
        innerBottomTextSize = attributes.getDimension(
            R.styleable.DonutProgress_donut_inner_bottom_text_size,
            default_inner_bottom_text_size
        )
        innerBottomTextColor = attributes.getColor(
            R.styleable.DonutProgress_donut_inner_bottom_text_color,
            default_inner_bottom_text_color
        )
        innerBottomText = attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text)
        startingDegree = attributes.getInt(
            R.styleable.DonutProgress_donut_circle_starting_degree,
            default_startingDegree
        )
        innerBackgroundColor = attributes.getColor(
            R.styleable.DonutProgress_donut_background_color,
            default_inner_background_color
        )
    }


    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    fun getFinishedStrokeWidth(): Float {
        return finishedStrokeWidth
    }

    fun setFinishedStrokeWidth(finishedStrokeWidth: Float) {
        this.finishedStrokeWidth = finishedStrokeWidth
        invalidate()
    }

    fun getUnfinishedStrokeWidth(): Float {
        return unfinishedStrokeWidth
    }

    fun setUnfinishedStrokeWidth(unfinishedStrokeWidth: Float) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth
        invalidate()
    }

    private val progressAngle: Float
        private get() = getProgress() / max.toFloat() * 360f

    fun getProgress(): Float {
        return progress
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        setText(progress.toString())
        if (this.progress > getMax()) {
            this.progress %= getMax().toFloat()
        }
        invalidate()
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
        invalidate()
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        if (max > 0) {
            this.max = max
            invalidate()
        }
    }

    fun getTextSize(): Float {
        return textSize
    }

    fun setTextSize(textSize: Float) {
        this.textSize = textSize
        invalidate()
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
        invalidate()
    }

    fun getFinishedStrokeColor(): Int {
        return finishedStrokeColor
    }

    fun setFinishedStrokeColor(finishedStrokeColor: Int) {
        this.finishedStrokeColor = finishedStrokeColor
        invalidate()
    }

    fun getUnfinishedStrokeColor(): Int {
        return unfinishedStrokeColor
    }

    fun setUnfinishedStrokeColor(unfinishedStrokeColor: Int) {
        this.unfinishedStrokeColor = unfinishedStrokeColor
        invalidate()
    }

    fun getSuffixText(): String? {
        return suffixText
    }

    fun setSuffixText(suffixText: String?) {
        this.suffixText = suffixText
        invalidate()
    }

    fun getPrefixText(): String? {
        return prefixText
    }

    fun setPrefixText(prefixText: String?) {
        this.prefixText = prefixText
        invalidate()
    }

    fun getInnerBackgroundColor(): Int {
        return innerBackgroundColor
    }

    fun setInnerBackgroundColor(innerBackgroundColor: Int) {
        this.innerBackgroundColor = innerBackgroundColor
        invalidate()
    }

    fun getInnerBottomText(): String? {
        return innerBottomText
    }

    fun setInnerBottomText(innerBottomText: String?) {
        this.innerBottomText = innerBottomText
        invalidate()
    }

    fun getInnerBottomTextSize(): Float {
        return innerBottomTextSize
    }

    fun setInnerBottomTextSize(innerBottomTextSize: Float) {
        this.innerBottomTextSize = innerBottomTextSize
        invalidate()
    }

    fun getInnerBottomTextColor(): Int {
        return innerBottomTextColor
    }

    fun setInnerBottomTextColor(innerBottomTextColor: Int) {
        this.innerBottomTextColor = innerBottomTextColor
        invalidate()
    }

    fun getStartingDegree(): Int {
        return startingDegree
    }

    fun setStartingDegree(startingDegree: Int) {
        this.startingDegree = startingDegree
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec))

        //TODO calculate inner circle height and then position bottom text at the bottom (3/4)
        innerBottomTextHeight = (height - height * 3 / 4).toFloat()
    }

    private fun measure(measureSpec: Int): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = min_size
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val delta = Math.max(finishedStrokeWidth, unfinishedStrokeWidth)
        finishedOuterRect[delta, delta, width - delta] = height - delta
        unfinishedOuterRect[delta, delta, width - delta] = height - delta
        val innerCircleRadius: Float =
            (width - Math.min(finishedStrokeWidth, unfinishedStrokeWidth) + Math.abs(
                finishedStrokeWidth - unfinishedStrokeWidth
            )) / 2f
        innerCirclePaint?.let {
            canvas.drawCircle(
                width / 2.0f,
                height / 2.0f,
                innerCircleRadius,
                it
            )
        }
        finishedPaint?.let {
            canvas.drawArc(
                finishedOuterRect,
                getStartingDegree().toFloat(), progressAngle, false, it
            )
        }
        unfinishedPaint?.let {
            canvas.drawArc(
                unfinishedOuterRect,
                getStartingDegree() + progressAngle,
                360 - progressAngle,
                false,
                it
            )
        }
        if (isShowText) {
            val text = if (text != null) text else prefixText + progress
            if (!TextUtils.isEmpty(text)) {
                val textHeight: Float = textPaint!!.descent() + textPaint!!.ascent()
                val suffixHeight: Float = suffixPaint!!.descent() + suffixPaint!!.ascent()
                val textSeatLine: Float = width / 2.0f - textHeight / 4
                val textSeatLinee: Float = width / 2.0f
                if (text!!.length > 1) {
                    val digitOne = text[0].toString()
                    val digitTwo = text[1].toString()
                    textPaint?.textAlign = Paint.Align.CENTER
                    textPaintDigit?.textAlign = Paint.Align.CENTER

                    textPaint?.let {
                        canvas.drawText(
                            digitOne,
                            width / 2.0f - width / 7.0f, textSeatLine, it
                        )
                    }
                    textPaintDigit?.let {
                        canvas.drawText(
                            digitTwo,
                            width / 2.0f + width / 7.0f, textSeatLine, it
                        )
                    }
                } else {
                    textPaint?.let {
                        canvas.drawText(
                            text,
                            (width - it.measureText(text)) / 2.0f,
                            textSeatLine,
                            it
                        )
                    }
                }
                if (!suffixText.isNullOrEmpty()) {
                    suffixPaint?.let {
                        suffixText?.let { suffixText ->
                            canvas.drawText(
                                suffixText, (width - it.measureText(suffixText)) / 2.0f,
                                textSeatLinee - suffixHeight * 2.5f, it
                            )
                        }
                    }
                }

            }
            if (!TextUtils.isEmpty(getInnerBottomText())) {
                innerBottomTextPaint?.let {
                    it.textSize = innerBottomTextSize
                    textPaint?.let { textPaint ->
                        val bottomTextBaseline: Float =
                            height - innerBottomTextHeight - (textPaint.descent() + textPaint.ascent()) / 2

                        getInnerBottomText()?.let { innerBottomText ->
                            canvas.drawText(
                                innerBottomText,
                                (width - it.measureText(getInnerBottomText())) / 2.0f,
                                bottomTextBaseline,
                                it
                            )
                        }
                    }
                }
            }
        }
        if (attributeResourceId != 0) {
            val bitmap = BitmapFactory.decodeResource(resources, attributeResourceId)
            canvas.drawBitmap(
                bitmap,
                (width - bitmap.width) / 2.0f,
                (height - bitmap.height) / 2.0f,
                null
            )
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor())
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize())
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE, getInnerBottomTextSize())
        bundle.putFloat(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor().toFloat())
        bundle.putString(INSTANCE_INNER_BOTTOM_TEXT, getInnerBottomText())
        bundle.putInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR, getInnerBottomTextColor())
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor())
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor())
        bundle.putInt(INSTANCE_MAX, getMax())
        bundle.putInt(INSTANCE_STARTING_DEGREE, getStartingDegree())
        bundle.putFloat(INSTANCE_PROGRESS, getProgress())
        bundle.putString(INSTANCE_SUFFIX, getSuffixText())
        bundle.putString(INSTANCE_PREFIX, getPrefixText())
        bundle.putString(INSTANCE_TEXT, getText())
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth())
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth())
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor())
        bundle.putInt(INSTANCE_INNER_DRAWABLE, attributeResourceId)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR)
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE)
            innerBottomTextSize = bundle.getFloat(INSTANCE_INNER_BOTTOM_TEXT_SIZE)
            innerBottomText = bundle.getString(INSTANCE_INNER_BOTTOM_TEXT)
            innerBottomTextColor = bundle.getInt(INSTANCE_INNER_BOTTOM_TEXT_COLOR)
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR)
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR)
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH)
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH)
            innerBackgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR)
            attributeResourceId = bundle.getInt(INSTANCE_INNER_DRAWABLE)
            initPainters()
            setMax(bundle.getInt(INSTANCE_MAX))
            setStartingDegree(bundle.getInt(INSTANCE_STARTING_DEGREE))
            setProgress(bundle.getFloat(INSTANCE_PROGRESS))
            prefixText = bundle.getString(INSTANCE_PREFIX)
            suffixText = bundle.getString(INSTANCE_SUFFIX)
            text = bundle.getString(INSTANCE_TEXT)
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setDonut_progress(percent: String) {
        if (!TextUtils.isEmpty(percent)) {
            setProgress(percent.toInt().toFloat())
        }
    }

    companion object {
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_TEXT_COLOR = "text_color"
        private const val INSTANCE_TEXT_SIZE = "text_size"
        private const val INSTANCE_TEXT = "text"
        private const val INSTANCE_INNER_BOTTOM_TEXT_SIZE = "inner_bottom_text_size"
        private const val INSTANCE_INNER_BOTTOM_TEXT = "inner_bottom_text"
        private const val INSTANCE_INNER_BOTTOM_TEXT_COLOR = "inner_bottom_text_color"
        private const val INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color"
        private const val INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color"
        private const val INSTANCE_MAX = "max"
        private const val INSTANCE_PROGRESS = "progress"
        private const val INSTANCE_SUFFIX = "suffix"
        private const val INSTANCE_PREFIX = "prefix"
        private const val INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width"
        private const val INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width"
        private const val INSTANCE_BACKGROUND_COLOR = "inner_background_color"
        private const val INSTANCE_STARTING_DEGREE = "starting_degree"
        private const val INSTANCE_INNER_DRAWABLE = "inner_drawable"
    }

    init {
        default_text_size = resources.dp2px(14f)
        min_size = resources.dp2px(100f).toInt()
        default_stroke_width = resources.dp2px(5f)
        default_inner_bottom_text_size = resources.sp2px(18f)
        default_font_face = R.font.iran_yekan_bold
        val attributes: TypedArray = context.theme
            .obtainStyledAttributes(attrs, R.styleable.DonutProgress, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()
        initPainters()
    }
}
