package developer.carlos.silva.network

class UrlSystem {
    companion object {
        private const val MAIN_URL = "https://www.anitube.site/"
        private const val SEARCH_URL = "https://www.anitube.site/?s=%s"
        private const val EPISODES_URL = "lista-de-episodios/"
        private const val CALENDAR_URL = "calendario/"
        fun get() = MAIN_URL
        fun search(label: String?) = String.format(SEARCH_URL, label)
        fun episodes() = MAIN_URL + EPISODES_URL
        fun calendar() = MAIN_URL + CALENDAR_URL
    }
}