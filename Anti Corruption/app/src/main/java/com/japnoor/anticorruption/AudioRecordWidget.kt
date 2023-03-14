package com.japnoor.anticorruption

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.media.MediaRecorder
import android.os.Environment
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class AudioRecordWidget : AppWidgetProvider() {
    private var isRecording=false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them


        for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.audio_record_widget)
                val intent = Intent(context, AudioRecordWidget::class.java)
                intent.action = "RECORD_AUDIO"
                val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT )
                views.setOnClickPendingIntent(R.id.audiorecord, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    override fun onReceive(context: Context, intent: Intent) {
        sharedPreferences=context.getSharedPreferences("AudioWidget",Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        if (intent.action == "RECORD_AUDIO") {
            val recorder = MediaRecorder()
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, AudioRecordWidget::class.java))
            val remoteViews = RemoteViews(context.packageName, R.layout.audio_record_widget)

            if (sharedPreferences.contains("audio1")) { // stop recording
                recorder.pause()
                recorder.reset()
                recorder.release()
                editor.remove("audio1")
                editor.apply()
                editor.commit()
//                isRecording = false
                remoteViews.setImageViewResource(R.id.audiorecord, R.drawable.lottiemic) // Change back to button
                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
            }
            else {
                val currentTime = Date().time
                val outputFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                    "Audio $currentTime .mp3"
                )
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                recorder.setOutputFile(outputFile.absolutePath)
                recorder.prepare()
                editor.putString("audio1","1")
                editor.apply()
                editor.commit()
                recorder.start()
//                isRecording = true
                remoteViews.setImageViewResource(R.id.audiorecord, R.drawable.playaudio)
                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
            }
            widgetIds.forEach { widgetId ->
                appWidgetManager.updateAppWidget(widgetId, remoteViews)
            }
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.audio_record_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}