package client;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Message;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import java.awt.Dimension;
import java.awt.Toolkit;
import messages.Detach;
import messages.NewClientMessage;
import messages.StringData;
import messages.VecPos;
import server.FieldData;

public class GameClient extends SimpleApplication implements ClientNetworkListener, ActionListener {
    //

    private int ID = -1;
    protected ClientNetworkHandler networkHandler;
    private ClientPlayfield playfield;
    private ClientPlayfield inPlayfield;
    private ClientPlayfield playfield1;
    FieldData fda;
    Material mats[], arrmat;
    Planet[] planets;
    GameClient main;
    AppStateManager asm;
    int i = 0;
    Vector3f hitVector;
    Geometry arrow, arrow1;

    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Starting Client");
        //
        AppSettings aps = getAppSettings();
        //
        GameClient app = new GameClient();
        app.setShowSettings(false);
        app.setSettings(aps);
        app.start();
    }

    // -------------------------------------------------------------------------
    public GameClient() {
        // this constructor has no fly cam!
        super(new StatsAppState(), new DebugKeysAppState());
    }

    // -------------------------------------------------------------------------
    @Override
    public void simpleInitApp() {
        initCam();
        setPauseOnLostFocus(false);
        //
        // CONNECT TO SERVER!
        networkHandler = new ClientNetworkHandler(this);
        //initMaterial();
        //
        initGui();
        initLightandShadow();
        initPostProcessing();
        initKeys();

    }

    // -------------------------------------------------------------------------
    public void SimpleUpdate(float tpf) {
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
    private static AppSettings getAppSettings() {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        return (aps);
    }

    // -------------------------------------------------------------------------
    private void initGui() {
        setDisplayFps(true);
        setDisplayStatView(false);
    }

    // -------------------------------------------------------------------------
    private void initLightandShadow() {
        // Light1: white, directional
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.7f, -1.3f, -0.9f)).normalizeLocal());
        sun.setColor(ColorRGBA.Gray);
        getRootNode().addLight(sun);

        // Light 2: Ambient, gray
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        getRootNode().addLight(ambient);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 1);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }

    // -------------------------------------------------------------------------
    private void initPostProcessing() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setBlurScale(2.0f);
        bloom.setBloomIntensity(2.0f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Stars.dds", false);
        rootNode.attachChild(sky);
    }

    // -------------------------------------------------------------------------
    private void initCam() {
        //flyCam.setEnabled(true);
        cam.setLocation(new Vector3f(3f, 15f, 50f));
        cam.lookAt(new Vector3f(0, 0, 3), Vector3f.UNIT_Y);
    }

    // -------------------------------------------------------------------------
    // This client received its InitialClientMessage.
    private void initGame(NewClientMessage msg) {
        System.out.println("Received initial message from server. Initializing playfield.");
        //
        // store ID
        this.ID = msg.ID;
        initMaterial();
        System.out.println("My ID: " + this.ID);
        playfield = new ClientPlayfield(this);
        playfield1 = new ClientPlayfield(this);
        inPlayfield = new ClientPlayfield(this);
        fda = msg.field.getLast();
        planets = new Planet[5];
        for (FieldData fd : msg.field) {
            planets[i] = new Planet(mats[i], this);
            planets[i].geom.setLocalTranslation(fd.x, fd.y, fd.z);
            getRootNode().attachChild(planets[i]);
            i++;
            fda = fd;
        }
    }

    // -------------------------------------------------------------------------
    // Keyboard input
    private void initKeys() {
        planets = new Planet[5];

        /* for (int i = 0; i < 5; i++) {
         planets[i] = new Planet(mats[i], );
         planets[i].geom.setLocalTranslation(-10 + 5 * i, 5f, 0f);
         //rootNode.attachChild(planets[i]);
         System.out.println("print: " + i);
         }
         */
        inputManager.addMapping("absorb", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("infusion", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("donation", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addListener(this, "absorb", "attack", "infusion", "donation");
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Click");

    }

    // key action
    public void onAction(String name, boolean isPressed, float tpf) {

        if (name.equals("absorb")) {
            planets[0].absorb(planets[1]);
        } else if (name.equals("attack") && isPressed) {
            planets[0].attack(planets[1]);
        } else if (name.equals("infusion")) {
            planets[0].infusion(planets[1]);
        } else if (name.equals("donation") && isPressed) {
            planets[0].donation(planets[1]);
        }
        if ("Click".equals(name) && isPressed) {
            CollisionResults results = new CollisionResults();
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            Ray ray = new Ray(click3d, dir);
            for (int j = 0; j < i; j++) {
                if (ID != j) {
                    planets[j].collideWith(ray, results);
                    float minDist = Float.MAX_VALUE;
                    if (hitVector != null) {
                        hitVector = null;
                        mats[ID].setColor("GlowColor", ColorRGBA.Black);
                        //rootNode.detachChild(arrow);
                        Detach deMsg = new Detach("detach", ID);
                        networkHandler.send(deMsg);
                    }
                    if (results.size() > 0) {
                        float[] xyz = planets[ID].getVec();
                        String target = results.getCollision(0).getGeometry().getName();
                        if (target.equals("Ball")) {
                            Vector3f pt = results.getClosestCollision().getGeometry().getWorldTranslation();
                            float dist = results.getCollision(0).getDistance();
                            if (dist < minDist) {
                                //float[] xyz = getVec();
                                Vector3f gp = new Vector3f(xyz[0], xyz[1], xyz[2]);
                                Vector3f gp1 = new Vector3f(pt.x, pt.y, pt.z);
                                gp1.subtractLocal(gp);
                                //gp.normalizeLocal();
                                hitVector = pt;
                                mats[ID].setColor("GlowColor", ColorRGBA.Pink);
                                /*Arrow line = new Arrow(gp1);
                                line.setLineWidth(4);
                                arrow = new Geometry("Arrow", line);
                                arrmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                arrmat.setColor("Color", ColorRGBA.Gray);
                                arrow.setMaterial(arrmat);*/
                                //arrow.setLocalTranslation(planets[ID].geom.getWorldTranslation());
                                VecPos vecMsg = new VecPos(planets[ID].geom.getWorldTranslation(), gp1, ID);
                                networkHandler.send(vecMsg);
                                //getRootNode().attachChild(arrow);
                            }
                        }
                    }
                }
            }

        }
    }

    private void initMaterial() {
        mats = new Material[6];
        mats[0] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[0].setTexture("ColorMap", assetManager.loadTexture("Textures/Earth.jpg"));

        mats[1] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[1].setTexture("ColorMap", assetManager.loadTexture("Textures/Arnessk.png"));

        mats[2] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[2].setTexture("ColorMap", assetManager.loadTexture("Textures/Klendathu.png"));

        mats[3] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[3].setTexture("ColorMap", assetManager.loadTexture("Textures/Reststop.png"));

        mats[4] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[4].setTexture("ColorMap", assetManager.loadTexture("Textures/Thunorrad.jpg"));
    }
    // -------------------------------------------------------------------------
    // message received

    public void messageReceived(Message msg) {
        if (msg instanceof NewClientMessage) {
            NewClientMessage ncm = (NewClientMessage) msg;
            if (this.ID == -1) {
                initGame(ncm);
            } else {
                planets[i] = new Planet(mats[i], this);
                FieldData tempfd = ncm.field.getLast();
                planets[i].geom.setLocalTranslation(tempfd.x, tempfd.y, tempfd.z);            
                getRootNode().attachChild(planets[i]);
                i++;
                //inPlayfield.addSphere(ncm.field.getLast());
            }
        }
        if (msg instanceof StringData) {
            StringData sd = (StringData) msg;
            System.out.println("Client received " + sd.key);
            playfield1.addSphere(sd.field);
            //new SingleBurstParticleEmitter(this, playfield1.node, Vector3f.ZERO);
        }
        if (msg instanceof VecPos) {
            VecPos vecMsg = (VecPos) msg;
            Arrow line = new Arrow(vecMsg.vecTar);
            line.setLineWidth(4);
            System.out.println("123123");
            arrow1 = new Geometry("Arrow", line);
            arrmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            arrmat.setColor("Color", ColorRGBA.Gray);
            arrow1.setMaterial(arrmat);
            arrow1.setLocalTranslation(vecMsg.vecSou);         
            planets[vecMsg.ID].attachChild(arrow1);
         
            //new SingleBurstParticleEmitter(this, playfield1.node, Vector3f.ZERO);
        }
        if (msg instanceof Detach) {
            Detach deMsg = (Detach) msg;
           arrow1.removeFromParent();
            //arrow1.setLocalScale(0);
            System.out.println("123");
        }
    }
}
