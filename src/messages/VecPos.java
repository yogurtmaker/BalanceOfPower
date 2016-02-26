/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import server.FieldData;

/**
 *
 * @author alien
 */
@Serializable
public class VecPos extends AbstractMessage {

    public Vector3f vecSou;
    public Vector3f vecTar;

    // -------------------------------------------------------------------------
    public VecPos() {
    }

    // -------------------------------------------------------------------------
    public VecPos(Vector3f vecSou, Vector3f vecTar) {
        super();
        this.vecSou = vecSou;
        this.vecTar = vecTar;
    }
}
