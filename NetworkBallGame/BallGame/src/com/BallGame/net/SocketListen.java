package com.BallGame.net;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.util.concurrent.Callable;
import java.lang.Exception;

public class SocketListen implements Callable<Socket> {
    ServerSocket listenSocket;
    Boolean isCancelled = false;
    Boolean stfu = false;

    /**
     * The SocketListen class asynchronously waits for an incoming client connection
     * when submitted to ExecutorService.
     * If a client connects, the accepting socket is returned for future use.
     * If the executing thread is cancelled by invocation of cancel(), null is
     * returned.
     * No guarantees are made if the executing thread is cancelled otherwise.
     * 
     * @param s ServerSocket to be listened on.
     */
    protected SocketListen(ServerSocket s) {
        listenSocket = s;
    }

    public void silence() {
        stfu = true;
    }

    public void unsilence() {
        stfu = false;
    }

    public void cancel() {
        isCancelled = true;
    }

    @Override
    public Socket call() throws Exception {
        Socket acceptingSocket;
        while (!isCancelled) {
            try {
                acceptingSocket = listenSocket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }
            if (!stfu)
                System.out.println("Player connected.");
            return acceptingSocket;
        }
        return null;
    }

}