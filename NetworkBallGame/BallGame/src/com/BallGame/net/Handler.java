package com.BallGame.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.net.Socket;

public class Handler{
    private List<Integer> pipe;
    private List<Socket> cSockets;
    private ExecutorService es;
    private ISListen[] listeners;

    /**
     * Constructs threads that listen on all sockets passed into the function, piping the read output into the provided list.
     * @param _cSockets List of sockets to be listened on.
     * @param _pipe List to have the read output piped into.
     * @throws Exception 
     */
    public Handler(List<Socket> _cSockets, List<Integer> _pipe) throws Exception{
        cSockets = _cSockets; 
        pipe = _pipe;
        es = Executors.newFixedThreadPool(cSockets.size());
        listeners = new ISListen[cSockets.size()];
        for(int i = 0; i < cSockets.size(); i++){
            listeners[i] = new ISListen(cSockets.get(i), pipe);
        }
    }

    /**
     * @return Array of listeners.
     */
    public ISListen[] getListeners(){
        return listeners.clone();
    }

    /**
     * Starts the listener threads.
     */
    public void startListen(){
        for (ISListen ft : listeners) {
            es.submit(ft);
        }
    }

    /**
     * Stops the listener threads. Note the threads cannot be restarted once stopped.
     */
    public void stopListen(){
        for (ISListen ft : listeners) {
            ft.stop();
        }
        es.shutdown();
    }
    
    /**
     * Cancels the listener threads. Note the threads cannot be restarted once cancelled.
     */
    public void cancelListen(){
        for (ISListen ft : listeners) {
            ft.stop();
        }
        es.shutdownNow();
    }
}
