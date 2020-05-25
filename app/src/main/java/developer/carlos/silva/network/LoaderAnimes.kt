package developer.carlos.silva.network

import android.os.Handler
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.singletons.MainController
import org.jsoup.Jsoup

class LoaderAnimes {
    companion object {
        fun loadAnimes(animeLoaderListener: AnimeLoaderListener?) {
            Thread {
                val doc = Jsoup.connect(UrlSystem.get()).get()
                val aniContainer = doc.select(".aniContainer")
                val epiSubContainer = doc.select(".epiSubContainer")
                val list = mutableListOf<Any>()

                list.addAll(aniContainer.map { element ->
                    element.select(".aniItem")
                        .map { Anime(it) }.toMutableList()
                }.toMutableList())

                list.addAll(2, epiSubContainer.map { element ->
                    element.select(".epiItem").map { Anime(it) }.toMutableList()
                }.toMutableList())

                list.add(0, "Tendência")
                list.add(2, "Recentes")
                list.add(4, "Disponível")
                list.add(6, "Lançamentos do Dia")

                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }

            }.start()
        }

        fun loadAnimes(animeLoaderListener: AnimeLoaderListener?, link: String) {
            Thread {
                val doc = Jsoup.connect(link).get()
                val pagAniListaContainer = doc.select(".pagAniListaContainer ")
                val epiSubContainer = pagAniListaContainer.select("a")
                val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }

        fun search(animeLoaderListener: AnimeLoaderListener?, label: String?) {
            Thread {
                val doc = Jsoup.connect(UrlSystem.search(label)).get()
                val searchPagContainer = doc.select(".searchPagContainer ")
                val epiSubContainer = searchPagContainer.select(".aniItem")
                val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }

        fun loadEpisodes(animeLoaderListener: AnimeLoaderListener?) {
            Thread {
                val doc = Jsoup.connect(UrlSystem.episodes()).get()
                val episodiosPagContainer = doc.select(".episodiosPagContainer")
                val epiSubContainer = episodiosPagContainer.select(".epiItem")
                val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }

        fun loadEpisodes(animeLoaderListener: AnimeLoaderListener?, url: String = UrlSystem.episodes()) {
            Thread {
                val doc = Jsoup.connect(url).get()
                val episodiosPagContainer = doc.select(".episodiosPagContainer")
                val epiSubContainer = episodiosPagContainer.select(".epiItem")
                val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }

        fun pagination(animeLoaderListener: AnimeLoaderListener?, url: String = UrlSystem.episodes()) {
            Thread {
                val doc = Jsoup.connect(url).get()
                val centerPagination = doc.select(".centerPagination")
                val epiSubContainer = centerPagination.select(".paginacao")
                    .select("a")
                val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }
    }
}