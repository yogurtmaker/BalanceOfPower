package client;

import com.jme3.app.Application;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.crypto.Data;
import messages.*;
import server.FieldData;

public class GameClient extends SimpleApplication implements ClientNetworkListener, ActionListener {
    //

    private int ID = -1;
    private int targetID = -1;
    private final int planetRad = 2;
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
      static ReentrantLock lock = new ReentrantLock();
      

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
            planets[i] = new Planet(mats[i],i);
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
        if (targetID != -1) {
            MessageTypes type = null;
            if (name.equals("absorb")) {
                type = MessageTypes.absord;
            } else if (name.equals("attack")&& isPressed) {
                type = MessageTypes.attack;
            } else if (name.equals("infusion")) {
                type = MessageTypes.infusion;
            } else if (name.equals("donation")&& isPressed) {
                type = MessageTypes.donation;
            }
            if (type != null) {
                ClientUpdateMessage atMsg = new ClientUpdateMessage(ID, targetID, type);
                networkHandler.send(atMsg);
            }
        }

        
        if ("Click".equals(name) && isPressed) {
            boolean sendMessage = false;
            CollisionResults results = new CollisionResults();
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            Ray ray = new Ray(click3d, dir);
                    rootNode.collideWith(ray, results);
                    if (results.size() > 0) {
                        String target = results.getClosestCollision().getGeometry().getName();
                        if (target.startsWith("Ball")) {
                           int tempID = Integer.valueOf(target.split(" ")[1]);
                            if (tempID!=ID) { 
                                targetID = tempID;
                                ClientUpdateMessage atMsg = new ClientUpdateMessage(ID, tempID, MessageTypes.attachArrow);
                                networkHandler.send(atMsg); 
                                sendMessage = true;
                            }
                        }
                    }
                    if(!sendMessage &&  ID!=-1){
                        //  mats[ID].setColor("GlowColor", ColorRGBA.Black);
                        targetID = -1;
                        ClientUpdateMessage deMsg = new ClientUpdateMessage(ID, -1, MessageTypes.detachArrow);
                        networkHandler.send(deMsg);
                    }
//                }
//            }

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
    public   void messageReceived(Message msg){
       final GameClient app;
        app = this;
        final Message message = msg;
    app.enqueue(new Callable(){
        public Object call() throws Exception {
       app.ClientUpdate(message);
         return null;
   
        }
    });
    }
   
    
    public   void ClientUpdate(Message msg){
            if (msg instanceof NewClientMessage) {
            NewClientMessage ncm = (NewClientMessage) msg;
            if (this.ID == -1) {
                initGame(ncm);
            } else {
                planets[i] = new Planet(mats[i],ncm.ID);
                FieldData tempfd = ncm.field.getLast();
                planets[i].geom.setLocalTranslation(tempfd.x, tempfd.y, tempfd.z);            
                getRootNode().attachChild(planets[i]);
                inPlayfield.addSphere(tempfd);
                i++;
                //inPlayfield.addSphere(ncm.field.getLast());
            }
        }
      else  if (msg instanceof ClientUpdateMessage) {
            ClientUpdateMessage message = (ClientUpdateMessage) msg;
            MessageTypes messageTypes = message.messageTypes; 
           System.out.println("Client received: " + message.messageTypes.name()
                    + " SourceId:"+message.sourceId +" TargetId:"+message.targetId );
           if (messageTypes.equals(MessageTypes.attachArrow)) {
             Vector3f tVector = planets[message.targetId].geom.getWorldTranslation();
             Vector3f sVector = planets[message.sourceId].geom.getWorldTranslation();
             Vector3f dirVector = tVector.subtract(sVector);
            Arrow line = new Arrow(dirVector.subtract(dirVector.normalize().mult(1.2f)));
            line.setLineWidth(4);
            arrow1 = new Geometry("Arrow", line);
            arrmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            arrmat.setColor("Color", ColorRGBA.Gray);
            arrow1.setMaterial(arrmat);
            arrow1.setLocalTranslation(sVector);        
             planets[message.sourceId].arrowNode.detachAllChildren();
            planets[message.sourceId].arrowNode.attachChild(arrow1);        
        }
       else if (messageTypes.equals(MessageTypes.detachArrow)) {
            planets[message.sourceId].arrowNode.detachAllChildren();
        }
    
    }
    }
}
