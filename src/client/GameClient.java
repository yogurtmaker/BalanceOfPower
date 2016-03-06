package client;

import com.jme3.app.Application;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    BitmapText text, text1, text2, text3, text4, texts[], texts1[];
    AppStateManager asm;
    int i = 0;
    Vector3f hitVector, ps[], ps1[];
    Geometry arrow, arrow1;
    static ReentrantLock lock = new ReentrantLock();
    Date date;
    DateFormat format;
    String passTime, time1;
    boolean ft, gf = false;

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
        initText();

        ps = new Vector3f[5];
        ps[0] = new Vector3f(650, 335, 0);
        ps[1] = new Vector3f(1150, 655, 0);
        ps[2] = new Vector3f(520, 565, 0);
        ps[3] = new Vector3f(910, 315, 0);
        ps[4] = new Vector3f(800, 810, 0);

        ps1 = new Vector3f[5];
        ps1[0] = new Vector3f(620, 235, 0);
        ps1[1] = new Vector3f(1120, 535, 0);
        ps1[2] = new Vector3f(490, 465, 0);
        ps1[3] = new Vector3f(880, 185, 0);
        ps1[4] = new Vector3f(770, 695, 0);

    }

    // -------------------------------------------------------------------------
    public void SimpleUpdate(float tpf) {
        System.out.println(time1);
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
            texts[i].setLocalTranslation(ps[i]);
            texts[i].setText("Enemy!(" + (i + 1) + ")");
            texts[i].setColor(ColorRGBA.Red);
            planets[i] = new Planet(mats[i], i);
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

        inputManager.addMapping("absorb", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("infusion", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("donation", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, "absorb", "attack", "infusion", "donation");
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Click");

    }

    // key action
    public void onAction(String name, boolean isPressed, float tpf) {
        if (i > 4) {
            if (targetID != -1) {
                MessageTypes type = null;
                if (name.equals("absorb")) {
                    type = MessageTypes.absorb;
                } else if (name.equals("attack") && isPressed) {
                    type = MessageTypes.attack;
                    new SingleBurstParticleEmitter(this, rootNode, planets[targetID].geom.getWorldTranslation());
                    time1 = getTime();
                    ft = true;
                } else if (name.equals("infusion")) {
                    type = MessageTypes.infusion;
                } else if (name.equals("donation") && isPressed) {
                    type = MessageTypes.donation;
                    new FlowerBurst(this, rootNode, planets[targetID].geom.getWorldTranslation());
                    time1 = getTime();
                    ft = true;
                }
                if (type != null) {
                    ClientUpdateMessage atMsg = new ClientUpdateMessage(ID, targetID, type, isPressed);
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
                        if (tempID != ID) {
                            targetID = tempID;
                            ClientUpdateMessage atMsg = new ClientUpdateMessage(ID, tempID, MessageTypes.attachArrow, isPressed);
                            networkHandler.send(atMsg);
                            sendMessage = true;
                        }
                    }
                }
                if (!sendMessage && ID != -1) {
                    //  mats[ID].setColor("GlowColor", ColorRGBA.Black);
                    targetID = -1;
                    ClientUpdateMessage deMsg = new ClientUpdateMessage(ID, -1, MessageTypes.detachArrow, isPressed);
                    networkHandler.send(deMsg);
                }
//                }
//            }
            }
        }
    }

    private void initMaterial() {
        mats = new Material[6];
        mats[0] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[0].setTexture("ColorMap", assetManager.loadTexture("Textures/Earth.jpg"));
        mats[0].setColor("Color", ColorRGBA.White);
        mats[1] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[1].setTexture("ColorMap", assetManager.loadTexture("Textures/Arnessk.png"));
        mats[1].setColor("Color", ColorRGBA.White);
        mats[2] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[2].setTexture("ColorMap", assetManager.loadTexture("Textures/Klendathu.png"));
        mats[2].setColor("Color", ColorRGBA.White);
        mats[3] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[3].setTexture("ColorMap", assetManager.loadTexture("Textures/Reststop.png"));
        mats[3].setColor("Color", ColorRGBA.White);
        mats[4] = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats[4].setTexture("ColorMap", assetManager.loadTexture("Textures/Thunorrad.jpg"));
        mats[4].setColor("Color", ColorRGBA.White);
    }
    // -------------------------------------------------------------------------
    // message received

    public void messageReceived(Message msg) {
        final GameClient app;

        app = this;
        final Message message = msg;
        app.enqueue(new Callable() {
            public Object call() throws Exception {
                app.ClientUpdate(message);
                return null;

            }
        });
    }

    public void ClientUpdate(Message msg) {
        if (ft) {
            long time = getPassTime(getTime(), time1);
            if (time >= 1) {
                HitTime ht = new HitTime(ID, targetID);
                networkHandler.send(ht);
                ft = false;
            }
        }
        if (msg instanceof GameFull && i < 2) {
            getGuiNode().attachChild(text);
            AppSettings s = getAppSettings();
            gf = true;
            text.setLocalTranslation(0, 0, 0);
            text1.setLocalTranslation(0, 0, 0);
            text2.setLocalTranslation(0, 0, 0);
            text3.setLocalTranslation(0, 0, 0);
            float lineY = s.getHeight() / 2;
            float lineX = (s.getWidth() - text4.getLineWidth()) / 2;
            text4.setLocalTranslation(lineX, lineY, 0f);

        } else if (msg instanceof NewClientMessage) {
            NewClientMessage ncm = (NewClientMessage) msg;
            if (this.ID == -1) {
                initGame(ncm);
                texts[i - 1].setLocalTranslation(ps[i - 1]);
                texts[i - 1].setText("You!(" + i + ")");
                texts[i - 1].setColor(ColorRGBA.Blue);
            } else {
                planets[i] = new Planet(mats[i], ncm.ID);
                texts[i].setLocalTranslation(ps[i]);
                texts[i].setText("Enemy!(" + (i + 1) + ")");
                texts[i].setColor(ColorRGBA.Red);
                FieldData tempfd = ncm.field.getLast();
                planets[i].geom.setLocalTranslation(tempfd.x, tempfd.y, tempfd.z);
                getRootNode().attachChild(planets[i]);
                inPlayfield.addSphere(tempfd);
                i++;
                //inPlayfield.addSphere(ncm.field.getLast());
            }
            if (i > 4) {
                text3.setLocalTranslation(0, 0, 0);
            }
        } else if (msg instanceof ClientUpdateMessage) {
            ClientUpdateMessage message = (ClientUpdateMessage) msg;
            MessageTypes messageTypes = message.messageTypes;
            System.out.println("Client received: " + message.messageTypes.name()
                    + " SourceId:" + message.sourceId + " TargetId:" + message.targetId);
            if (messageTypes.equals(MessageTypes.detachArrow)) {
                planets[message.sourceId].arrowNode.detachAllChildren();
            } else {
                ColorRGBA arrowColor = ColorRGBA.Gray;
                if (messageTypes.equals(MessageTypes.absorb) && message.isPressed) {
                    arrowColor = ColorRGBA.Red;
                }
                if (messageTypes.equals(MessageTypes.infusion) && message.isPressed) {
                    arrowColor = ColorRGBA.Green;
                }
                if (messageTypes.equals(MessageTypes.attack)) {
                    arrowColor = ColorRGBA.Red;
                }
                if (messageTypes.equals(MessageTypes.donation)) {
                    arrowColor = ColorRGBA.Green;
                }

                Vector3f tVector = planets[message.targetId].geom.getWorldTranslation();
                Vector3f sVector = planets[message.sourceId].geom.getWorldTranslation();
                Vector3f dirVector = tVector.subtract(sVector);
                Arrow line = new Arrow(dirVector.subtract(dirVector.normalize().mult(1.2f)));
                line.setLineWidth(4);
                arrow1 = new Geometry("Arrow", line);
                arrmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                arrmat.setColor("Color", arrowColor);
                arrow1.setMaterial(arrmat);
                arrow1.setLocalTranslation(sVector);
                planets[message.sourceId].arrowNode.detachAllChildren();
                planets[message.sourceId].arrowNode.attachChild(arrow1);
            }
        } else if (msg instanceof EnergyMessage && !gf) {
            EnergyMessage eMsg = (EnergyMessage) msg;
            int n = 0;
            if (eMsg.energyList != null) {
                for (Double energy : eMsg.energyList) {
                    String t = "";
                    planets[n].setEnergy(energy);
                    float a = (float) (energy / 100 + .5);
                    mats[n].setColor("Color", new ColorRGBA(a, a, a, 1));
                    planets[n].geom.setMaterial(mats[n]);
                    texts1[n].setLocalTranslation(ps1[n]);
                    texts1[n].setColor(ColorRGBA.Green);
                    t = String.format("Energy: %3.1f\t", planets[n].getEnergy());
                    texts1[n].setText(t);
                    n++;
                }
            }
        } else if (msg instanceof HitTime) {
            HitTime ht = (HitTime) msg;
            if (ht.targetId != -1) {
                ColorRGBA arrowColor = ColorRGBA.Gray;
                Vector3f tVector = planets[ht.targetId].geom.getWorldTranslation();
                Vector3f sVector = planets[ht.sourceId].geom.getWorldTranslation();
                Vector3f dirVector = tVector.subtract(sVector);
                Arrow line = new Arrow(dirVector.subtract(dirVector.normalize().mult(1.2f)));
                line.setLineWidth(4);
                arrow1 = new Geometry("Arrow", line);
                arrmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                arrmat.setColor("Color", arrowColor);
                arrow1.setMaterial(arrmat);
                arrow1.setLocalTranslation(sVector);
                planets[ht.sourceId].arrowNode.detachAllChildren();
                planets[ht.sourceId].arrowNode.attachChild(arrow1);
                ft = false;
            }
        }
    }

    public void initText() {
        BitmapFont bmf = assetManager.loadFont("Interface/Fonts/Console.fnt");
        text = new BitmapText(bmf);
        text.setSize(bmf.getCharSet().getRenderedSize() * 2);
        text.setColor(ColorRGBA.Green);
        text.setText("Welcome to the game of BlanceOfPower! ");
        getGuiNode().attachChild(text);
        AppSettings s = getAppSettings();
        float lineY = s.getHeight() / 2 + 400;
        float lineX = (s.getWidth() - text.getLineWidth()) / 2 - 450;
        text.setLocalTranslation(lineX, lineY, 0f);

        text1 = new BitmapText(bmf);
        text1.setSize(bmf.getCharSet().getRenderedSize() * 2);
        text1.setColor(ColorRGBA.Green);
        text1.setText("Press U to absorb. Press I to attack. ");
        getGuiNode().attachChild(text1);
        lineY = s.getHeight() / 2 + 350;
        lineX = (s.getWidth() - text1.getLineWidth()) / 2 - 450;
        text1.setLocalTranslation(lineX, lineY, 0f);

        text2 = new BitmapText(bmf);
        text2.setSize(bmf.getCharSet().getRenderedSize() * 2);
        text2.setColor(ColorRGBA.Green);
        text2.setText("Press J to do infusion. Press K to do donation. ");
        getGuiNode().attachChild(text2);
        lineY = s.getHeight() / 2 + 300;
        lineX = (s.getWidth() - text2.getLineWidth()) / 2 - 380;
        text2.setLocalTranslation(lineX, lineY, 0f);

        text3 = new BitmapText(bmf);
        text3.setSize(bmf.getCharSet().getRenderedSize() * 2);
        text3.setColor(ColorRGBA.Red);
        text3.setText("Wait for 5 players to start game!");
        getGuiNode().attachChild(text3);
        lineY = s.getHeight() / 2;
        lineX = (s.getWidth() - text3.getLineWidth()) / 2;
        text3.setLocalTranslation(lineX, lineY, 0f);

        text4 = new BitmapText(bmf);
        text4.setSize(bmf.getCharSet().getRenderedSize() * 2);
        text4.setColor(ColorRGBA.Red);
        text4.setText("Sorry! Game is full!");
        getGuiNode().attachChild(text4);

        texts = new BitmapText[5];
        texts1 = new BitmapText[5];
        for (int j = 0; j < 5; j++) {
            texts[j] = new BitmapText(bmf);
            texts[j].setSize(bmf.getCharSet().getRenderedSize() * 2);
            getGuiNode().attachChild(texts[j]);
            texts1[j] = new BitmapText(bmf);
            texts1[j].setSize(bmf.getCharSet().getRenderedSize() * 2);
            getGuiNode().attachChild(texts1[j]);
        }
    }

    public long getPassTime(String time1, String time2) {
        try {
            Date now = format.parse(time1);
            Date date1 = format.parse(time2);
            long l = now.getTime() - date1.getTime();
            long hour = l / (60 * 60 * 1000);
            long min = l / (60 * 1000) - hour * 60;
            long s = l / 1000 - hour * 60 * 60 - min * 60;
            return s;
        } catch (Exception e) {
        }
        return 0;
    }

    public String getTime() {
        date = new Date();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        passTime = format.format(date);
        return passTime;
    }
}
