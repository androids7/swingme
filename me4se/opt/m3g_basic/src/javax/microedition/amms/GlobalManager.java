package javax.microedition.amms;

public class GlobalManager {

    public static MediaProcessor createMediaProcessor(String string) {
        if ("image/raw".equals(string)) {
            return new AwtJpegEncoder();
        }
        throw new RuntimeException();
    }

}
