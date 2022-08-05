package com.BallGame.net;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TestServer {
    private static ArrayList<Socket> csockets;

    public TestServer() {
        try {
            this.csockets = network.connectAsServer(1234);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestServer server = new TestServer();
        List<Integer> pipe = new ArrayList();
        Handler handler;
        try {
            handler = new Handler(server.csockets, pipe);
            handler.startListen();
            // if (!handler.pipe.isEmpty) {
            //     int tmp = pipe.get(0);
            //     System.out.println(tmp);
            //     pipe.remove(0);
            // }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
