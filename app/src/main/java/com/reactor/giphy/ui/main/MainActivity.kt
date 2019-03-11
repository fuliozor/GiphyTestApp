package com.reactor.giphy.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.widget.TextView
import com.reactor.giphy.R
import com.reactor.giphy.data.NetworkState
import com.reactor.giphy.data.Status
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val page = "<html>\n" +
            "<head>\n" +
            "    <meta charset='UTF-8'/>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            margin: 0;\n" +
            "        }\n" +
            "\n" +
            "        #container {\n" +
            "            display: flex;\n" +
            "            flex-direction: column;\n" +
            "            align-items: center;\n" +
            "        }\n" +
            "\n" +
            "        #container img {\n" +
            "            width: 100%;\n" +
            "            height: auto;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <script lang=\"js\">\n" +
            "      function addNewElements(links) {\n" +
            "        var div = document.getElementById(\"container\");\n" +
            "        for (var i = 0; i < links.length; i++) {\n" +
            "          var img = document.createElement(\"img\");\n" +
            "          img.setAttribute(\"src\", links[i]);\n" +
            "          div.appendChild(img)\n" +
            "        }\n" +
            "      }\n" +
            "\n" +
            "      window.onscroll = function () {\n" +
            "        console.log('onscroll');\n" +
            "        if ((window.innerHeight + window.pageYOffset) >= document.body.offsetHeight - 2) {\n" +
            "          Android.onPageScrolled()\n" +
            "        }\n" +
            "      };\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"container\"></div>\n" +
            "</body>\n" +
            "</html>"

    private lateinit var gifsViewModel: GifsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar.visibility = View.GONE

        gifsViewModel = ViewModelProviders.of(this).get(GifsViewModel::class.java)

        etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search()
                    true
                } else false
            }
        })

        btnSearch.setOnClickListener {
            search()
        }

        initWebView()
        observeRequestStatus()
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun initWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("WebView", consoleMessage.message())
                return true
            }
        }
        webView.addJavascriptInterface(JsInterface(), "Android")
        webView.loadData(page, "text/html", "utf-8")
    }

    private fun observeRequestStatus() {
        gifsViewModel.data.observe(this, Observer<NetworkState> {
            if (it == null) return@Observer
            when (it.status) {
                Status.RUNNING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    if (it.data!!.total == 0) {
                        showError(R.string.error_empty_result)
                    } else {
                        addImagesToWebView(it.data.urls)
                        hideKeyboardFrom(this, etSearch)
                    }
                }
                Status.FAILED -> {
                    progressBar.visibility = View.GONE
                    showError(R.string.error_loading_data)
                }
            }
        })
    }

    private fun addImagesToWebView(images: List<String>) {
        val param = "[" + (images.joinToString(",") { url -> "'$url'" }) + "]"
        webView.loadUrl("javascript:addNewElements($param);")
    }

    private fun showError(text: Int) {
        Snackbar.make(webView, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok) { }.show()
    }

    private fun search() {
        webView.loadData(page, "text/html", "utf-8")
        gifsViewModel.search(etSearch.text.toString())
        hideKeyboardFrom(this, etSearch)
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    inner class JsInterface {
        @JavascriptInterface
        fun onPageScrolled() {
            gifsViewModel.loadMoreData()
        }
    }
}
