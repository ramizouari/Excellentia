package com.excellentia.judge.configuration;

import jakarta.annotation.PostConstruct;

public class Host
{
    private String url;

    private String host;
    private int port;
    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    @PostConstruct
    public void init()
    {
        var S=url.replaceFirst("\\w+://", "").split(":");
        host=S[0];
        port=Integer.parseInt(S[1]);
    }

    public void setUrl(String url) {
        this.url = url;
        init();
    }

    public String getUrl()
    {
        return url;
    }
}
