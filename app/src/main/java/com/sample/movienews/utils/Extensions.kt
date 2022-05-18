package com.sample.movienews.utils

import android.widget.TextView
import com.sample.movienews.utils.Constant.COLLAPSE_MAX_LINES

fun TextView.canBeExpanded(): Boolean {
    return this.lineCount > COLLAPSE_MAX_LINES
}
