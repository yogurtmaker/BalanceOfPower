package server;

import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.*;


public class ServerNetworkHandler extends TimerTask implements MessageListener, ConnectionListener {

    public static int SERVERPORT = 6143;
    static ReentrantLock lock = new ReentrantLock();
    Server server;
    ServerNetworkListener gameServer;

    // -------------------------------------------------------------------------
    public ServerNetworkHandler(GameServer l) {
        gameServer = l;         
           Timer timer = new Timer();
           timer.schedule(this, 1000, 1000);
        try {
            server = Network.createServer(SERVERPORT);
            Registration.registerMessages();
            server.addMessageListener(this);
            server.addConnectionListener(this);
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerNetworkHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // -------------------------------------------------------------------------
    public void messageReceived(Object source, Message msg) {
        //System.out.println("Received: " + (StringData)msg);
        List<Double> energyList= gameServer.messageReceived(msg);
        EnergyMessage eMsg = new EnergyMessage(energyList);
        broadcast(msg);   
         broadcast(eMsg);   
        
    }
   
    //timertask method which used for update the energy list
    @Override
    public void run() {
         List<Double> energyList = gameServer.getEnergyList();
      EnergyMessage eMsg = new EnergyMessage(energyList);
      if(energyList.size()>0){   
       // System.out.println("energy:"+energyList.toString());
         broadcast(eMsg); 
      }
    }

    // -------------------------------------------------------------------------
    public void connectionAdded(Server server, HostedConnection conn) {
        int connID = conn.getId();
        System.out.println("Client " + connID + " connected");
        Message m;
        try {
            // gameServer.newConnectionReceived throws an Exception
            // if the connection should not be accepted.
            m = gameServer.newConnectionReceived(connID);
            // if all is ok, broadcast gameServer-created message
            // this is usually an InitialClientMessage
            // (which is just not hard coded here to keep it customizable).
            broadcast(m);
        } catch (Exception e) {
            // Connection not accepted.
            GameFull gf = new GameFull();
            broadcast(gf);
            System.out.println("Connection not accepted. Kicking out client. TODO!!!" + connID);
            
        }
    }

    // -------------------------------------------------------------------------
    public void sendToClient(int ID, Message msg) {
        try {
            HostedConnection hc = server.getConnection(ID);
            server.broadcast(Filters.in(hc), msg);
        } catch (Exception e) {
            System.out.println("ERROR in sendToClient()");
        }
    }

    // -------------------------------------------------------------------------
    public void broadcast(Message m) {
        server.broadcast(m);
    }

    // -------------------------------------------------------------------------
    public void connectionRemoved(Server server, HostedConnection conn) {
        // TODO
    }

  
}
