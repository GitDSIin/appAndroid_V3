package com.apptomatico.app_movil_kotlin_v3.browserapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class BrowserAppActivity: Fragment() {
    lateinit var rootView: View
    var webView: WebView? = null
    var swipe: SwipeRefreshLayout? = null
    private var toolbar: Toolbar? = null
    var urlConsulta: String? = "https://www.google.com/"
    private lateinit var bottomNavigationView: BottomNavigationView
    private var configUsuMenu: MenuItem? = null
    private var conexionMenu: MenuItem? = null
    private var estatusAlertasMenu: MenuItem? = null
    var progressbartop: ProgressBar? = null
    lateinit var navView: NavigationView
    lateinit var layoutnavbar: RelativeLayout
    lateinit var databaseHelper_Data: DatabaseHelper
    private var isWindowFocused = false
    private var isBackPressed = false
    private var isAppWentToBg = false
    private lateinit var countDownTimer: CountDownTimer

    private val start = 600_000L
    var timer = start

    private lateinit var float_btn_alerta_usr: FloatingActionButton
    private lateinit var txt_alerta_usr: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        arguments?.let {
            urlConsulta  = it.getString(ARG_URL)


        }



    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.activity_browser_app, container, false)
        databaseHelper_Data = DatabaseHelper.getInstance(requireContext())






        webView = rootView.findViewById<View>(R.id.webView) as WebView
        swipe = rootView.findViewById<View>(R.id.swipe) as SwipeRefreshLayout
        swipe!!.setOnRefreshListener { WebAction() }


        with(rootView){

            val isvalidurl =    URLUtil.isValidUrl(urlConsulta)
            if(isvalidurl){
                WebAction()
            }else{
                Toast.makeText(requireContext(), "El portal de ayuda no se encuentra disponible en estos momentos, por favor intentelo más tarde", Toast.LENGTH_LONG).show()
            }


        }

        return rootView

    }




    fun WebAction() {




        webView!!.settings.javaScriptEnabled = true
        //webView!!.settings.setAppCacheEnabled(true) JHE 09022023
        webView!!.loadUrl(urlConsulta!!)
        swipe!!.isRefreshing = true
        webView!!.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                webView!!.loadUrl("file:///android_assets/error.html")
            }

            override fun onPageFinished(view: WebView, url: String) {
                // do your stuff here
                swipe!!.isRefreshing = false
            }
        }
    }


    companion object {

        const val ARG_URL = "column-url"



        @JvmStatic
        fun newInstance(strUrl: String) = BrowserAppActivity().apply {
            arguments = Bundle().apply {
                putString(ARG_URL, strUrl)
            }
        }

    }









}