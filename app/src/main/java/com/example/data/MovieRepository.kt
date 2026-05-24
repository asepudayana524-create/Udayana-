package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MovieRepository(private val movieDao: MovieDao) {

    val allMovies: Flow<List<MovieEntity>> = movieDao.getAllMovies()
    val favoriteMovies: Flow<List<MovieEntity>> = movieDao.getFavoriteMovies()
    val watchlistMovies: Flow<List<MovieEntity>> = movieDao.getWatchlistMovies()

    fun getMovieById(id: String): Flow<MovieEntity?> = movieDao.getMovieById(id)

    fun searchMovies(query: String): Flow<List<MovieEntity>> = movieDao.searchMovies(query)

    suspend fun updateMovie(movie: MovieEntity) = movieDao.updateMovie(movie)

    suspend fun seedDefaultMoviesIfNeeded() {
        // Run a query to look if DB is empty
        val currentMovies = movieDao.getAllMovies().first()
        if (currentMovies.isEmpty()) {
            val defaults = listOf(
                MovieEntity(
                    id = "laskar-pelangi",
                    title = "Laskar Pelangi",
                    director = "Riri Riza",
                    releaseYear = 2008,
                    genre = "Drama, Petualangan, Keluarga",
                    rating = 4.8,
                    synopsis = "Kisah perjuangan inspiratif dari sepuluh anak di Pulau Belitung (disebut Laskar Pelangi) dari keluarga miskin untuk tetap bersekolah di SD Muhammadiyah Gantong yang serba kekurangan dan terancam ditutup.",
                    posterColorStart = "#3A6073",
                    posterColorEnd = "#3A7BD5"
                ),
                MovieEntity(
                    id = "aadc",
                    title = "Ada Apa dengan Cinta?",
                    director = "Rudy Soedjarwo",
                    releaseYear = 2002,
                    genre = "Drama, Romansa",
                    rating = 4.7,
                    synopsis = "Kisah romansa masa SMA yang legendaris antara Cinta, seorang gadis populer, ceria, dan aktif di majalah dinding sekolah, dengan Rangga, seorang siswa pendiam, dingin, soliter, dan pencinta sastra.",
                    posterColorStart = "#D38312",
                    posterColorEnd = "#A83279"
                ),
                MovieEntity(
                    id = "nkcthi",
                    title = "Nanti Kita Cerita tentang Hari Ini",
                    director = "Angga Dwimas Sasongko",
                    releaseYear = 2020,
                    genre = "Drama, Keluarga",
                    rating = 4.6,
                    synopsis = "Tiga bersaudara: Angkasa, Aurora, dan Awan hidup dalam keluarga yang tampak bahagia dan harmonis. Namun, di balik itu, masing-masing menyimpan konflik batin dan luka emosional yang siap meledak.",
                    posterColorStart = "#1D2671",
                    posterColorEnd = "#C33764"
                ),
                MovieEntity(
                    id = "seperti-dendam",
                    title = "Seperti Dendam, Rindu Harus Dibayar Tuntas",
                    director = "Edwin",
                    releaseYear = 2021,
                    genre = "Drama, Aksi, Komedi Hitam",
                    rating = 4.5,
                    synopsis = "Mengisahkan Ajo Kawir, jagoan tangguh yang merahasiakan ketidakmampuannya karena impotensi, kemudian jatuh cinta pada Iteung, seorang petarung wanita yang tak kalah bengis di tengah hiruk-pikuk era 80-an.",
                    posterColorStart = "#e53935",
                    posterColorEnd = "#e35d5b"
                ),
                MovieEntity(
                    id = "yuni",
                    title = "Yuni",
                    director = "Kamila Andini",
                    releaseYear = 2021,
                    genre = "Drama, Isu Sosial",
                    rating = 4.7,
                    synopsis = "Yuni adalah gadis SMA cerdas yang bermimpi melanjutkan pendidikan tinggi. Ketika lamaran pernikahan dari para pria mulai berdatangan, ia dihadapkan pada dilema moral, ekspektasi sosial, dan mitos lokal.",
                    posterColorStart = "#8E2DE2",
                    posterColorEnd = "#4A00E0"
                ),
                MovieEntity(
                    id = "keluarga-cemara",
                    title = "Keluarga Cemara",
                    director = "Yandy Laurens",
                    releaseYear = 2018,
                    genre = "Drama, Keluarga, Hangat",
                    rating = 4.6,
                    synopsis = "Setelah bangkrut akibat ditipu kerabatnya sendiri, Abah memutuskan memboyong istrinya (Emak) dan anak-anaknya (Euis & Ara) ke desa terpencil di Jawa Barat demi memulai hidup sederhana yang sarat kebersamaan.",
                    posterColorStart = "#56ab2f",
                    posterColorEnd = "#a8e063"
                ),
                MovieEntity(
                    id = "penyalin-cahaya",
                    title = "Penyalin Cahaya",
                    director = "Wregas Bhanuteja",
                    releaseYear = 2021,
                    genre = "Drama, Misteri, Investigasi",
                    rating = 4.6,
                    synopsis = "Suryani kehilangan beasiswa kuliah berprestasi miliknya setelah foto pesta malam teater miliknya tersebar tanpa izin. Bersama Amin, ia meluncurkan penyelidikan mandiri di kampusnya untuk mengungkap kegelapan pelecehan seksual.",
                    posterColorStart = "#0f2027",
                    posterColorEnd = "#203a43"
                ),
                MovieEntity(
                    id = "aruna-lidahnya",
                    title = "Aruna & Lidahnya",
                    director = "Edwin",
                    releaseYear = 2018,
                    genre = "Drama, Kuliner, Pertemanan",
                    rating = 4.5,
                    synopsis = "Aruna, seorang penyelidik flu burung yang terobsesi pada makanan, melakukan kunjungan dinas lintas kota di Indonesia didampingi sahabatnya Bona yang koki, Nad tetangga petualang rasa, dan Farish mantan kolega kerja.",
                    posterColorStart = "#f12711",
                    posterColorEnd = "#f5af19"
                ),
                MovieEntity(
                    id = "bumi-manusia",
                    title = "Bumi Manusia",
                    director = "Hanung Bramantyo",
                    releaseYear = 2019,
                    genre = "Drama, Sejarah, Romansa",
                    rating = 4.6,
                    synopsis = "Berdasarkan novel karya Pramoedya Ananta Toer, film ini melacak asmara legendaris nan pilu antara Minke, seorang pribumi cerdas berpikiran terbuka, dan Annelies, gadis Indo-Belanda putri dari Nyai Ontosoroh yang karismatik.",
                    posterColorStart = "#800020",
                    posterColorEnd = "#b8860b"
                ),
                MovieEntity(
                    id = "habibie-ainun",
                    title = "Habibie & Ainun",
                    director = "Faozan Rizal",
                    releaseYear = 2012,
                    genre = "Drama, Biografi, Romansa",
                    rating = 4.8,
                    synopsis = "Kisah nyata pengabdian, cinta abadi, perjuangan, serta persatuan jiwa antara BJ Habibie, sang teknokrat visioner pembuat pesawat, mendampingi sang istri tercinta, Ainun, yang mengarungi hidup hingga ajal memisahkan.",
                    posterColorStart = "#D4145A",
                    posterColorEnd = "#FBB03B"
                )
            )
            movieDao.insertMovies(defaults)
        }
    }
}
