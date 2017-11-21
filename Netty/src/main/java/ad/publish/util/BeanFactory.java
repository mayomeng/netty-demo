package ad.publish.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanFactory {

    private static  ApplicationContext context;

    /**
     * Beanを取得
     */
    public static void init() {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    /**
     * Beanを取得
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * Beanを取得
     */
    public static Object[] getBeans(String beanNames) {
        String[] beanNameArray = beanNames.split(",");
        Object[] objectArray = new Object[beanNameArray.length];
        for (int i = 0; i < beanNameArray.length; i++) {
            objectArray[i] = context.getBean(beanNameArray[i]);
        }

        return objectArray;
    }
}
