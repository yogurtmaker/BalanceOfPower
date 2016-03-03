/*
 * The Game Server contains the game logic
 */
package server;

import client.Planet;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.network.Message;
import com.jme3.system.JmeContext;
import java.util.ArrayList;
import java.util.List;
import messages.NewClientMessage;
import messages.*;

/**
 *
 * @author Rolf
 */
public class GameServer extends SimpleApplication implements ServerNetworkListener {

    ServerNetworkHandler networkHandler;
    PlayField playfield;
    int i = 0;
   public List<Planet> planetList;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Starting Game Server at port " + ServerNetworkHandler.SERVERPORT);
        GameServer app = new GameServer();
        app.start(JmeContext.Type.Headless);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

     @Override
    public void simpleInitApp() {
        planetList = new ArrayList<Planet>();
        networkHandler = new ServerNetworkHandler(this);
        playfield = new PlayField();
    }
    // -------------------------------------------------------------------------
//    public GameServer() {
//      
//        
//    }

    // -------------------------------------------------------------------------
    // Methods required by ServerNetworkHandler
    public List<Double> messageReceived(Message msg) {
        if (msg instanceof ClientUpdateMessage) {
               List<Double> energyList = new ArrayList();
            ClientUpdateMessage sd = (ClientUpdateMessage) msg;
            System.out.println("Sever received:" + sd.messageTypes.name()
                    + " SourceId:"+sd.sourceId +" TargetId:"+sd.targetId );
            if(sd.messageTypes.equals(MessageTypes.absord)){
            planetList.get(sd.sourceId).absorb( planetList.get(sd.targetId));
          }
            if(sd.messageTypes.equals(MessageTypes.attack)){
            planetList.get(sd.sourceId).attack( planetList.get(sd.targetId));
          }
            if(sd.messageTypes.equals(MessageTypes.donation)){
            planetList.get(sd.sourceId).donation( planetList.get(sd.targetId));
          }
            if(sd.messageTypes.equals(MessageTypes.infusion)){
            planetList.get(sd.sourceId).infusion( planetList.get(sd.targetId));
          }
     
            for(Planet planet:planetList){
            energyList.add(planet.getEnergy());
            }
            return energyList;
        }
        
        return null;
    }

    // -------------------------------------------------------------------------
    public synchronized Message newConnectionReceived(int connectionID) throws Exception {
        // put player on random playfield
        boolean ok = playfield.addElement(connectionID);
        if (i >= 5)
            ok = false;
        if (!ok) {
            throw new Exception("Max number of players exceeded.");
        }
        Material  mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Earth.jpg"));
        Planet planet = new Planet(mat, connectionID);
        planetList.add(planet);
        rootNode.attachChild(planet);
        // send entire playfield to new client
        NewClientMessage iniCM = new NewClientMessage(connectionID, playfield.data);
         i++;
        return (iniCM);
    }

    public List<Double> getEnergyList() {
      List<Double> energyList = new ArrayList();
        for(Planet planet: planetList){
        energyList.add(planet.getEnergy());
        }
      return energyList;
    }

   
}
