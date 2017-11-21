package ad.publish.io.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ad.publish.info.AdColumn;
import ad.publish.task.HandlerTask;

@Sharable
@Component("DistributionHandler")
public class DistributionHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static Logger log = LoggerFactory.getLogger(DistributionHandler.class);

    @Override
    public boolean acceptInboundMessage(Object param) {

        HttpRequest request = (HttpRequest)param;

        if ("/favicon.ico".equals(request.uri()) || !request.uri().contains("/getAdImg")) {
            return false;
        }

        return true;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject param) throws Exception {

        try {
            HttpRequest request = (HttpRequest)param;

            QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = decoderQuery.parameters();
            AdColumn adColumn = new AdColumn();
            adColumn.setAdColumnId(params.get("id").get(0));
            adColumn.setType(params.get("type").get(0));
            HandlerTask task = new HandlerTask();
            task.setParams(adColumn);
            //task.setContext(ctx);

            ctx.write(task);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

}