package com.easou.androidsdk.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easou.androidsdk.login.AuthAPI;
import com.easou.androidsdk.login.RegisterAPI;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.EucAPIException;
import com.easou.androidsdk.login.service.EucApiResult;
import com.easou.androidsdk.util.ThreadPoolManager;
import com.easou.espay_user_lib.R;

/**
 * created by xiaoqing.zhou
 * on 2020/8/3
 * fun
 */
public class WebViewDialog extends BaseDialog {

    public WebViewDialog(@NonNull Context context, int animation, int gravity, float mWidth, int mHeight,String url) {
        super(context, animation, gravity, mWidth, mHeight);
        mContext = context;
        this.url = url;
    }

    private View mView;
    private Context mContext;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_webview, null);
        setContentView(mView);
        initView();
    }

    private void initView() {
        WebView webView = (WebView) mView.findViewById(R.id.webview_help);
        ImageView mCloseWeb = (ImageView) mView.findViewById(R.id.iv_close_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        });
        mCloseWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
