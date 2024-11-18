package com.dimalahmad.notificationcounterlikedislike;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dimalahmad.notificationcounterlikedislike.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    // Menggunakan View Binding untuk mengakses elemen layout
    private lateinit var binding: ActivityMainBinding

    // ID untuk channel notifikasi
    private val channelId = "TEST_NOTIF"

    // ID untuk notifikasi
    private val notifId = 90

    // BroadcastReceiver untuk menangani pembaruan counter
    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateLikeDislike() // Memperbarui nilai counter secara real-time
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menginisialisasi View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Membuat NotificationChannel (hanya untuk API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test Notification Channel" // Nama channel
            val descriptionText = "This is a channel for test notifications" // Deskripsi channel
            val importance = NotificationManager.IMPORTANCE_DEFAULT // Tingkat pentingnya notifikasi
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText // Mengatur deskripsi channel
            }
            // Mendaftarkan channel ke sistem
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Memperbarui tampilan jumlah like dan dislike saat aplikasi diluncurkan
        updateLikeDislike()

        // Mendaftarkan BroadcastReceiver untuk menangani aksi pembaruan
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(updateReceiver, IntentFilter("ACTION_UPDATE_COUNTERS"))

        // Menangani klik tombol notifikasi
        binding.btnNotif.setOnClickListener {
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Intent untuk aksi "Like"
            val likeIntent = Intent(this, NotifReceiver::class.java).apply {
                action = "ACTION_LIKE" // Aksi untuk Like
            }
            val likePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                likeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Intent untuk aksi "Dislike"
            val dislikeIntent = Intent(this, NotifReceiver::class.java).apply {
                action = "ACTION_DISLIKE" // Aksi untuk Dislike
            }
            val dislikePendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                dislikeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Membuat dan menampilkan notifikasi
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_2) // Ikon kecil untuk notifikasi
                .setContentTitle("Voting Dimulai!!!") // Judul notifikasi
                .setContentText("Apakah film berikut recomended?") // Konten notifikasi
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(resources, R.drawable.img_3)) // Gambar besar di notifikasi
                )
                .setAutoCancel(true) // Notifikasi akan hilang saat diklik
                .addAction(0, "Like", likePendingIntent) // Aksi Like
                .addAction(0, "Dislike", dislikePendingIntent) // Aksi Dislike

            notifManager.notify(notifId, builder.build()) // Menampilkan notifikasi
        }
    }

    // Fungsi untuk memperbarui jumlah like dan dislike
    private fun updateLikeDislike() {
        val prefs: SharedPreferences = getSharedPreferences("CounterPrefs", Context.MODE_PRIVATE)
        val likeCount = prefs.getInt("LIKE_COUNT", 0) // Mengambil jumlah Like dari SharedPreferences
        val dislikeCount = prefs.getInt("DISLIKE_COUNT", 0) // Mengambil jumlah Dislike dari SharedPreferences

        // Menampilkan jumlah Like dan Dislike di UI
        binding.likeCounter.text = "$likeCount"
        binding.dislikeCounter.text = "$dislikeCount"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister BroadcastReceiver saat Activity dihancurkan
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
    }
}
