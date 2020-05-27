package developer.carlos.silva.network

import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.interfaces.AnimeLoaderStringListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.database.models.DataEpisode
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
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
                val sinopse = doc.select("#sinopse2").first()?.text() ?: ""
                val capaAnime = doc.select("#capaAnime")
                val img = capaAnime.select("img")
                val capaLink = img.attr("src").toString()
                val pagAniListaContainer = doc.select(".pagAniListaContainer ")
                val epiSubContainer = pagAniListaContainer.select("a")
                val videoId = Utils.getDigitFromString(link)
                val list = epiSubContainer.map { Anime(it) }
                    .map { element ->
                        DataEpisode(
                            id = element.videoId,
                            foreignKey = videoId,
                            link = element.link,
                            title = element.title
                        )
                    }.toMutableList()

                val objt = DataAnime()
                objt.id = videoId
                objt.lista = list
                objt.capa = capaLink
                objt.sinopse = sinopse.toString()

                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(mutableListOf(objt))
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

        fun loadEpisodes(
            animeLoaderListener: AnimeLoaderListener?,
            url: String = UrlSystem.episodes()
        ) {
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

        fun pagination(
            animeLoaderListener: AnimeLoaderListener?,
            url: String = UrlSystem.episodes()
        ) {
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

        fun loadCalendar(animeLoaderListener: AnimeLoaderListener?) {
            var count = 0
            Thread {
                val list = mutableListOf<Any>()
                val titles = mutableListOf(
                    "SEGUNDA",
                    "TERÇA",
                    "QUARTA",
                    "QUINTA",
                    "SEXTA",
                    "SÁBADO",
                    "DOMINGO"
                )
                val doc = Jsoup.connect(UrlSystem.calendar()).get()
                val mwidth = doc.select(".mwidth")
                val calendarioPagContainer = mwidth.select(".calendarioPagContainer")

                calendarioPagContainer.forEach {
                    list.add(titles[count])
                    list.addAll(it.select(".aniItemList").map { element -> Anime(element) })
                    count++
                }

                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderListener?.onLoad(list)
                }
            }.start()
        }

        fun loadCapa(animeLoaderStringListener: AnimeLoaderStringListener?, url: String) {
            val doc = Jsoup.connect(url).get()
            val capaAnime = doc.select("#capaAnime")
            val img = capaAnime.select("img")
            val result = img.attr("src").toString()
            Thread {
                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderStringListener?.onLoad(result)
                }
            }.start()
        }
    }
}