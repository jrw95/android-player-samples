package com.brightcove.player.samples.exoplayer.basic;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

class VideoObjectInfo implements Serializable {

    private String contentUrl;
    private String licenseUrl;
    private String name;

    static VideoObjectInfo create (final String name, final String contentUrl) {
        return new VideoObjectInfo (name, contentUrl, "");
    }

    static VideoObjectInfo create (@NonNull final String name, @NonNull final String contentUrl, @Nullable final String licenseUrl) {
        return new VideoObjectInfo(name, contentUrl, licenseUrl);
    }

    private VideoObjectInfo(String name, String contentUrl, String licenseUrl) {
        if (!TextUtils.isEmpty(name)) {
            this.name = name;
        }
        else {
            throw new IllegalStateException("Name can not be empty or null");
        }

        if (!TextUtils.isEmpty(contentUrl)) {
            this.contentUrl = contentUrl;
        }
        else {
            throw new IllegalStateException("Content URL can not be empty or null");
        }

        if (!TextUtils.isEmpty(licenseUrl)) {
            this.licenseUrl = licenseUrl;
        }
    }

    String getName() { return this.name; }

    String getContentUrl() { return this.contentUrl; }

    String getLicenseUrl() { return this.licenseUrl; }
}
