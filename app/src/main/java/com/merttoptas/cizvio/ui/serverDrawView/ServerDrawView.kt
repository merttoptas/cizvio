package com.merttoptas.cizvio.ui.serverDrawView

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.model.*
import com.merttoptas.cizvio.ui.AbstractView
import com.merttoptas.cizvio.utils.widget.MyPath
import com.merttoptas.cizvio.utils.widget.PaintOptions
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.String
import java.util.*

class ServerDrawView (context:Context,  attrs: AttributeSet? = null,
                      defStyleAttr: Int = 0)  : View(context, attrs), AbstractView.View{

    private var mPaths = LinkedHashMap<MyPath, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<MyPath, PaintOptions>()
    private var mPaint = Paint()
    private var mPath = MyPath()
    private var mPaintOptions = PaintOptions()
    lateinit var mSocket: Socket
    private var centerX = 0
    private var centerY = 0
    private var mCurX = 0f
    private var mCurY = 0f
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    val gson: Gson = Gson()
    var getColors :Int ? = null
    var isDraw = false

    private var mIsStrokeWidthBarEnabled = false

    init {
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
            isDither = true
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        for ((key, value) in mPaths) {
            changePaint(value)
            canvas!!.drawPath(key, mPaint)
        }
        changePaint(mPaintOptions)
        canvas!!.drawPath(mPath, mPaint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val width = w
        val height = h
        val paddingLeft = getPaddingLeft()
        val paddingRight = getPaddingRight()
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom
        val usableWidth = width - (paddingLeft + paddingRight)
        val usableHeight = height  - (paddingTop + paddingBottom)
        centerX = paddingLeft + (usableWidth / 2)
        centerY = paddingTop + (usableHeight / 2)
    }
    override fun createSocketServer() {
        CoroutineScope(Dispatchers.Main).launch {
            acceptSocket()
            while (mSocket.connected()) {
                Log.d("isConnected", "isConnected")
            }
        }
    }

    override suspend fun acceptSocket() {
        CoroutineScope(Dispatchers.Default).async {
            try {
                mSocket = IO.socket("https://cryptic-castle-13142.herokuapp.com")
                Log.d("success", "Connnection")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("fail", "Failed to connect")
            }

            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on("drawData", onUpdateDraw)

        }.await()
    }

    var onUpdateDraw = Emitter.Listener {
        var x :Float = 0f
        val draw: GetDraw = gson.fromJson(it[0].toString(), GetDraw::class.java)
        draw.viewType = DrawType.DRAW_MINE.index
    }

    var onConnect = Emitter.Listener {
        val data = initialData("a","a")
        val jsonData = gson.toJson(data)
        mSocket.emit("subscribe", jsonData)
    }

    var onDrawData = Emitter.Listener {
        val drawData: SendDraw = gson.fromJson(it[0].toString(), SendDraw::class.java)

    }

    override fun changePaint(paintOptions: PaintOptions) {
        mPaint.color = if (paintOptions.isEraserOn) paintOptions.color else Color.WHITE
        mPaint.strokeWidth = paintOptions.strokeWidth
    }


    override fun setColor(newColor: Int) {
        getColors = newColor
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    override fun setStrokeWidth(newStrokeWidth: Float) {
        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()

        }
    }

    private fun sendDraw(x1:Float, y1:Float , colors:Int, strokeWith: Float, isDraw:Boolean) {
        val hexColor = String.format("#%06X", 0xFFFFFF and colors)

        val sendDrawData = SendDraw("a", "a", x1, y1, isDraw, strokeWith, hexColor)
        val jsonData = gson.toJson(sendDrawData)
        mSocket.emit("drawData", jsonData)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        motionTouchEventX = event!!.x
        motionTouchEventY = event!!.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                actionStart()
                mUndonePaths.clear()
            }
            MotionEvent.ACTION_MOVE -> actionMove()
            MotionEvent.ACTION_UP -> actionUp()
        }

        invalidate()
        return true
    }

    private fun actionStart() {
        val color = ResourcesCompat.getColor(resources,
            R.color.color_blue,null)

        mPath.reset()
        mPath.moveTo(motionTouchEventX, motionTouchEventY)
        mCurX = motionTouchEventX
        mCurY = motionTouchEventY
        isDraw = true
        sendDraw(
            x1 = resizeX(mCurX),
            y1 = resizeY(mCurY),
            colors = color,
            strokeWith = mPaintOptions.strokeWidth / centerX,
            isDraw = isDraw
        )
        //invalidate()

    }

    private fun actionMove() {
        isDraw = false
        val dx = Math.abs(motionTouchEventX - mCurX)
        val dy = Math.abs(motionTouchEventY - mCurY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            mPath.quadTo(
                mCurX,
                mCurY,
                (motionTouchEventX + mCurX) / 2,
                (motionTouchEventY + mCurY) / 2
            )
            mCurX = motionTouchEventX
            mCurY = motionTouchEventY
            sendDraw(
                x1 = resizeX(mCurX),
                y1 = resizeY(mCurY),
                colors = mPaintOptions.color,
                strokeWith = mPaintOptions.strokeWidth / centerX,
                isDraw = isDraw
            )
        }

       invalidate()
    }

    private fun actionUp() {

        mPaths[mPath] = mPaintOptions
        mPath = MyPath()
        mPaintOptions = PaintOptions(
            mPaintOptions.color,
            mPaintOptions.strokeWidth,
            mPaintOptions.alpha,
            mPaintOptions.isEraserOn
        )
        isDraw = false
        sendDraw(
            x1 = resizeX(mCurX),
            y1 = resizeY(mCurY),
            colors = mPaintOptions.color,
            strokeWith = mPaintOptions.strokeWidth / centerX,
            isDraw = isDraw
        )
    }

    private fun resizeX(mCurX: Float): Float {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)

        val x_ratio = mCurX / centerX

        return x_ratio
    }

    private fun resizeY(mCurY: Float): Float {

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)

        val y_ratio = mCurY / centerY
        return  y_ratio
    }

}