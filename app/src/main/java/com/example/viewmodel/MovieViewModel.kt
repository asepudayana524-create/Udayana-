package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val movieDao = MovieDatabase.getDatabase(application).movieDao()
    private val repository = MovieRepository(movieDao)

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reactive lists from DB
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val moviesList: StateFlow<List<MovieEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allMovies
            } else {
                repository.searchMovies(query)
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteMovies: StateFlow<List<MovieEntity>> = repository.favoriteMovies
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val watchlistMovies: StateFlow<List<MovieEntity>> = repository.watchlistMovies
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Chat AI Recommender State
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Halo! Saya adalah Asisten AI Perfilman Drama Indonesia. Tanyakan apa saja tentang film drama Indonesia, rekomendasi berdasarkan suasana hatimu, atau diskusi tentang sutradara dan karya terbaik kita!",
                isUser = false
            )
        )
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Seed default Indonesian drama films on startup
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedDefaultMoviesIfNeeded()
        }
    }

    // Toggle Favorite Action
    fun toggleFavorite(movie: MovieEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovie(movie.copy(isFavorite = !movie.isFavorite))
        }
    }

    // Toggle Watchlist Action
    fun toggleWatchlist(movie: MovieEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovie(movie.copy(isOnWatchlist = !movie.isOnWatchlist))
        }
    }

    // Save Personal Movie Notes/Review
    fun saveUserNotes(movie: MovieEntity, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovie(movie.copy(userNotes = notes))
        }
    }

    // Update query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // UI helper to get individual movie flow
    fun getMovieFlow(id: String): Flow<MovieEntity?> {
        return repository.getMovieById(id)
    }

    // Get current API key state
    fun isApiKeyPlaceholder(): Boolean {
        val apiKey = BuildConfig.GEMINI_API_KEY
        return apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER", ignoreCase = true)
    }

    // Send chat prompt to AI
    fun sendMessageToAi(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(text = prompt, isUser = true)
        _chatHistory.value = _chatHistory.value + userMessage
        _isAiLoading.value = true

        viewModelScope.launch {
            val responseText = callGeminiApi(prompt)
            _chatHistory.value = _chatHistory.value + ChatMessage(text = responseText, isUser = false)
            _isAiLoading.value = false
        }
    }

    // Call Direct REST Gemini API with structured System Instruction and fallback
    private suspend fun callGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
        if (isApiKeyPlaceholder()) {
            return@withContext getMockResponse(prompt)
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        val systemMessage = "Kamu adalah Pakar Sinema Indonesia yang ramah, berwawasan luas, dan objektif. Fokus utama kamu adalah membahas dan memberi rekomendasi seputar Film Drama Indonesia. Sebutkan judul film, sutradara, tahun rilis, dan ulasan singkat yang menggugah selera menonton."
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemMessage))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Maaf, asisten AI tidak dapat menghasilkan tanggapan saat ini."
        } catch (e: Exception) {
            "Gagal menghubungi server AI: ${e.localizedMessage}. Menyalakan mode simulasi cerdas...\n\n" + getMockResponse(prompt)
        }
    }

    // Highly smart mock generator for Indonesian Drama Cinema so user gets excellent and realistic answers instantly
    private fun getMockResponse(prompt: String): String {
        val lowerPrompt = prompt.lowercase()
        return when {
            lowerPrompt.contains("keluarga") -> {
                "Untuk genre keluarga yang menyentuh hati, saya sangat merekomendasikan **Keluarga Cemara (2018)** garapan sutradara Yandy Laurens. Film ini mengisahkan ketangguhan Abah, Emak, Euis, dan Ara setelah jatuh miskin dan pindah ke desa. " +
                        "Alternatif lain adalah **Nanti Kita Cerita tentang Hari Ini (NKCTHI - 2020)** karya Angga Dwimas Sasongko yang membedah luka emosional tiga bersaudara dalam keluarga kelas menengah perkotaan."
            }
            lowerPrompt.contains("romantis") || lowerPrompt.contains("cinta") || lowerPrompt.contains("romance") -> {
                "Jika Anda menyukai romansa klasik mendalam, **Ada Apa dengan Cinta? (2002)** oleh Rudy Soedjarwo adalah mahakarya legendaris puisi remaja terindah. " +
                        "Jika menyukai romansa sejarah biografi luar biasa, tontonlah **Habibie & Ainun (2012)** sutradara Faozan Rizal yang mengeksplorasi perjuangan cinta sejati yang matang."
            }
            lowerPrompt.contains("independen") || lowerPrompt.contains("sosial") || lowerPrompt.contains("isu") -> {
                "Untuk film drama yang sarat isu sosial tajam, Anda wajib menonton **Yuni (2021)** besutan Kamila Andini. Film ini bersuara lantang mengenai pembatasan cita-cita perempuan muda oleh adat istiadat setempat. " +
                        "Judul lain yang sangat kuat adalah **Penyalin Cahaya (2021)** oleh Wregas Bhanuteja yang mengupas isu keselamatan korban kekerasan seksual di instansi pendidikan."
            }
            lowerPrompt.contains("rekomendasi") || lowerPrompt.contains("terbaik") || lowerPrompt.contains("bagus") -> {
                "Berikut adalah 3 film drama Indonesia terbaik dengan nilai esensial tinggi yang wajib ditonton:\n\n" +
                        "1. **Laskar Pelangi (2008)** (Dir. Riri Riza) - Kisah kepolosan anak-anak Belitong memperjuangkan hak pendidikan dasar.\n" +
                        "2. **Ada Apa dengan Cinta? (2002)** (Dir. Rudy Soedjarwo) - Tonggak kebangkitan sinema romansa remaja modern.\n" +
                        "3. **Yuni (2021)** (Dir. Kamila Andini) - Eksplorasi sosial jujur, berkilau di kancah festival internasional."
            }
            else -> {
                "Pertanyaan yang menarik! Sinema drama Indonesia memiliki kekayaan cerita yang luar biasa mulai dari era perjuangan hingga representasi urban modern. " +
                        "Beberapa sutradara handal kita seperti Riri Riza, Edwin, Hanung Bramantyo, dan Kamila Andini konsisten melahirkan karya-karya ikonik.\n\n" +
                        "Apakah ada topik, tema tertentu (seperti kuliner, politik, persahabatan, atau masa lalu), atau nama sutradara/aktor tertentu yang ingin Anda telusuri lebih dalam?"
            }
        }
    }
}
