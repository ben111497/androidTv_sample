package com.example.tvlab

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var tvRecommend: TextView
    private lateinit var clPlayVideo: ConstraintLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var adapter: VideoListAdapter
    private var list = ArrayList<VideoList>()

    class Result(val list: ArrayList<VideoList>)
    class VideoList(val videoID: String, val picture: String, val videoName: String, val author: String)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            list.shuffle()
            init()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        setJson()
        init()
        setListener()
    }

    private fun initView() {
        webView = findViewById(R.id.webView)
        tvRecommend = findViewById(R.id.tvRecommend)
        clPlayVideo = findViewById(R.id.clPlayVideo)
        rvVideoList = findViewById(R.id.rvVideoList)
    }

    private fun init() {
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

    fun clickView(view: View, x: Float, y: Float) {
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

    private fun setJson() {
        val json = "{\"list\":[" +
                "{\"videoID\":\"Ua6Sxuhfbh8\",\"picture\":\"https://i.ytimg.com/an_webp/Ua6Sxuhfbh8/mqdefault_6s.webp?du=3000&sqp=CJSBsZQG&rs=AOn4CLCv_ZHUA1-_1Xspv6sWCyqRX9YQLQ\",\"videoName\":\"【蜘蛛人：無家日】最新4K前導電影預告-2021.12.15電影院限定震撼大銀幕\",\"author\":\"SonyPictures索尼影業\"}," +
                "{\"videoID\":\"xINW8hu_czc\",\"picture\":\"https://i.ytimg.com/an_webp/xINW8hu_czc/mqdefault_6s.webp?du=3000&sqp=CJT2sJQG&rs=AOn4CLCdvbAyPlKtdpLSd9ke2EgMwvdqSQ\",\"videoName\":\"【小小兵2:格魯的崛起】最新歡樂預告-2022年暑假歡樂登場\",\"author\":\"環球影片官方頻道\"}," +
                "{\"videoID\":\"x3Tt2bJN-yo\",\"picture\":\"https://i.ytimg.com/an_webp/x3Tt2bJN-yo/mqdefault_6s.webp?du=3000&sqp=CNrtsJQG&rs=AOn4CLCX5f9oD9uXG4M97Jckcr4WJ1_nWA\",\"videoName\":\"《阿凡達2:水之道》首個電影預告Avatar2:TheWayofWaterOfficialTrailer\",\"author\":\"IGNChina\"}," +
                "{\"videoID\":\"LZ5oUhcpf2M\",\"picture\":\"https://i.ytimg.com/an_webp/LZ5oUhcpf2M/mqdefault_6s.webp?du=3000&sqp=CI2FsZQG&rs=AOn4CLBfwZ0f3pvu4ZvlcAbYNhQScZ-lcg\",\"videoName\":\"威視電影【鯊顫】正式預告(05.13大銀幕血口活吞)\",\"author\":\"威視電影\"}," +
                "{\"videoID\":\"PuqhU0KaM0g\",\"picture\":\"https://i.ytimg.com/an_webp/PuqhU0KaM0g/mqdefault_6s.webp?du=3000&sqp=CKjasJQG&rs=AOn4CLDkwf1Kvh5AkxUy_nUHWjwc3mTGtA\",\"videoName\":\"【侏羅紀世界：統霸天下】最新預告-6月8日大銀幕震撼登場\",\"author\":\"環球影片官方頻道\"}," +
                "{\"videoID\":\"IbsLhjGg6mg\",\"picture\":\"https://i.ytimg.com/an_webp/IbsLhjGg6mg/mqdefault_6s.webp?du=3000&sqp=CJDCsJQG&rs=AOn4CLAKylO_uXGpE0XZcAp_ni7UNdqUEA\",\"videoName\":\"【捍衛戰士：獨行俠】最新預告-5月25日全球同步戲院見\",\"author\":\"派拉蒙影片官方頻道\"}," +
                "{\"videoID\":\"UEdXuvh4HTE\",\"picture\":\"https://i.ytimg.com/an_webp/UEdXuvh4HTE/mqdefault_6s.webp?du=3000&sqp=CPeIsZQG&rs=AOn4CLCgxTH431FyNkwPo6hiW2E_jiePIQ\",\"videoName\":\"電影《咒》Incantation正式預告｜03/18大銀幕試膽\",\"author\":\"牽猴子電影粉絲俱樂部\"}," +
                "{\"videoID\":\"veYJtiBFxCc\",\"picture\":\"https://i.ytimg.com/an_webp/veYJtiBFxCc/mqdefault_6s.webp?du=3000&sqp=CMv_sJQG&rs=AOn4CLAP9T-f9yybPOavin2dOcwUbnMqdA\",\"videoName\":\"皮克斯年度動畫《巴斯光年》全新預告登場6月17日(五)　大銀幕上映\",\"author\":\"迪士尼影業\"}," +
                "{\"videoID\":\"uln6gDHY_Is\",\"picture\":\"https://i.ytimg.com/an_webp/uln6gDHY_Is/mqdefault_6s.webp?du=3000&sqp=CMnIsJQG&rs=AOn4CLBlguNumPjrjYF6cvNX_huSIh0zPA\",\"videoName\":\"【不可能的任務：致命清算第一章】首支預告-2023年全台大銀幕震撼登場\",\"author\":\"派拉蒙影片官方頻道\"}," +
                "{\"videoID\":\"HOyPQfJ5p68\",\"picture\":\"https://i.ytimg.com/an_webp/HOyPQfJ5p68/mqdefault_6s.webp?du=3000&sqp=CJ_csJQG&rs=AOn4CLBT9vt-ecYtCSuIl_EmSFO0wUd13w\",\"videoName\":\"【怪獸與鄧不利多的秘密】全新預告，4月14日(週四)大銀幕獻映\",\"author\":\"華納兄弟台灣\"}," +
                "{\"videoID\":\"KKoRq2wB8Vo\",\"picture\":\"https://i.ytimg.com/an_webp/KKoRq2wB8Vo/mqdefault_6s.webp?du=3000&sqp=CObpsJQG&rs=AOn4CLBLNTTmqPoOadOgBDxf73Ocv3w6xw\",\"videoName\":\"【失落謎城】最新預告-4月8日全台戲院見\",\"author\":\"派拉蒙影片官方頻道\"}," +
                "{\"videoID\":\"EMdcOqVc0RU\",\"picture\":\"https://i.ytimg.com/an_webp/EMdcOqVc0RU/mqdefault_6s.webp?du=3000&sqp=CPmDsZQG&rs=AOn4CLA2DR2Hx84qwvQODzNnI9TlQaDOpA\",\"videoName\":\"啟發自駭人真實事件！【噩兆】DEIFIED正式預告12/24(五)全台大銀幕上映\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"ObpJ0L_9FYE\",\"picture\":\"https://i.ytimg.com/an_webp/ObpJ0L_9FYE/mqdefault_6s.webp?du=3000&sqp=CNOBsZQG&rs=AOn4CLCWkffK8nXE_6shBR1_EK51xiT-Wg\",\"videoName\":\"《復仇者聯盟：終局之戰》特效團隊年度災難鉅獻！【北海浩劫】TheNorthSea電影預告12/10(五)迫在眉睫\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"PH0TmFxYYK4\",\"picture\":\"https://i.ytimg.com/an_webp/PH0TmFxYYK4/mqdefault_6s.webp?du=3000&sqp=CMjVsJQG&rs=AOn4CLCL1rTqLDcBgunLYcCZ8bl-PiAnHA\",\"videoName\":\"哇！是殺人牛仔褲【牛宰褲】Slaxx電影預告2/19(五)褲斃了！\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"cjXwZpo034A\",\"picture\":\"https://i.ytimg.com/an_webp/cjXwZpo034A/mqdefault_6s.webp?du=3000&sqp=CICNsZQG&rs=AOn4CLAqjv8A8CUGMVifo_ISzsu7PkQbWg\",\"videoName\":\"＜浪客劍心＞真人電影預告片（中文字幕）\",\"author\":\"TokyoOtakuMode\"}," +
                "{\"videoID\":\"3wJBokobEYo\",\"picture\":\"https://i.ytimg.com/an_webp/3wJBokobEYo/mqdefault_6s.webp?du=3000&sqp=CMrXsJQG&rs=AOn4CLDCwOR_mkgUuhHSkJf8ppbGFhigLQ\",\"videoName\":\"《玩命關頭》坦克版！【T-34：玩命坦克】電影預告2/3(三)決戰大銀幕\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"L98BK6JmMxw\",\"picture\":\"https://i.ytimg.com/an_webp/L98BK6JmMxw/mqdefault_6s.webp?du=3000&sqp=CJyMsZQG&rs=AOn4CLDs14kdHA7XneH68xHzerUtS9CXbg\",\"videoName\":\"【1917】最新精彩預告-1月30日分秒必爭\",\"author\":\"環球影片官方頻道\"}," +
                "{\"videoID\":\"KROC8AqSyB0\",\"picture\":\"https://i.ytimg.com/vi/KROC8AqSyB0/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLCWz9hKkXyvprCJPQIJPDzG9BHJ2A\",\"videoName\":\"「浩克」化身正義律師【黑水風暴】DarkWaters電影預告近期隆重鉅獻\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"jjO9DF57_vE\",\"picture\":\"https://i.ytimg.com/an_webp/jjO9DF57_vE/mqdefault_6s.webp?du=3000&sqp=CJTesJQG&rs=AOn4CLAxbBWLmqcI_TV9jXrCDqlGg_r3jQ\",\"videoName\":\"【一級玩家】終極中文官方預告，3月29日(週四)加入革命軍\",\"author\":\"華納兄弟台灣\"}," +
                "{\"videoID\":\"9ntqHD6tDbA\",\"picture\":\"https://i.ytimg.com/vi/9ntqHD6tDbA/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBrF6qUMoE38i17AXVUe58HD9M4Bw\",\"videoName\":\"中島哲也【來了】電影預告暌違五年的恐怖巨作3/22(五)在劫難逃\",\"author\":\"GaragePlay車庫娛樂\"}," +
                "{\"videoID\":\"YPkOvNCh5pQ\",\"picture\":\"https://i.ytimg.com/an_webp/YPkOvNCh5pQ/mqdefault_6s.webp?du=3000&sqp=CLSBsZQG&rs=AOn4CLBs4jFpshUoosd3vjqsqJRnMob6GQ\",\"videoName\":\"《电锯人/鏈鋸人》動畫版預告ChainsawManAnimeTrailer\",\"author\":\"IGNChina\"}" +
                "]}"

        val result = Gson().fromJson(json, Result::class.java)
        list.addAll(result.list.shuffled())
    }
}