package ad.publish.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallBackHandlerForLogic implements CallBackHandler {

    private Object param;

    public void setParams(Object param) {
        this.param = param;
    }

    @Override
    public void process() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        System.out.println(sdf.format(new Date(startTime)) + " :" + " write over : " + param);
    }

}
