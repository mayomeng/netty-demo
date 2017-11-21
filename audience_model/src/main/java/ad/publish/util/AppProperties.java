package ad.publish.util;

import java.io.IOException;
import java.util.ResourceBundle;

public class AppProperties {

    static {
        try {
            AppProperties.init();
        } catch (IOException e) {

        }
    }

    private static ResourceBundle applicationProperties;

    public static void init() throws IOException {
        applicationProperties = ResourceBundle.getBundle("application");
    }

    public static String getValue(String key) {
        return applicationProperties.getString(key);
    }
}
