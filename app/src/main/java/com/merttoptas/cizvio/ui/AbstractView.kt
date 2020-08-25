package com.merttoptas.cizvio.ui

import com.merttoptas.cizvio.utils.widget.PaintOptions

interface AbstractView {
    interface View {

        fun createSocketServer()

        suspend fun acceptSocket()

        fun changePaint(paintOptions: PaintOptions)

        fun setColor(newColor: Int)

        fun setStrokeWidth(newStrokeWidth: Float)

    }

}