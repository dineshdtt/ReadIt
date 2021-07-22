package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.classes.ClipBoard;
import in.tts.classes.TTS;
import in.tts.model.Browser;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

import static java.security.AccessController.getContext;

public class BrowserActivity extends AppCompatActivity {

    private ProgressBar superProgressBar;
    private WebView superWebView;
    private RelativeLayout rl;
    private PrefManager prefManager;
    private List<String> linkList;
    private View menuBookMark;
    private CheckBox cbMenu;
    private TTS tts;
    private String historyUrl = "";
    private String text = "";
    private MenuItem menuSpeak;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_browser);
            checkInternetConnection();
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.app_name));
            }

            prefManager = new PrefManager(BrowserActivity.this);
            if (getIntent() != null) {
                superProgressBar = findViewById(R.id.myProgressBar);
                superWebView = findViewById(R.id.myWebView);
                rl = findViewById(R.id.llBrowser);

                superProgressBar.setMax(100);

                if (getIntent().getStringExtra("Data") != null) {
                    superWebView.loadUrl("https://www.google.co.in/search?q="
                            + getIntent().getStringExtra("Data")
                            + "&oq=df&aqs=chrome..69i57j69i60l3j0l2.878j0j7&sourceid=chrome&ie=UTF-8");
                } else if (getIntent().getStringExtra("url") != null) {
                    superWebView.loadUrl(getIntent().getStringExtra("url"));
                } else {
                    superWebView.loadUrl("https://www.google.co.in");
                }

//                superWebView.setHapticFeedbackEnabled(false);
                superWebView.getSettings().setJavaScriptEnabled(true);
                superWebView.getSettings().setSupportZoom(true);
                superWebView.getSettings().setBuiltInZoomControls(true);
                superWebView.getSettings().setDisplayZoomControls(true);
                superWebView.getSettings().setLoadWithOverviewMode(true);
                superWebView.getSettings().setUseWideViewPort(true);
                superWebView.clearCache(true);
                superWebView.setHorizontalScrollBarEnabled(true);
            }

            superWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, final String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    try {

                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                    super.onPageFinished(view, url);
                    try {
                        startOfHistory();
                        tts = new TTS(BrowserActivity.this);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                Document doc = Jsoup.connect(url).get();
//                                Log.d("TAG_WEb ", " DATa 23 " + doc.getElementsByTag("script") );
//                                Log.d("TAG_WEb ", " DATa 24" + doc.getElementsByTag("body").text() );
//                                text = Jsoup.connect(url).get().getElementsByTag("body").toString().replaceAll("\\<.*?\\>", "");
                                    text = Jsoup.connect(url).get().getElementsByTag("body").text();
                                    if (text.trim().length() > 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (menuSpeak != null) {
                                                    menuSpeak.setVisible(true);
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                }
                            }
                        });


                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    // open in Webview
