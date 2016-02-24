/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import server.FieldData;

/**
 *
 * @author Rolf
 */
@Serializable
public class ClientPlayfield {

    SimpleApplication sa;
    Geometry sg;
    Node node;

    public ClientPlayfield() {
    }

    public ClientPlayfield(SimpleApplication sa) {
        this.sa = sa;
    }

    public void addSphere(FieldData fd) {
        Sphere s = new Sphere(32, 32, 1);
        sg = new Geometry("", s);
        Material mat = new Material(sa.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", fd.color);
        mat.setColor("Diffuse", ColorRGBA.Orange);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f); // shininess from 1-128
        sg.setMaterial(mat);
        node = new Node();
        node.attachChild(sg);
        node.setLocalTranslation(fd.x, fd.y, fd.z);
        sa.getRootNode().attachChild(node);
    }
}
