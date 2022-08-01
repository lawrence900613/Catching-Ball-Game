package com.BallGame.net;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class AsyncListen implements Runnable {
    List<Integer> pipe;
    InputStream is;
    Socket s;
    Boolean isCancelled = false;
    Boolean errorOccurred = false;
    
    public AsyncListen(Socket _s, List<Integer> _pipe) throws Exception{
        s = _s;
        is = _s.getInputStream();
        pipe = _pipe;
    }
    
    public void cancel(){ isCancelled = true; }

    @Override
    public void run(){
        try {
            while(!isCancelled && !s.isInputShutdown()){
                int p = is.read();
                synchronized (this) {
                    pipe.add(p);
                }
            }
        } catch (Exception e) {
            errorOccurred = true;
            e.printStackTrace();
        }
        
    }
}
