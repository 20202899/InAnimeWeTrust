package developer.carlos.silva.interfaces

import developer.carlos.silva.models.Anime
import developer.carlos.silva.models.Genre

interface AnimeLoaderListener {
    fun onLoad(animes: MutableList<Any>)
}

interface AnimeLoaderStringListener {
    fun onLoad(link: String)
}

interface AnimeLoaderGenreListener {
    fun onLoad(genres: MutableList<Genre>)
}