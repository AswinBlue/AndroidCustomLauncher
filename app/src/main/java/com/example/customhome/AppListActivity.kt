package com.example.customhome

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.format.ResolverStyle
import com.example.customhome.R
import java.io.ByteArrayOutputStream
import kotlin.math.log

class AppListActivity : MainActivity() {
    private lateinit var manager : PackageManager
    private lateinit var apps : ArrayList<AppDetail>
    private lateinit var listView : ListView
    private lateinit var header : View
    private lateinit var itt : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        header = layoutInflater.inflate(R.layout.activity_main, null, false)
        itt = intent
        loadApps()
        loadListView()
        addClickListener()
    }

    private fun loadApps() {
        Log.i("mLog","loadApps")
        manager = packageManager
        apps = arrayListOf<AppDetail>()
        val _itt = Intent(Intent.ACTION_MAIN, null)
        _itt.addCategory(Intent.CATEGORY_LAUNCHER)

        var availableActivities = manager.queryIntentActivities(_itt, 0) as List<ResolveInfo>

        for (ri in availableActivities) {
            var app = AppDetail()

            app.name = ri.loadLabel(manager)
            Log.d("mLog","app name " + app.name)
            app.uri = ri.activityInfo.packageName
            Log.d("mLog","app uri " + app.uri)
            app.icon = ri.activityInfo.loadIcon(manager)
            Log.d("mLog","app icon " + app.icon)
            apps.add(app)
        }
    }

    private fun loadListView() {
        Log.i("mLog","loadListView")
        listView = findViewById(R.id.apps_list) as ListView
        var adapter = object : ArrayAdapter<AppDetail>(this, R.layout.list_item, apps) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return if (convertView == null) {
                    // https://stackoverflow.com/questions/4576219/logcat-error-addviewview-layoutparams-is-not-supported-in-adapterview-in-a/25486503
                    val tempView : View = layoutInflater.inflate(R.layout.list_item, null)
                    tempView
                } else {
                    var appIcon : ImageView = convertView.findViewById(R.id.item_app_icon) as ImageView
                    appIcon.setImageDrawable(apps.get(position).icon)
                    Log.d("mLog","get icon")

                    var appName : TextView = convertView.findViewById(R.id.item_app_name) as TextView
                    appName.setText(apps.get(position).name)
                    Log.d("mLog","get label")

                    var appUri : TextView = convertView.findViewById(R.id.item_app_uri) as TextView
                    appUri.setText(apps.get(position).uri)
                    Log.d("mLog","get name")

                    return convertView
                }
            }
        }
        listView.setAdapter(adapter)
        listView.deferNotifyDataSetChanged()
    }

    private fun addClickListener() {
        Log.i("mLog","addClickListener")
        listView.setOnItemClickListener{ parent, view, position, id ->
            var stream = ByteArrayOutputStream()

            itt.putExtra("name", apps.get(position).name)
            Log.d("mLog","get name")
            itt.putExtra("uri", apps.get(position).uri)
            Log.d("mLog","get uri")
            var drawable : Drawable = apps.get(position).icon
            Log.d("mLog","get icon")
            /* commented code can occur errors. use codes beneath the comments
             * ERROR: java.lang.ClassCastException: android.graphics.drawable.AdaptiveIconDrawable cannot be cast to android.graphics.drawable.BitmapDrawable
             */
//            var bitmap : Bitmap = (drawable as BitmapDrawable).bitmap
//            itt.putExtra("icon", byteArray)
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            // convert drawable to byte-array and put it in 'itt' intent
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            var byteArray : ByteArray = stream.toByteArray()
            itt.putExtra("icon", byteArray)
            itt.putExtra("btn_idx", itt.getIntExtra("btn_idx", -1))

            setResult(RESULT_OK, itt)
            finish()
        }
    }
}