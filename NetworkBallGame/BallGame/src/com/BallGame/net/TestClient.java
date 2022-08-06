package com.BallGame.net;

import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import com.BallGame.net.network.ClientResponse;

public class TestClient extends Thread {
    public Socket socket;
    private int uid;
    private List<Socket> cSockets;
    // private ClientResponse clientResponse;
    private OutputStream OutputStream;
    private InputStream InputStream;

    public int getUID() {
        return this.uid;
    }

    public TestClient() {
        try {
            ClientResponse clientResponse = network.connectAsClient("localhost", 3000);
            cSockets = new ArrayList<Socket>(1);
            System.out.println("connected");
            this.cSockets.add(this.socket);
            this.socket = clientResponse.getSocket();
            this.uid = clientResponse.getUID();
            this.OutputStream = socket.getOutputStream();
            this.InputStream = socket.getInputStream();
            System.out.println("my id: " + this.uid);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * send msgs to client handler
     */
    public void sendMsg(int msg) throws Exception {
        try {
            OutputStream.write(network.intToByteArr(msg));
        } catch (IOException e) {
            OutputStream.close();
            socket.close();
        }
    }

    /**
     * listen for msgs from server (broadcast msgs)
     */
    public int[] listenForMsgs() throws Exception {
        InputStream is = socket.getInputStream();
        byte[] p = new byte[4];
        is.read(p, 0, 4);
        int[] message = network.decode(network.byteArrToInt(p));
        return message;
    }

}
