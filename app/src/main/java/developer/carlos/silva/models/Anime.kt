package developer.carlos.silva.models

import android.util.Log
import developer.carlos.silva.network.UrlSystem
import developer.carlos.silva.utils.Utils
import org.jsoup.nodes.Element
import java.io.Serializable

class Anime : Serializable{


    var videoId = 0
    var title = ""
    var timer = ""
    var imagePath = ""
    var link = ""

    constructor(element: Element) {
        val elementA = element.select("a")
        link = elementA.attr("href").toString()
        title = elementA.attr("title").toString()
        imagePath = element.select("img").attr("src")
        videoId = Utils.getDigitFromString(link)
    }

    constructor() {

    }

}