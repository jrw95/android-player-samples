package com.brightcove.player.samples.offlineplayback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.network.HttpRequestConfig;

/**
 * Playlist model is an immutable DTO that holds important values related to a Playlist.
 */
public class VideoModel {
    /**
     * The unique primary identifier of the videoModel on the video cloud. This value may change
     * during server side maintenance. Please use {@link #referenceId} instead.
     */
    public final String id;

    /**
     * The unique reference identifier of the videoModel on the video cloud as set by the cloud account owner.
     */
    public final String referenceId;

    /**
     * The display name of the videoModel.
     */
    public final String displayName;

    /**
     * Constructs a new videoModel model.
     *
     * @param id          The unique primary identifier of the videoModel on the video cloud.
     * @param referenceId The unique reference identifier of the videoModel on the video cloud.
     * @param displayName The display name of the videoModel.
     * @throws IllegalArgumentException if both {@link #id} and {@link #referenceId} are null.
     * @throws IllegalArgumentException if the videoModel display name is null.
     */
    public VideoModel(@Nullable String id, @Nullable String referenceId, @NonNull String displayName) {
        if (id == null && referenceId == null) {
            throw new IllegalArgumentException("Video must have a non-null identifier or reference identifier");
        }

        this.id = id;
        this.referenceId = referenceId;
        this.displayName = displayName;
    }

    /**
     * Overrides the base implementation to return the videoModel display name.
     *
     * @return the videoModel name.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Creates a videoModel model with the given unique primary identifier and display name.
     *
     * @param id   The unique primary identifier of the videoModel on the video cloud.
     * @param displayName The display name of the videoModel.
     * @return reference to the newly created videoModel model.
     */
    public static VideoModel byId(@NonNull String id, @NonNull String displayName) {
        return new VideoModel(id, null, displayName);
    }

    /**
     * Creates a videoModel model with the given unique reference identifier and displayName.
     *
     * @param referenceId The unique reference identifier of the videoModel on the video cloud.
     * @param displayName        The display name of the videoModel.
     * @return reference to the newly created videoModel model.
     */
    public static VideoModel byReferenceId(@NonNull String referenceId, @NonNull String displayName) {
        return new VideoModel(null, referenceId, displayName);
    }

    /**
     * Searches the given catalog for this videoModel.
     *
     * @param catalog  reference to the catalog to be searched.
     * @param listener reference to a listener instance that will be notified when the search is complete.
     * @throws NullPointerException if the catalog or listener is null.
     */
    public void findVideo(@NonNull Catalog catalog, @NonNull HttpRequestConfig httpRequestConfig, @NonNull VideoListener listener) {
        if (referenceId != null) {
            catalog.findVideoByReferenceID(referenceId, httpRequestConfig, listener);
        } else if (id != null) {
            catalog.findVideoByID(id, httpRequestConfig, listener);
        }
    }
}
