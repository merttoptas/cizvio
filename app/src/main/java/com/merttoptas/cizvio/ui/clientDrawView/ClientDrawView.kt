package com.merttoptas.cizvio.ui.clientDrawView

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.model.DrawType
import com.merttoptas.cizvio.model.GetDraw
import com.merttoptas.cizvio.model.Message
import com.merttoptas.cizvio.model.initialData
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
import java.util.LinkedHashMap


class ClientDrawView (context: Context, attrs: AttributeSet? = null,
                      defStyleAttr: Int = 0)  : View(context, attrs), AbstractView.View{
    lateinit var mSocket: Socket
    private var mPaint = Paint()
    private var mPath = MyPath()
    private var mPaths = LinkedHashMap<MyPath, PaintOptions>()

    private var mPaintOptions = PaintOptions()
    private var mColors = R.color.color_black.toString()
    private var centerX = 0
    private var centerY = 0
    private var mCurX = 0f
    private var mCurY = 0f
    private var isDraw = true
    private var mStrokeWidth = 8f
    private var mIsStrokeWidthBarEnabled = false
    val gson = Gson()

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
    override fun createSocketServer() {
        CoroutineScope(Dispatchers.Main).launch {
            acceptSocket()
            while (mSocket.connected()) {
                Log.d("isConnected", "isConnected")
            }
        }    }

    override suspend fun acceptSocket() {
        CoroutineScope(Dispatchers.Default).async {
            try {
                mSocket = IO.socket("https://fast-plains-58306.herokuapp.com")
                Log.d("success", "Connnection")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("fail", "Failed to connect")
            }

            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on("drawData", onUpdateDraw)
            mSocket.on("updateDraw", onUpdateDraw)


        }.await()
    }
    var onConnect = Emitter.Listener {
        val data = initialData("a", "a")
        val jsonData = gson.toJson(data)
        mSocket.emit("subscribe", jsonData)
    }

    var onUpdateDraw = Emitter.Listener {
        val draw: GetDraw = gson.fromJson(it[0].toString(), GetDraw::class.java)
        draw.viewType = DrawType.DRAW_PARTNER.index

        mCurX = draw.x1
        mCurY = draw.y1
        isDraw = draw.isDraw
        mColors = draw.colors
        mStrokeWidth = draw.strokeWidth * centerX

        if (isDraw) {
            actionUp()
            setColor(mColors.toInt())
            setStrokeWidth(mStrokeWidth)
            mPath.moveTo( resizeX(mCurX), resizeY(mCurY))
            invalidate()
        } else {
            mPath.lineTo(resizeX(mCurX), resizeY(mCurY))
            mPath.moveTo(resizeX(mCurX), resizeY(mCurY))
            mPath.quadTo(resizeX(mCurX),  resizeY(mCurY),resizeX(mCurX) , resizeY(mCurY))
            invalidate()
        }
    }

    override fun changePaint(paintOptions: PaintOptions) {
        mPaint.color = if (paintOptions.isEraserOn) paintOptions.color else Color.WHITE
        mPaint.strokeWidth = paintOptions.strokeWidth
    }

    override fun setColor(newColor: Int) {
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

    private fun actionUp() {

        mPaths[mPath] = mPaintOptions
        mPath = MyPath()
        mPaintOptions = PaintOptions(
            mPaintOptions.color,
            mPaintOptions.strokeWidth,
            mPaintOptions.alpha,
            mPaintOptions.isEraserOn
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isDraw) {
            changePaint(mPaintOptions)
            canvas!!.drawPath(mPath, mPaint)

        } else {
            for ((key, value) in mPaths) {
                changePaint(value)
                canvas!!.drawPath(key, mPaint)
            }
            changePaint(mPaintOptions)
            canvas!!.drawPath(mPath, mPaint)
        }
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

    private fun resizeY(mCurY: Float): Float {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)

        val y_ratio = mCurY * centerY
        return y_ratio
    }

   private fun resizeX(mCurX: Float): Float {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
            .defaultDisplay
            .getMetrics(displayMetrics)

        val x_ratio = mCurX * centerX
        return x_ratio
    }


}