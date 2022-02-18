# Tugas Besar 1 Strategi Algoritma

Merupakan project dengan menggunakan bahasa pemrograman Java untuk membuat bot permainan Overdrive dalam pertandingan tahunan Entelect Challenge menggunakan strategi greedy. Proyek ini dibuat untuk pemenuhan tugas besar 1 IF2211 Strategi Algoritma.

## Daftar Isi

- [Strategi Greedy](#strategi-greedy)
- [Requirement](#requirement)
- [Setup](#setup)
- [Usage](#usage)
- [Author](#author)

## Strategi Greedy

Strategi greedy yang digunakan secara ringkas dapat dirangkum sebagai berikut:

Pada setiap langkah (ronde), dapatkan semua command **(himpunan kandidat)** yang dapat dipakai tanpa pinalti skor **(fungsi kelayakan)**, kemudian menggunakan prioritas dengan _grouping_ perintah yang mirip secara fungsi, dimana setiap _command group_ juga akan memprioritaskan _command_ yang lebih berguna berdasarkan state permainan di setiap rondenya **(fungsi seleksi)** untuk mencapai jarak antara pemain dan musuh (negatif jika musuh mendahului pemain atau positif jika pemain mendahului musuh) yang maksimum, kecepatan mobil yang maksimum, serta memaksimumkan skor yang diperoleh **(fungsi objektif)**, sehingga didapatkan urutan command yang akan menjadi perintah untuk bot di permainan pada setiap rondenya **(himpunan solusi)** hingga akhir dari permainan **(fungsi solusi)**.

## Requirement

1. JDK 1.8 keatas
2. Maven

## Setup

Untuk melakukan build project, gunakan maven sehingga menghasilkan folder `bin` berisi `jar` program bot yang siap digunakan.

```
mvn clean install
```

## Usage

Bot yang telah dibuild dalam tahap Setup dengan format `.jar` dapat dipakai bersamaan dengan _game runner_ sehingga dapat berkomunikasi dengan _game engine_. Pastikan telah menyalin folder `bin` dan `bot.json` dalam satu folder yang sama yang dimasukkan ke dalam folder game runner, kemudian atur `game-runner-config.json` pada `pemain-a` atau `pemain-b` sehingga menggunakan path folder bot yang telah disalin. Jalankan `game-runner-jar-with-dependencies.jar` dengan command berikut:

```
java -jar ./game-runner-jar-with-dependencies.jar
```

Pastikan terdapat `game-engine.jar`, `game-config.json`, dan `game-runner-config.json` pada folder yang sama dengan jar untuk _game runner_.

Untuk memudahkan dalam menjalankan permainan dan menggunakan bot, gunakan starter-pack yang telah tersedia [disini](https://github.com/EntelectChallenge/2020-Overdrive/releases/download/2020.3.4/starter-pack.zip), kemudian salin folder bot di dalam folder starter pack yang telah diekstrak (pastikan telah dibuiild terlebih dahulu) dan ubah `game-runner-config.json` sesuai dengan langkah diatas, terakhir jalankan `run.bat`.

## Author

**Kelompok 37** | _Eco Racing_  
- Rifqi Naufal Abdjul (13520062)
- Amar Fadil (13520103)
- Vito Ghifari (13520153)
