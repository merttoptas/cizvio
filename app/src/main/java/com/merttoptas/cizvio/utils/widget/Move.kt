package com.merttoptas.cizvio.utils.widget

import android.graphics.Path
import com.merttoptas.cizvio.utils.widget.Action
import java.io.Writer

class Move (val x: Float, val y:Float):
    Action {
    override fun perform(path: Path) {
        path.moveTo(x,y)
    }

    override fun perform(writer: Writer) {
        writer.write("M$x, $y")
    }

}