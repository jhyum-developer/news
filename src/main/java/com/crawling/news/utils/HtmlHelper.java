package com.crawling.news.utils;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

public class HtmlHelper {
    private static Logger logger = LoggerFactory.getLogger(HtmlHelper.class);

    public static String getOgImage(Document document, String documentUrl) {
        String url = null;
        Element metaOgImage  = document.select("meta[property=og:image]").first();
        if (metaOgImage == null) {
            Elements alterImgs = document.select("img");
            for (Element img : alterImgs) {
               if (img != null) {
                    int width = 0;
                    int height = 0;
                    try {
                        width = Integer.parseInt(img.attr("width"));
                        height = Integer.parseInt(img.attr("height"));
                    } catch (Exception e) {
                    }

                    if (width > 400 && height > 300) {
                        url = img.attr("src");
                        break;
                    }
                }
            }
        } else {
            url = metaOgImage.attr("content");
        }
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            int firstSlashIndex = documentUrl.indexOf("/");
            String host = documentUrl;
            if (firstSlashIndex > 0) {
                host = documentUrl.substring(0, firstSlashIndex);
            }
            if (url.startsWith("/"))
                url = host + url;
            else
                url = host + "/" + url;
        }

        return url;
    }

    public static String getImageType(String url) {
        int dotIndex = url.lastIndexOf(".");
        String type = "png";
        if (dotIndex > 0)
            type = url.substring(dotIndex + 1);
        if (type.startsWith("png"))
            type = "png";
        else if (type.startsWith("jpg") || type.startsWith("jpeg"))
            type =  "jpeg";
        else if (type.startsWith("gif"))
            type =  "gif";
        else
            type =  "bmp";

        return type;
    }

    public static String makeThumbnailFromOgImage(String url) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            URL imgLocation = new URL(url);
            HttpURLConnection connection;
            if(url.indexOf("https://")!=-1){
                final SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, getTrustingManager(), new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                connection = (HttpURLConnection)imgLocation.openConnection();
            }
            else{
                connection = (HttpURLConnection)imgLocation.openConnection();
            }
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedImage img = ImageIO.read(connection.getInputStream());

            // java-image-scaling 라이브러리
            int width = 120;
            double height = 120 * (img.getHeight() / (1.0 * img.getWidth()));
            MultiStepRescaleOp rescale = new MultiStepRescaleOp(width, (int)height);
            rescale.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Soft);

            BufferedImage resizedImage = rescale.filter(img, null);
            String type = getImageType(url);

            ImageIO.write(resizedImage, type, bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            String imageString = encoder.encode(imageBytes);
            return imageString;

        } catch (Exception e) {
            logger.info(Util.makeStackTrace(e));
        }
        return null;
    }
    private static TrustManager[] getTrustingManager() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        return trustAllCerts;
    }

}