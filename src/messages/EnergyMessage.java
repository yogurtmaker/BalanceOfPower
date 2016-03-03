/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.List;

@Serializable
public class EnergyMessage  extends AbstractMessage {
    public List<Double> energyList;
    
     public EnergyMessage(){
     }
     
     
    public EnergyMessage(List<Double> energyList){
    this.energyList = energyList;
    }
}