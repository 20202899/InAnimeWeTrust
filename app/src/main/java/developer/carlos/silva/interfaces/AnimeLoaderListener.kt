package developer.carlos.silva.interfaces

import developer.carlos.silva.models.Anime

interface AnimeLoaderListener {
    fun onLoad(animes: MutableList<Any>)
}