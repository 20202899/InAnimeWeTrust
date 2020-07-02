package developer.carlos.silva.network

class UrlSystem {
    companion object {
        private const val MAIN_URL = "https://www.anitube.site/?__cf_chl_jschl_tk__="
        private const val SEARCH_URL = "https://www.anitube.site/?s=%s"
        private const val EPISODES_URL = "https://www.anitube.site/lista-de-episodios/"
        private const val CALENDAR_URL = "https://www.anitube.site/calendario/"
        private const val GENRE_URL = "https://www.anitube.site/lista-de-generos-online/"

        fun get() = MAIN_URL
        fun search(label: String?) = String.format(SEARCH_URL, label)
        fun episodes() = EPISODES_URL
        fun calendar() = CALENDAR_URL
        fun genres() = GENRE_URL
    }
}