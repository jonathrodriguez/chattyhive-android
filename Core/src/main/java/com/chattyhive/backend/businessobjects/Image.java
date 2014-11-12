package com.chattyhive.backend.businessobjects;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

/**
 * Created by Jonathan on 02/11/2014.
 */
public class Image {
    public static final int DENSITY_NORMAL = 0;
    public static final int DENSITY_LIGHT = 1;

    public enum ImageSize { small, medium, large, xlarge, file }

    InputStream smallThumbnail;
    InputStream mediumThumbnail;
    InputStream largeThumbnail;
    InputStream xlargeThumbnail;
    InputStream fileImage;

    DataProvider dataProvider;
    String fileURL;

    public Image(String fileURL) {
        this.dataProvider = Controller.GetRunningController().getDataProvider();
        this.fileURL = fileURL;
        this.OnImageLoaded = new Event<EventArgs>();
    }

    public Event<EventArgs> OnImageLoaded;

    public void loadImage(final ImageSize imageSize, final int imageDensity) {
        if ((imageDensity < 0) || (imageDensity > 1)) throw new InvalidParameterException("imageDensity must be 0 for normal density version or 1 for light version.");

        Boolean alreadyLoaded = false;

        switch (imageSize) {
            case small:
                alreadyLoaded = (smallThumbnail != null);
                break;
            case medium:
                alreadyLoaded = (mediumThumbnail != null);
                break;
            case large:
                alreadyLoaded = (largeThumbnail != null);
                break;
            case xlarge:
                alreadyLoaded = (xlargeThumbnail != null);
                break;
            case file:
                alreadyLoaded = (fileImage != null);
                break;
        }


        if (alreadyLoaded) {
            if (this.OnImageLoaded != null)
                this.OnImageLoaded.fire(this, EventArgs.Empty());
            return;
        }

        new Thread(){
            @Override
            public void run() {
                internalLoadImage(imageSize,imageDensity);
            }
        }.start();
    }

    private void internalLoadImage (ImageSize imageSize, int imageDensity) {
        switch (imageSize) {
            case small:
                if (smallThumbnail != null) {
                    // TODO: think about checking updates
                } else {
                    String url = this.fileURL.replace("file_","small_");
                    //String url = this.fileURL;
                    smallThumbnail = dataProvider.getImage(url);
                }
                break;
            case medium:
                if (mediumThumbnail != null) {
                    // TODO: think about checking updates
                } else {
                    String url = this.fileURL.replace("file_","medium_");
                    //String url = this.fileURL;
                    mediumThumbnail = dataProvider.getImage(url);
                }
                break;
            case large:
                if (largeThumbnail != null) {
                    // TODO: think about checking updates
                } else {
                    String url = this.fileURL.replace("file_","large_");
                    //String url = this.fileURL;
                    largeThumbnail = dataProvider.getImage(url);
                }
                break;
            case xlarge:
                if (xlargeThumbnail != null) {
                    // TODO: think about checking updates
                } else {
                    String url = this.fileURL.replace("file_","xlarge_");
                    //String url = this.fileURL;
                    xlargeThumbnail = dataProvider.getImage(url);
                }
                break;
            case file:
                if (fileImage != null) {
                    // TODO: think about checking updates
                } else {
                    String url = this.fileURL;
                    fileImage = dataProvider.getImage(url);
                }
                break;
        }

        //String url = this.fileURL;
        //fileImage = dataProvider.getImage(url);

        if (this.OnImageLoaded != null)
            this.OnImageLoaded.fire(this,EventArgs.Empty());
    }

    public InputStream getImage(final ImageSize imageSize, final int imageDensity) {
        if ((imageDensity < 0) || (imageDensity > 1)) throw new InvalidParameterException("imageDensity must be 0 for normal density version or 1 for light version.");

        switch (imageSize) {
            case small:
                if (smallThumbnail != null)
                    return smallThumbnail;
                break;
            case medium:
                if (mediumThumbnail != null)
                    return mediumThumbnail;
                break;
            case large:
                if (largeThumbnail != null)
                    return largeThumbnail;
                break;
            case xlarge:
                if (xlargeThumbnail != null)
                    return xlargeThumbnail;
                break;
            case file:
                if (fileImage != null)
                    return fileImage;
                break;
        }

        new Thread(){
            @Override
            public void run() {
                internalLoadImage(imageSize,imageDensity);
            }
        }.start();

        return null;
    }

    public void freeMemory () {
        if (smallThumbnail != null)
            try { smallThumbnail.close(); } catch (IOException e) { e.printStackTrace(); } finally { smallThumbnail = null; }
        if (mediumThumbnail != null)
            try { mediumThumbnail.close(); } catch (IOException e) { e.printStackTrace(); } finally { mediumThumbnail = null; }
        if (largeThumbnail != null)
            try { largeThumbnail.close(); } catch (IOException e) { e.printStackTrace(); } finally { largeThumbnail = null; }
        if (xlargeThumbnail != null)
            try { xlargeThumbnail.close(); } catch (IOException e) { e.printStackTrace(); } finally { xlargeThumbnail = null; }
        if (fileImage != null)
            try { fileImage.close(); } catch (IOException e) { e.printStackTrace(); } finally { fileImage = null; }
    }
}
