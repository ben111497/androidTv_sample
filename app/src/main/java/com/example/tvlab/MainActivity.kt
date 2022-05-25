package com.example.tvlab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var tvRecommend: TextView
    private lateinit var clPlayVideo: ConstraintLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var adapter: VideoListAdapter
    private var list = ArrayList<VideoList>()

    class Result(val list: ArrayList<VideoList>)
    class VideoList(
        val videoID: String,
        val picture: String,
        val videoName: String,
        val author: String
        )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            list.shuffle()
            initData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        httpGet("https://fghw7ihkqermjwecjdaahy7n7m0vniev.lambda-url.us-west-1.on.aws/")
        setListener()
    }

    private fun initView() {
        webView = findViewById(R.id.webView)
        tvRecommend = findViewById(R.id.tvRecommend)
        clPlayVideo = findViewById(R.id.clPlayVideo)
        rvVideoList = findViewById(R.id.rvVideoList)
    }

    private fun initData() {
        val item = list[0]
        tvRecommend.text = item.videoName
        val iframeHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/${item.videoID}\"?autoplay=1&loop=1&playlist=${item.videoID} title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"
        setWebView(iframeHtml)
        initAvatarRecyclerView()
    }

    private fun setListener() {
        clPlayVideo.setOnClickListener {
            gotoWatchVideo(list[0].videoID)
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

    private fun initAvatarRecyclerView() {
        if (!::adapter.isInitialized) {
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL

            adapter = VideoListAdapter(this, list)
            adapter.setListener(object : VideoListAdapter.Listener {
                override fun onItemClick(embedded: String) {
                    gotoWatchVideo(embedded)
                }
            })

            rvVideoList.layoutManager = layoutManager
            rvVideoList.adapter = adapter
        } else {
            adapter.notifyDataSetChanged()
            rvVideoList.scrollToPosition(0)
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

    private fun gotoWatchVideo(videoID: String) {
        webView.loadUrl("about:blank")
        val intent = Intent(this@MainActivity, MainActivity2::class.java)
        intent.putExtra("Embedded", videoID)
        startActivityForResult(intent, 100)
    }

    private fun httpGet(url: String) {
        val req = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val json = response.body?.string()
                    val result = Gson().fromJson(json, Result::class.java)
                    list.addAll(result.list.shuffled())

                    runOnUiThread {
                        initData()
                        clPlayVideo.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("error", "${e.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}