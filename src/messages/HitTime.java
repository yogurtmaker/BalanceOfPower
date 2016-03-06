/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class HitTime extends AbstractMessage {

    public int sourceId;
    public int targetId;
    public String type;

    public HitTime() {
    }

    public HitTime(int sourceId, int targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }
}