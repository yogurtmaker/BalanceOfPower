/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author alien
 */
@Serializable
public class Detach extends AbstractMessage {

    public String signal;
    public int ID;

    // -------------------------------------------------------------------------
    public Detach() {
    }

    // -------------------------------------------------------------------------
    public Detach(String signal, int ID) {
        super();
        this.ID = ID;
        this.signal = signal;
    }
}
