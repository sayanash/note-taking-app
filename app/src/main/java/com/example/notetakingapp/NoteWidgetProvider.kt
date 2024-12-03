package com.example.notetakingapp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.appwidget.AppWidgetProvider

class NoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Loop through each widget
        for (appWidgetId in appWidgetIds) {
            // Inflate the widget layout
            val views = RemoteViews(context.packageName, R.layout.widget_note)

            // Create an Intent to launch EditNoteActivity
            val intent = Intent(context, EditNoteActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Assign the PendingIntent to the button in the widget
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)

            // Update the widget with the new RemoteViews
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
