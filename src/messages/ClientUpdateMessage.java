/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author 2
 */
@Serializable
public class ClientUpdateMessage extends AbstractMessage {

    public int sourceId;
    public int targetId;
    public MessageTypes messageTypes;
    public boolean isPressed;

    public ClientUpdateMessage() {
    }

    public ClientUpdateMessage(int sourceId, int targetId, MessageTypes messageTypes, boolean isPressed) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.messageTypes = messageTypes;
        this.isPressed = isPressed;
    }
}
