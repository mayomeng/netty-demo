/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ad.publish.client.audience;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import ad.publish.client.audience.proto.coder.AudienceProtobufDecoder;
import ad.publish.client.audience.proto.coder.AudienceProtobufEncoder;

public class AudiencePoolHandler extends AbstractChannelPoolHandler {


    @Override
    public void channelCreated(Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        p.addLast(new AudienceProtobufEncoder());

        p.addLast(new ProtobufVarint32FrameDecoder());
        p.addLast(new AudienceProtobufDecoder());
        p.addLast(new AudienceRequestHandler());
    }
}