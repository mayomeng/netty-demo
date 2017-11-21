package mxy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerConnect {

    private Selector selector;

    public void initServer() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind( new InetSocketAddress("192.168.196.181", 8088) );

        this.selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     * @throws IOException
     */
    public void listen() throws IOException {

        while (true) {

/*            if(selector.select(5000) == 0){
                System.out.println("=====wait=====");
                continue;
            }*/
            selector.select();

            Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();

            while (iterator.hasNext()) {

                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    System.out.println("event is acceptable ~~");
                    accept( key );
                }

                if (key.isReadable()) {
                    System.out.println("event is readable ~~");
                    read( key );
                }

/*                if (key.isWritable() ){
                    write(key);
                }*/
            }
        }
    }

    public void accept(SelectionKey key) throws IOException{

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        System.out.println("channel id:" + channel.hashCode() );
        channel.configureBlocking(false);
        channel.register( this.selector, SelectionKey.OP_READ );

    }

    public void read(SelectionKey key) throws IOException{

        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate( 1024 );
        channel.read(buffer);
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println( "received from client:" + msg );
        // 写数据到客户端
        channel.write( ByteBuffer.wrap(("hi " + msg + ", server received your message").getBytes() ) );

    }

    public  void write(SelectionKey key) throws IOException{
        ByteBuffer buf = (ByteBuffer)key.attachment();
        buf.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        while(buf.hasRemaining()){
            sc.write(buf);
        }
        buf.compact();
    }

    /**
     * 启动服务端测试
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerConnect server = new ServerConnect();
        server.initServer();
        server.listen();
    }

}