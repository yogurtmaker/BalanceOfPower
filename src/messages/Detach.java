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

    // -------------------------------------------------------------------------
    public Detach() {
    }

    // -------------------------------------------------------------------------
    public Detach(String signal) {
        super();
        this.signal = signal;
    }
}
