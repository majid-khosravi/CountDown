package ir.majidkhosravi.countdown.utils

import android.content.res.Resources

object DimenUtils {


    const val INVALID_VALUE = -1


    fun Resources.dp2px(dp: Float): Float {
        val scale: Float = this.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun Resources.sp2px(sp: Float): Float {
        val scale: Float = this.displayMetrics.scaledDensity
        return sp * scale
    }



}