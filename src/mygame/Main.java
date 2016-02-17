package mygame;

import com.jme3.app.SimpleApplication;
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
    WorldSphere worldSphere;

    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initCam();
        worldSphere = new WorldSphere(this);
        worldSphere.sphere.addControl(new Main.SphereControl());
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Stars.dds", false);
        rootNode.attachChild(sky);
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

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    class SphereControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            spatial.rotate(0, tpf / 4, 0);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private void initCam() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(25);
        cam.setLocation(new Vector3f(0f, 15f, 15f));
        cam.lookAt(new Vector3f(0, 5f, 0), Vector3f.UNIT_Y);
    }
}
