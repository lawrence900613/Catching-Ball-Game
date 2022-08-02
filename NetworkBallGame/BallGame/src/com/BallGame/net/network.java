Skip to content
Search or jump to…
Pull requests
Issues
Marketplace
Explore
 
@junpin-foo 
junpin-foo
/
CMPT371-Project
Private
Code
Issues
Pull requests
1
Actions
Projects
Security
Insights
Settings
CMPT371-Project/NetworkBallGame/BallGame/src/com/BallGame/net/network.java /
@limaniner420
limaniner420 improved clarity; better thread/field safety
Latest commit dbfd76c 14 hours ago
 History
 1 contributor
139 lines (130 sloc)  5.13 KB

package com.BallGame.net;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;

public class network {
    public static final int MAX_CLIENTS = 3; //Should not exceed 3. See encoding scheme.

    /**  
     * Encapsulement for return values of connectAsClient as a tuple of [Socket, Int].
     */
    public static class ClientResponse{
        Socket s;
        int uid;
        protected ClientResponse(Socket s, int uid){
            this.s = s;
            this.uid = uid;
        }
        /**
         * @return Socket connected to server.
          */
        public Socket getSocket(){ return s; }
        /**
         * @return UID assigned by the server.
          */
        public int getUID(){ return uid; }
    }
    /**
     * Listens for up to {@value #MAX_CLIENTS} connections on provided port and returns the client-connected sockets for future use.
     * @param port port to listen on.
     * @return array of client-connected sockets.
     * @throws Exception 
     */
    public static ArrayList<Socket> connectAsServer(int port) throws Exception{
        
        ServerSocket ssocket = new ServerSocket(port);
        ssocket.setSoTimeout(200);
        ArrayList<Socket> csockets = new ArrayList<Socket>(MAX_CLIENTS);
        SocketListen[] listeners = new SocketListen[MAX_CLIENTS];
        
        ExecutorService es = Executors.newFixedThreadPool(MAX_CLIENTS);
        ArrayList<FutureTask<Socket>> SLT = new ArrayList<FutureTask<Socket>>(MAX_CLIENTS);
        // submits all listeners for async listening
        for(int i = 0; i < MAX_CLIENTS; i++){
            listeners[i] = new SocketListen(ssocket);
            SLT.add(new FutureTask<Socket>(listeners[i]));
            es.submit(SLT.get(i));
        }
        
        System.out.println("Waiting for players...\nPress any key to start game.");
        System.in.read();
        
        for(int i = 0; i < MAX_CLIENTS; i++){
            if(SLT.get(i).isDone()){ //if connection acq'd, prep socket for return
                csockets.add(SLT.get(i).get());
                csockets.get(i).getOutputStream().write(i); //indicate uid to client
            }
            else //cancel thread
                listeners[i].stop();
        }
        ssocket.close();
        es.shutdown();
        return csockets;
    }
    /**
     * Connect to server with provided host and port.
     * @param host hostname of the server.
     * @param port listening port of the server.
     * @return ClientResponse containing the connected socket and UID assigned.
     * @throws Exception 
     */
    public static ClientResponse connectAsClient(String host, int port) throws Exception{
        Socket socket = new Socket(host, port);
        int uid = socket.getInputStream().read();
        return new ClientResponse(socket, uid);
    }

    /* Preliminary encoding scheme
     * Left
     * b    31-30   : UID
     * b    29-26   : unused
     * b    25      : inform unlock
     * b    24      : inform lock
     * b    23-12   : x
     * bits 11-0    : y
     * Right
     */

    public static int encode(int[] arr){
        return encode(arr[0], arr[1] == 1, arr[2] == 1, arr[3], arr[4]);
    }
    public static int encode(int UID, int unlock, int lock, int x, int y){
        return encode(UID, unlock == 1, unlock == 1, x, y);
    }
    /**
     * Encodes provided information as a 32-bit string wrapped as an integer.
     * @param UID Bit 31-30. The UserID returned at connection.
     * @param unlock Bit 25. Indicates to recipients if ball is released.
     * @param lock Bit 24. Indicates to recipients if ball is held.
     * @param x Bit 23-12. Indicates current x position of the ball.
     * @param y Bit 11-0. Indicates current y position of the ball
     * @return a 32-bit string containing encoded information wrapped as an integer.
     * 
     */
    public static int encode(int UID, Boolean unlock, Boolean lock, int x, int y){
        
        int info = 0x00000000;
        info |= (UID << 30);
        //
        info |= (unlock ? 0x02000000 : 0);
        info |= (lock ? 0x01000000 : 0);
        info |= ((x & 0x00000FFF) << 12);
        info |= (y & 0x00000FFF);
        return info;
    }
    /**
     * Decodes the 32-bit string into an integer array.
     * 0: UID: Bit 31-30. The UserID returned when at connection.
     * 1: unlock: Bit 25. Indicates to recipients if ball is released.
     * 2: lock: Bit 24. Indicates to recipients if ball is held.
     * 3: x: Bit 23-12. Indicates current x position of the ball.
     * 4: y: Bit 11-0. Indicates current y position of the ball
     * @param info_en 32-bit string wrapped as an integer.
     * @return an integer array containing information decoded from provided info_en.
     * 
     */
    public static int[] decode(int info_en){
        int[] info = new int[5];
        info[0] = (info_en & 0xC0000000) >>> 30;
        info[1] = (info_en & 0x02000000) >>> 25;
        info[2] = (info_en & 0x01000000) >>> 24;
        info[3] = (info_en & 0x00FFF000) >>> 12;
        info[4] = (info_en & 0x00000FFF);
        return info;
    }
}
Footer
© 2022 GitHub, Inc.
Footer navigation
Terms
Privacy
Security
Status
Docs
Contact GitHub
Pricing
API
Training
Blog
About
You have unread notifications
