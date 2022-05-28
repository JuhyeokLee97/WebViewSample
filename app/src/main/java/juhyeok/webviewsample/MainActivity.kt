package juhyeok.webviewsample

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import juhyeok.webviewsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        bindViews()
    }


    private fun initViews() {
        // Initialize WebView
        binding.webView.apply {
            // 생성한 `webView`를 사용하기 위함 => 외부 웹 어플리케이션을 이용하지 않을 수 있다.
            webViewClient = MyWebViewClient()
            // ProgressBar 사용하기 위해... `webView`에서 다양한 기능을 사용하기 위해서는 `WebChromeClient`를 사용한다.
            webChromeClient = MyWebChromeClient()
            // 웹 이벤트 작동을 위해 `JavaScript`를 허용한다.
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
    }

    private fun bindViews() {
        binding.apply {
            // 주소 검색 창, 키보드 엔터 액션 이벤트
            etSearch.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    val loadingUrl = view.text.toString()
                    // http(s)가 첨부된 URL인지 검사한다.
                    if (URLUtil.isNetworkUrl(loadingUrl))
                        webView.loadUrl(loadingUrl)
                    else
                        webView.loadUrl("http://$loadingUrl")
                }
                return@setOnEditorActionListener false
            }
            // 뒤로 가기 버튼
            btnGoBack.setOnClickListener { webView.goBack() }
            // 앞으로 가기 버튼
            btnGoForward.setOnClickListener { webView.goForward() }
            // 홈으로 가기 버튼
            btnGoHome.setOnClickListener { webView.loadUrl(DEFAULT_URL) }
            // 드래그 새로고침
            refreshLayout.setOnRefreshListener {
                webView.reload()
            }
        }

    }


    /**
     * `WebView`의 페이지 로딩 상태를 파악하여 `progressbar`와 `refreshLayout`에 적용시킬 수 있도록
     * `WebViewClient.onPageStarted`와 `WebViewClient.onPageFinished`를 재구현한다.
     */
    inner class MyWebViewClient : android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            binding.progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.apply {
                refreshLayout.isRefreshing = false
                progressBar.hide()

                btnGoBack.isEnabled = webView.canGoBack()
                btnGoForward.isEnabled = webView.canGoForward()

                etSearch.setText(url)
            }
        }
    }

    /**
     * `WebView`의 페이지 로딩 정도를 파악하여 `progressbar`의 상태를 관리하기 위해
     * `WebChromeClient.onProgressChanged`를 재구현한다.
     */
    inner class MyWebChromeClient: WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            binding.progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}