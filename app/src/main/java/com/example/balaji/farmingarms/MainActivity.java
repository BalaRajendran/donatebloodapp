package com.example.balaji.farmingarms;
import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private WebView mwebView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private int STORAGE_PERMISSION_CODE = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //WebView
        mwebView = (WebView) findViewById(R.id.myWebView);
        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);
        WebSettings webSettings = mwebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        mwebView.setWebChromeClient(new WebChromeClient());
        //improve webView performance
        mwebView.getSettings().setBuiltInZoomControls(true);
        mwebView.getSettings().setDisplayZoomControls(false);
        mwebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mwebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mwebView.getSettings().setAppCacheEnabled(true);
        mwebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setDomStorageEnabled(true);
            webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
            mwebView.loadUrl("http://blooddonate.epizy.com/");
        } catch (Exception e) {
            e.printStackTrace();
        }
                requestStoragePermission();
        mwebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                String path = Environment.getDataDirectory().getAbsolutePath().toString() + "/storage/emulated/0/appFolder";
                File mFolder = new File(path);
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }
                File Directory = new File("/sdcard/blooddonate/");
                Directory.mkdirs();
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setDescription("Downloading video from kaizzan");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir("kaizzan", "Kaizzan video");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();
            }
        });
        mwebView.setWebViewClient(new MyWebviewClient(){
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                Toast.makeText(MainActivity.this, "Please Turn on the Internet Connection Or Wifi", Toast.LENGTH_SHORT).show();
            }
        });
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mwebView.reload();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }
    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                Intent i = new Intent(
                        android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(
                        android.content.Intent.EXTRA_TEXT, "Register Your Details to donate your blood http://blooddonate.epizy.com/");
                startActivity(Intent.createChooser(
                        i,
                        "Share Via"));
                break;
        }
        int id = item.getItemId();
        if (id == R.id.home) {
            mwebView.loadUrl("http://blooddonate.epizy.com/");
        }
        else if (id == R.id.nav_feedback) {
            Intent intent =new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class MyWebviewClient extends WebViewClient {
        ProgressDialog pd = null;
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd=new ProgressDialog(MainActivity.this);
            pd.setTitle("Please Wait..");
            pd.setMessage("Website is Loading..");
            pd.show();
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            pd.dismiss();
            super.onPageFinished(view, url);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mwebView.canGoBack()) {
                        mwebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }
}