/*
 * The Game Server contains the game logic
 */
package server;

import com.jme3.network.Message;
import messages.NewClientMessage;
import messages.StringData;

/**
 *
 * @author Rolf
 */
public class GameServer implements ServerNetworkListener {

    ServerNetworkHandler networkHandler;
    PlayField playfield;
    int i = 0;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Starting Game Server at port " + ServerNetworkHandler.SERVERPORT);
        GameServer gs = new GameServer();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    // -------------------------------------------------------------------------
    public GameServer() {
        networkHandler = new ServerNetworkHandler(this);
        playfield = new PlayField();
    }

    // -------------------------------------------------------------------------
    // Methods required by ServerNetworkHandler
    public void messageReceived(Message msg) {
        if (msg instanceof StringData) {
            StringData sd = (StringData) msg;
            System.out.println("Sever received " + sd.key);
        }
    }

    // -------------------------------------------------------------------------
    public Message newConnectionReceived(int connectionID) throws Exception {
        // put player on random playfield
        boolean ok = playfield.addElement(connectionID);
        if (i >= 5)
            ok = false;
        if (!ok) {
            throw new Exception("Max number of players exceeded.");
        }
        // send entire playfield to new client
        NewClientMessage iniCM = new NewClientMessage(connectionID, playfield.data);
        i++;
        return (iniCM);
    }
}