//                    if (url.contains("android_asset") ){
//                        // Can be clever about it like so where myshost is defined in your strings file
//                        // if (Uri.parse(url).getHost().equals(getString(R.string.myhost)))
//                        return false;
//                    }
//                    // open rest of URLS in default browser
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                    return true;
//                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });
            superWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    try {
                        superProgressBar.setVisibility(View.VISIBLE);
                        superProgressBar.setProgress(newProgress);
                        if (newProgress == 100) {
                            superProgressBar.setVisibility(View.GONE);
                            linkList = prefManager.populateSelectedSearch();
                            toUpdateBookMarkIcon();
                        } else {
                            superProgressBar.setVisibility(View.VISIBLE);

                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    try {
//                        getSupportActionBar().setTitle(title);
                        if (getSupportActionBar() != null) {
                            CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, title);
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    super.onReceivedIcon(view, icon);
//                    ImageView iv = new ImageView(BrowserActivity.this);
//                    iv.setImageBitmap(icon);
//                    rl.addView(iv);

                }
            });

            superWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    try {
                        Uri myUri = Uri.parse(url);
                        Intent superIntent = new Intent(Intent.ACTION_VIEW);
                        superIntent.setData(myUri);
                        startActivity(superIntent);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            // Get clipboard manager object.
            Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
            final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
            superWebView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ClipData clipData = ClipData.newPlainText("", "");
                        clipboardManager.setPrimaryClip(clipData);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            superWebView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

//                    try {
//                        ClipData clipData = ClipData.newPlainText("", "");
//                        clipboardManager.setPrimaryClip(clipData);
                    ClipBoard.ToPopup(BrowserActivity.this, BrowserActivity.this, null);
//                    } catch (Exception | Error e) {
//                        e.printStackTrace();
//                        Crashlytics.logException(e);
//                    }
////                    Toast.makeText(BrowserActivity.this, "jhjm ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void checkInternetConnection() {
        try {
            if (CommonMethod.isOnline(BrowserActivity.this)) {
                new BrowserActivity();
            } else {
                CommonMethod.toDisplayToast(BrowserActivity.this, getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public boolean startOfHistory() {
        try {
//            Log.d("TAGWEB", "WEB Histroy");
            WebBackForwardList currentList = superWebView.copyBackForwardList();
//            Log.d("TAGWEB", " Web " + currentList.getCurrentItem() + ":" + currentList.getSize() + ":" + currentList.getCurrentItem());
            for (int i = 0; i < currentList.getSize(); i++) {
                WebHistoryItem item = currentList.getItemAtIndex(i);
//                Log.d("TAGWEB", " web item " + item.getTitle());
                if (item != null) { // Null-fence in case they haven't called loadUrl yet (CB-2458)
                    String url = item.getUrl();
                    String currentUrl = superWebView.getUrl();
//                    Log.d("TAGWEB", i + ". The current URL is: " + currentUrl);
//                    Log.d("TAGWEB", i + ". The URL at item 0 is:" + url);
//                    return currentUrl.equals(url);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return false;
    }

    private void toUpdateBookMarkIcon() {
        try {
            if (linkList != null) {
                if (linkList.contains(superWebView.getUrl())) {
                    cbMenu.setChecked(true);
                } else {
                    cbMenu.setChecked(false);
                }
            } else {
                linkList = new ArrayList<>();
                cbMenu.setChecked(false);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.browser_menu, menu);
            menuSpeak = menu.findItem(R.id.menuSpeakBrowser);
            menuBookMark = menu.findItem(R.id.menuBookmark).getActionView();
            cbMenu = menuBookMark.findViewById(R.id.cbBookmark);
            cbMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cbMenu.isChecked()) {
                        toChangeData(true);
                    } else {
                        toChangeData(false);
                    }
                }
            });

            linkList = prefManager.populateSelectedSearch();
            if (linkList != null) {
                if (linkList.contains(superWebView.getUrl())) {
                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_black_24dp);
                } else {
                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
                }
            } else {
                linkList = new ArrayList<>();
                menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }


        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menuSpeakBrowser:
                    toSpeakWebPage();
                    break;

                case R.id.menuBack:
                    onBackPressed();
                    break;

                case R.id.menuForward:
                    GoForward();
                    break;

                case R.id.menuBookmarksList:
                    startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
                    break;

                case R.id.menuReload:
                    superWebView.reload();
                    break;

                case R.id.menuBookmark:
                    toChangeData(true);
                    break;

                case android.R.id.home:
                    onBackPressed();
                    break;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return true;
    }

    private void toSpeakWebPage() {
        try {
            Log.d("WEB", "toSpeakWebPage  " + text.length() + ":" + text);
            if (text.trim().length() > 0) {
                tts.SpeakLoud(text.replaceAll("&nbsp;", "\\s"), "AUD_Web" + superWebView.getTitle() + System.currentTimeMillis());
                CommonMethod.toDisplayToast(BrowserActivity.this, "Sound will play...");
//                tts.toSaveAudioFile(text.replaceAll("&nbsp;", "\\s"), "AUD_Web" + superWebView.getTitle() + System.currentTimeMillis());
            } else {
                CommonMethod.toDisplayToast(BrowserActivity.this, " Unable to fetch data ");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toChangeData(boolean b) {
        try {
            String message;
            if (!b) {
                linkList.remove(superWebView.getUrl().trim().replaceAll("\\s+", "%20"));
                message = "Bookmark Removed";
            } else {
                linkList.add(superWebView.getUrl().trim().replaceAll("\\s+", "%20"));
                message = "Bookmarked";
            }
            prefManager.setSearchResult(linkList);
            Snackbar snackbar = Snackbar.make(rl, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void GoForward() {
        try {
            if (superWebView.canGoForward()) {
                superWebView.goForward();
            } else {
                Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }

            if (superWebView.canGoBack()) {
                superWebView.goBack();
            } else {
                finish();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onPause() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onDestroy();
    }

}

/*
*  @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        if(url.toLowerCase().contains("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    @SuppressLint("NewApi")
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        if(!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    */

/*
AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Document doc = Jsoup.connect(url).get();
                                Log.d("WEB", "Doc " + doc + ":lll\n" + doc.getAllElements());
                                Log.d("WEB", "Doc 1" + doc.getElementsByTag("body"));
                                Elements newsHeadlines = doc.getElementsByAttribute("value1");
                                Log.d("WEB", "Ele  " + newsHeadlines.text());
//                        String ip = newsHeadlines[0].text().split("**")[1];
//                                String text = "<B>I don't want this to be bold<\\B>";
//                                System.out.println(text);
                               String text = doc.getElementsByTag("body").toString().replaceAll("\\<.*?\\>", "");
//                                System.out.println(text);
                                Log.d("WEB", "text  " + text);


                                URL url1 = new URL(url);
                                URLConnection yc = url1.openConnection();
                                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                                String inputLine;
                                StringBuilder builder = new StringBuilder();
                                while ((inputLine = in.readLine()) != null)
                                    builder.append(inputLine.trim());
                                in.close();
                                String htmlPage = builder.toString();
                                Log.d("WEB", "text1  " + htmlPage);
                                String versionNumber = htmlPage.replaceAll("\\<.*?>","");
                                Log.d("WEB", "text2  " + versionNumber);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    */