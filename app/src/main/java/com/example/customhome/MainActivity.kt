package com.example.customhome

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.WindowCompat

open class MainActivity : AppCompatActivity() {
    private var btn_applist = ArrayList<ImageButton>()
    private var label_applist = ArrayList<TextView>()
    private lateinit var names : ArrayList<String>
    private val btn_num = 9
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var decorView : View = window.decorView

        // hide action bar
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // ignore system window flags
            window.setDecorFitsSystemWindows(false)
            // appoint new settings
            val controller = window.insetsController
            if (controller != null) {
                // remove status bar & navigation
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                // show system bar when swipe
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }

        // create 'btn_num' number of buttons
        names = ArrayList<String>()
        for (i in 0 until btn_num) {
            names.add(i, i.toString())
            val btn_id = resources.getIdentifier("image_btn" + i, "id", packageName)
            val label_id = resources.getIdentifier("label_btn" + i, "id", packageName)
            var btn = findViewById<ImageButton>(btn_id)
            var label = findViewById<TextView>(label_id)
            btn.setOnClickListener{ view ->
                var _itt : Intent = Intent(this, AppListActivity::class.java)
                _itt.putExtra("btn_idx", i)
                startApp(label.getText().toString(), _itt, 1)
            }

            btn_applist.add(btn)
            label_applist.add(label)
        }
    }

    // basics for launcher App
    override fun onBackPressed() {
        return // do nothing
    }

    override fun onStop() {
        Log.i("mLog","onStop")
        super.onStop()
        /* restart (looks like nothing happen) */
        // startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onPause() {
        Log.i("mLog","onPuase")
        super.onPause()
        /* change order of the process */
        // val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // activityManager.moveTaskToFront(taskId, 0)
    }
    // -> basics for launcher App

    fun startApp(textView : String, intent : Intent, num : Int) {
        var manager : PackageManager = this.packageManager
        if (textView=="") {
            startActivityForResult(intent, num)
        }
        else {
            var activity : Intent? = manager.getLaunchIntentForPackage(names.get(num - 1).toString())
            activity?.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            var arr : ByteArray? = data?.getByteArrayExtra("icon")
            var arr_size = 0
            arr?.let { arr_size = arr.size }
            var bitmap : Bitmap = BitmapFactory.decodeByteArray(arr, 0, arr_size)

            if (requestCode == 1) {
                addApp(bitmap, data?.getIntExtra("btn_idx", -1), data?.getStringExtra("name"), data?.getStringExtra("uri"))
            }
        }
    }

    private fun addApp(bitmap: Bitmap, indexNum: Int?, label: String?, name: String?) {
        if (indexNum == -1) return
        indexNum?.let {
            btn_applist.get(it).setImageBitmap(bitmap)
            label_applist.get(indexNum).setText(label)
            name?.let { names.add(indexNum, it) }
        }
    }
}