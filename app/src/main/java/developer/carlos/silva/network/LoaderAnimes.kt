package developer.carlos.silva.network

import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.interfaces.AnimeLoaderStringListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.database.models.DataEpisode
import developer.carlos.silva.interfaces.AnimeLoaderGenreListener
import developer.carlos.silva.models.Genre
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import org.jsoup.Jsoup

class LoaderAnimes {
    companion object {

        fun loadAnimes(animeLoaderListener: AnimeLoaderListener?) {
            Thread {
                try {
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
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }

            }.start()
        }

        fun loadAnimes(animeLoaderListener: AnimeLoaderListener?, link: String) {
            Thread {
                try {
                    var count = 0
                    val doc = Jsoup.connect(link).get()
                    val boxAnimeSobreLinhas =
                        doc.select(".boxAnimeSobreLinha").map { it.text() }
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
                                title = element.title,
                                order = count++
                            )
                        }.toMutableList()

                    val objt = DataAnime()
                    objt.id = videoId
                    objt.infos.addAll(boxAnimeSobreLinhas)
                    objt.lista = list
                    objt.capa = capaLink
                    objt.sinopse = sinopse

                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf(objt))
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun search(animeLoaderListener: AnimeLoaderListener?, label: String?) {
            Thread {
                try {
                    val doc = Jsoup.connect(UrlSystem.search(label)).get()
                    val searchPagContainer = doc.select(".searchPagContainer ")
                    val epiSubContainer = searchPagContainer.select(".aniItem")
                    val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(list)
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun loadEpisodes(animeLoaderListener: AnimeLoaderListener?) {
            Thread {
                try {
                    val doc = Jsoup.connect(UrlSystem.episodes()).get()
                    val episodiosPagContainer = doc.select(".episodiosPagContainer")
                    val epiSubContainer = episodiosPagContainer.select(".epiItem")
                    val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(list)
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun loadEpisodes(
            animeLoaderListener: AnimeLoaderListener?,
            url: String = UrlSystem.episodes()
        ) {
            Thread {
                try {
                    val doc = Jsoup.connect(url).get()
                    val episodiosPagContainer = doc.select(".episodiosPagContainer")
                    val epiSubContainer = episodiosPagContainer.select(".epiItem")
                    val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(list)
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun pagination(
            animeLoaderListener: AnimeLoaderListener?,
            url: String = UrlSystem.episodes()
        ) {
            Thread {
                try {
                    val doc = Jsoup.connect(url).get()
                    val centerPagination = doc.select(".centerPagination")
                    val epiSubContainer = centerPagination.select(".paginacao")
                        .select("a")
                    val list = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(list)
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun loadCalendar(animeLoaderListener: AnimeLoaderListener?) {
            var count = 0
            Thread {
                try {
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
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }

        fun loadGenres(animeLoaderGenreListener: AnimeLoaderGenreListener?) {
            Thread {
                val doc = Jsoup.connect(UrlSystem.genres()).get()
                val generosPagContainer = doc.select(".generosPagContainer")
                val aElement = generosPagContainer.select("a")
                val result = aElement
                    .map { Genre(it.text(), it.attr("href")) }
                    .toMutableList()

                MainController.getInstance()?.getHandler()?.post {
                    animeLoaderGenreListener?.onLoad(result)
                }

            }.start()
        }

        fun loadAnimesByGenre(
            animeLoaderListener: AnimeLoaderListener?,
            url: String = UrlSystem.episodes()
        ) {
            Thread {
                try {
                    val doc = Jsoup.connect(url).get()
                    val searchPagContainer = doc.select(".searchPagContainer")
                    val centerPagination = doc.select(".centerPagination")
                    val epiSubContainer = centerPagination.select(".paginacao")
                        .select("a")
                    val animes = searchPagContainer.select(".aniItem")
                        .map { Anime(it) }.toMutableList<Any>()
                    val paginas = epiSubContainer.map { Anime(it) }.toMutableList<Any>()
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf(animes, paginas))
                    }
                } catch (e: Exception) {
                    MainController.getInstance()?.getHandler()?.post {
                        animeLoaderListener?.onLoad(mutableListOf())
                    }
                }
            }.start()
        }
    }
}