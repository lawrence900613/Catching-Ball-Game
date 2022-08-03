package com.BallGame.net;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class ISListen implements Runnable {
    private List<Integer> pipe;
    private InputStream is;
    private Socket s;
    private Boolean silent = false;
    private Boolean stopped = false;
    private Boolean exceptionOccurred = false;
    
    /** 
     * Dictates the time that an instance can be blocked on reading the inputstream of socket passed during construction.
     */
    private static final int TICK_TIME = 33;

    /**
     * Constructs an ISListen instance which asynchronously reads incoming data into the provided pipe.
     * @param _s Socket of whose inputstream is to be read asynchronously.
     * @param _pipe List where read data will be stored in.
     * @throws Exception 
     */
    public ISListen(Socket _s, List<Integer> _pipe) throws Exception{
        s = _s;
        is = _s.getInputStream();
        pipe = _pipe;
        s.setSoTimeout(TICK_TIME);
    }

    public void silence(){ silent = true; }
    public void unsilence(){ silent = false; }
    public void stop(){ stopped = true; }
    public Boolean exceptionStatus(){ return exceptionOccurred; }

    @Override
    public void run(){
        while(!stopped && !s.isInputShutdown()){
            try {
                byte[] p = new byte[4];
                is.read(p, 0, 4);
                synchronized (pipe) {
                    pipe.add(network.byteArrToInt(p));
                }
            } catch (SocketTimeoutException e) {
                continue;
            } catch (Exception e){
                if(!silent)
                    e.printStackTrace();
                exceptionOccurred = true;
                break;
            }
        }
    }
}
