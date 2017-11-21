package ad.publish.io.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ad.publish.info.AdColumn;
import ad.publish.info.TypeEnum;
import ad.publish.logic.CallBackHandlerForLogic;
import ad.publish.logic.PublishProducer;
import ad.publish.logic.unit.LogicUnit;
import ad.publish.task.Task;
import ad.publish.util.BeanFactory;
import ad.publish.util.CommonUtil;

@Sharable
@Component("DrawImageHandler")
public class DrawImageHandler extends ChannelOutboundHandlerAdapter {

    @Autowired
    @Qualifier("AdEventProducer")
    private PublishProducer publishProducer;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        Task task = (Task)msg;
        task.setContext(ctx);

        AdColumn adColumn = (AdColumn)task.getParams();
        if (TypeEnum.audience.toString().equals(adColumn.getType())) {
            publishProducer.put(task);
        } else {
            writeImg(ctx, task);
        }
    }

    private void writeImg(ChannelHandlerContext ctx, Task task) {

        LogicUnit logicUnit = (LogicUnit)BeanFactory.getBean("LogicUnitImage");
        logicUnit.setTask(task);

        AdColumn adColumn = (AdColumn)task.getParams();
        CallBackHandlerForLogic callBackHandler = new CallBackHandlerForLogic();
        callBackHandler.setParams(adColumn.getAdColumnId());

        try {
            CommonUtil.writeImg(ctx, (File)logicUnit.getResult(), callBackHandler);
        } catch (Exception e) {
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
