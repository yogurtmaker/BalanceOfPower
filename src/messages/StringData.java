/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import client.ClientPlayfield;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import server.FieldData;

/**
 *
 * @author alien
 */
@Serializable
public class StringData extends AbstractMessage {

    public String key;
    public FieldData field;

    // -------------------------------------------------------------------------
    public StringData() {
    }

    // -------------------------------------------------------------------------
    public StringData(String key, FieldData playfield) {
        super();
        this.key = key;
        this.field = playfield;
    }
}
