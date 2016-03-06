/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class GameFull extends AbstractMessage {

    public String type;

    public GameFull() {
    }

    public GameFull(String type) {
        this.type = type;
    }
}