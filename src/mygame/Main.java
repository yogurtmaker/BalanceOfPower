package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    static Dimension screen;
    Material[] mats;

    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initCam();
       initMaterial();
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Stars.dds", false);
        rootNode.attachChild(sky);
        Game game = new Game();
        stateManager.attach(game);
    }

    private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    
    private void initMaterial() {
        mats = new Material[5];
        mats[0] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mats[0] .setTexture("ColorMap", assetManager.loadTexture("Textures/Earth.jpg"));
        
         mats[1]  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mats[1] .setTexture("ColorMap",assetManager.loadTexture("Textures/Arnessk.png"));
        
         mats[2]  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mats[2] .setTexture("ColorMap", assetManager.loadTexture("Textures/Klendathu.png"));
        
         mats[3]  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mats[3] .setTexture("ColorMap", assetManager.loadTexture("Textures/Reststop.png"));
        
         mats[4]  = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mats[4] .setTexture("ColorMap", assetManager.loadTexture("Textures/Thunorrad.jpg"));
    }
    
    
    
    
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

  

    private void initCam() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(25);
        cam.setLocation(new Vector3f(0f, 15f, 15f));
        cam.lookAt(new Vector3f(0, 5f, 0), Vector3f.UNIT_Y);
    }
}
