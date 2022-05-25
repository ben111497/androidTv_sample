package com.example.tvlab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var btnBack: Button
    private var videoID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        intent?.extras?.let {
            videoID = it.getString("Embedded") ?: ""
        }

        initView()
        init()
        setListener()
    }

    private fun initView() {
        webView = findViewById(R.id.webView)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun init() {
        val iframeHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/${videoID}\"?autoplay=1&loop=1&playlist=${videoID} title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
        setWebView(iframeHtml)
    }

    private fun setListener() {
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView(embeddedCode: String) {
        with(webView) {
            //縮放
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            //將圖片調整到適合WebView的大小
            settings.useWideViewPort = true
            //自動適應螢幕大小
            settings.loadWithOverviewMode = true
            // 取消WebView中滾動陰影
            overScrollMode = View.OVER_SCROLL_NEVER
            // 取消滾動白邊
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            isScrollContainer = false

            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true

            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            isScrollContainer = false

            setBackgroundColor(Color.parseColor("#000000"))

            loadDataWithBaseURL("https://youtube.com", embeddedCode, "text/html; charset=utf-8", "UTF-8", null)
            Handler(Looper.getMainLooper()).postDelayed({ clickView(this, 350f, 350f) }, 3000)
        }
    }

    private fun clickView(view: View, x: Float, y: Float) {
        var downTime: Long = SystemClock.uptimeMillis()
        val downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0)
        downTime += 10
        val upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0)
        view.onTouchEvent(downEvent)
        view.onTouchEvent(upEvent)
        downEvent.recycle()
        upEvent.recycle()
    }
}