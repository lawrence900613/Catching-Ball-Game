package com.BallGame.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.net.Socket;

public class Handler{
    List<Integer> pipe;
    List<Socket> cSockets;
    ExecutorService es;
    AsyncListen[] listeners;
    Boolean isShutdown;

    /**
     * Constructs threads that listen on all sockets passed into the function, piping the read output into the provided list.
     * @param _cSockets List of sockets to be listened on.
     * @param _pipe List to have the read packets piped into
     * @throws Exception 
     */
    public Handler(List<Socket> _cSockets, List<Integer> _pipe) throws Exception{
        cSockets = _cSockets; 
        pipe = _pipe;
        es = Executors.newFixedThreadPool(cSockets.size());
        listeners = new AsyncListen[cSockets.size()];
        for(int i = 0; i < cSockets.size(); i++){
            listeners[i] = new AsyncListen(cSockets.get(i), pipe);
        }
    }

    /**
     * Starts the handling threads.
     */
    public void startHandler(){
        for (AsyncListen ft : listeners) {
            es.submit(ft);
        }
    }
    /**
     * Stops the handling threads. Note the handler cannot be restarted once stopped.
     */
    public void stopHandler(){
        for (AsyncListen ft : listeners) {
            ft.cancel();
        }
        es.shutdown();
    }

    // public static byte[] intToByteArr(int n){
    //     return ByteBuffer.allocate(4).putInt(n).array();
    // }
    // public static int byteArrToInt(byte[] bs){
    //     return ByteBuffer.wrap(bs).getInt();
    // }
}
