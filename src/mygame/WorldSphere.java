/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Guo Jing Wu
 */
public class WorldSphere extends Node {

    public static Material mat1, mat2, mat3, mat4, mat5;
    static Geometry ball1, ball2, ball3, ball4, ball5;
    public Node sphere;

    protected WorldSphere(SimpleApplication sa) {
        initMaterial(sa);
        initGeometry();

        sphere = new Node();
        sphere.setShadowMode(RenderQueue.ShadowMode.Receive);
        sphere.attachChild(ball1);
        sphere.attachChild(ball2);
        sphere.attachChild(ball3);
        sphere.attachChild(ball4);
        sphere.attachChild(ball5);
        sa.getRootNode().attachChild(sphere);
    }

    private void initGeometry() {
        Quaternion roll90 = new Quaternion();
        roll90.fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));

        Sphere largeSphere = new Sphere(64, 64, 2);
        largeSphere.setTextureMode(Sphere.TextureMode.Projected);
        
        ball1 = new Geometry("Ball", largeSphere);
        ball1.setLocalRotation(roll90);
        ball1.setMaterial(mat1);
        ball1.setLocalTranslation(-10f, 5f, 0f);
        
        ball2 = new Geometry("Ball", largeSphere);
        ball2.setLocalRotation(roll90);
        ball2.setMaterial(mat2);
        ball2.setLocalTranslation(-5f, 5f, 0f);
        
        ball3 = new Geometry("Ball", largeSphere);
        ball3.setLocalRotation(roll90);
        ball3.setMaterial(mat3);
        ball3.setLocalTranslation(0f, 5f, 0f);
        
        ball4 = new Geometry("Ball", largeSphere);
        ball4.setLocalRotation(roll90);
        ball4.setMaterial(mat4);
        ball4.setLocalTranslation(5f, 5f, 0f);
        
        ball5 = new Geometry("Ball", largeSphere);
        ball5.setLocalRotation(roll90);
        ball5.setMaterial(mat5);
        ball5.setLocalTranslation(10f, 5f, 0f);
    }

    private void initMaterial(SimpleApplication sa) {
        mat1 = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Earth.jpg"));
        
        mat2 = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Arnessk.png"));
        
        mat3 = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Klendathu.png"));
        
        mat4 = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat4.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Reststop.png"));
        
        mat5 = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat5.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Thunorrad.jpg"));
    }
}
