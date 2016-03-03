
package server;

import com.jme3.network.Message;
import java.util.List;


public interface ServerNetworkListener {
    public List<Double> messageReceived(Message msg);
    public Message newConnectionReceived(int connectionID) throws Exception;
    public List<Double> getEnergyList();
}
