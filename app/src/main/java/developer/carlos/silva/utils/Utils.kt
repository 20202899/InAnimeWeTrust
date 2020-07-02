package developer.carlos.silva.utils

import android.content.res.Resources
import android.util.TypedValue
import developer.carlos.silva.singletons.MainController
import java.lang.reflect.Type


class Utils {
    companion object {
        fun <T> uncodedScriptText(s: String, type: Type): T {
            val r = s.replace("\\s".toRegex(), "")
            val gson = MainController.getInstance()?.mGson!!
            val index = r.indexOf("sources:")
            val lastIndex = r.lastIndexOf("],")
            val result = r.substring(index, lastIndex)
            val nowIndex = result.indexOf("[")
            val nowLastIndex = result.lastIndexOf("]")
            val endless = result.substring(nowIndex, nowLastIndex + 1)
            return gson.fromJson(endless, type)
        }

        fun getDigitFromString(s: String) = "\\d+".toRegex().find(s)!!.groupValues.last().toInt()
        fun getAllDigitFromString(s: String) = s.replace("[^0-9]".toRegex(), "")

        fun dpToPx(dp: Float, r: Resources) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }
}