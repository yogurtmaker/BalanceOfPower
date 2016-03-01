/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Rolf
 */
public class PlayField {

    public final static float MINMAX = 20f;
    public final static float RADIUS = 1f;
    public List<Vector3f> plantetPosition;
    public LinkedList<FieldData> data;

    // -------------------------------------------------------------------------
    public PlayField() {
        data = new LinkedList<FieldData>();
        plantetPosition = new ArrayList();
         plantetPosition.add(new Vector3f(-1, -5, 8));
        plantetPosition.add(new Vector3f(19, 10, 10));
        plantetPosition.add(new Vector3f(-8, 5, 3));
        plantetPosition.add(new Vector3f(10, -3, 15));
        plantetPosition.add(new Vector3f(6, 16, 3));
       
      
    }

    // -------------------------------------------------------------------------
    public boolean addElement(int id) {
        Random rand = new Random();
//        float x = rand.nextFloat() * 2 * MINMAX - MINMAX;
//        float y = rand.nextFloat() * 2 * MINMAX - MINMAX;
//        float z = rand.nextFloat() * 2 * MINMAX - MINMAX;
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        ColorRGBA c = new ColorRGBA(r, g, b, 1.0f);
        Vector3f pos = plantetPosition.get(id);
       // FieldData newData = new FieldData(id, x, y, z, c);
         FieldData newData = new FieldData(id, pos.x, pos.y, pos.z, c);
        data.addLast(newData);
        //
        // here we could add a test for max. number of players reached.
        // (TODO)
        return (true);
    }
}
