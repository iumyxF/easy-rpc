package com.example.rpc.server.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/13 11:31
 */
public class RequestDecoderTest {
    
    @Test
    public void buffTest(){
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeByte(1);
        buffer.writeByte(1);
        buffer.writeByte(1);
        buffer.writeByte(1);
        buffer.writeByte(1);
        buffer.writeLong(Long.MAX_VALUE);
        buffer.writeInt(10);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        buffer.writeByte(127);
        buffer.writeByte(127);
        buffer.writeByte(127);
        buffer.writeByte(127);
        buffer.writeByte(127);

        System.out.println(Arrays.toString(buffer.array()));


        System.out.println("-----------");
        byte[] bytes = ByteBufUtil.getBytes(buffer, 0, 17 + buffer.getInt(13), false);
        System.out.println(Arrays.toString(bytes));
        //ByteBuf buf = buffer.readBytes(17 + buffer.getInt(13));
        //System.out.println(Arrays.toString(buf.array()));
    }

    @Test
    public void doGet() throws IOException {
        URL url = new URL("https://www.baidu.com");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        System.out.println(content);
    }
}