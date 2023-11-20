package com.serdar.whell.wheel

import android.graphics.Bitmap

private fun Bitmap?.runIfSafe(function: (Bitmap) -> Unit) {
    this ?: return

    if (isRecycled.not()) {
        function(this)
    }
}