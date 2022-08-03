package com.BallGame.net;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.lang.Exception;

public class SocketListen implements Callable<Socket>{
    private ServerSocket listenSocket;
    private Boolean stopped = false;
    private Boolean silent = false;

    /** 
     * Constructs a SocketListen instnace which asynchronously waits for an incoming client connection.
     * If a client connects, the accepting socket is returned for future use.
     * @param s ServerSocket to be listened on.
     */
    protected SocketListen(ServerSocket s){ listenSocket = s; }
    public void silence(){ silent = true; }
    public void unsilence(){ silent = false; }
    public void stop(){ stopped = true; }

    @Override
    public Socket call() throws Exception{
        Socket acceptingSocket;
        while(!stopped){
            try {
                acceptingSocket = listenSocket.accept();    
            } catch (SocketTimeoutException e) {
                continue;
            }
            if(!silent)
                System.out.println("Player connected.");
            return acceptingSocket;
        }
        return null;
    }

}