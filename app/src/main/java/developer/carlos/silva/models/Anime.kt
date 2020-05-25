package developer.carlos.silva.models

import android.util.Log
import developer.carlos.silva.network.UrlSystem
import org.jsoup.nodes.Element
import java.io.Serializable

class Anime(element: Element) : Serializable{
    var videoId = 0
    var title = ""
    var timer = ""
    var imagePath = ""
    var link = ""

    init {
        val elementA = element.select("a")
        link = elementA.attr("href").toString()
        title = elementA.attr("title").toString()
        imagePath = element.select("img").attr("src")
        videoId = "\\d+".toRegex().find(link)!!.groupValues[0].toInt()
    }

}