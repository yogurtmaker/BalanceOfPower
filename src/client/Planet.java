/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;

public class Planet extends Node {

    final int unitEnergyTrans = 1;//energy transfer per 0.2s
    float totaltime = 0;
    // free mean no action, operated mean planet being attacked or infusion, death mean no energy
    final int free = 0, absorb = 1, infusion = 2, operated = 3, death = 4;
    double energy = 100, tempTime = 0;
    int state = 0;
    GameClient main;
    SimpleApplication sa;
    Material mat, arrmat;
    Geometry geom, arrow;
    Planet planet;
    Node trackNode;
    Vector3f hitVector;

    public Planet(Material mat, GameClient main) {
        this.mat = mat;
        this.sa = main;
        initPlanet();
        PlanetControl pControl = new PlanetControl();
        addControl(pControl);
    }

    private void initPlanet() {
        Sphere largeSphere = new Sphere(64, 64, 2);
        largeSphere.setTextureMode(Sphere.TextureMode.Projected);
        geom = new Geometry("Ball", largeSphere);
        geom.setMaterial(mat);
        attachChild(geom);
    }
    
     public synchronized void setEnergy(double amount) {
        energy = amount;
    }

    public synchronized double addToEnergy(double amount) {
        energy += amount;
        return energy;
    }

    public synchronized int setState(int state) {
        int tempState = this.state;
        this.state = state;
        return tempState;
    }

    // final int free=0,absorb = 1,infusion = 2,operated=3; 
    public boolean absorb(Planet planet) {
        if (state == free && planet.state != death) {
            setState(absorb);
            this.planet = planet;
            return true;
        }
        if (state == absorb) {
            setState(free);
        }
        return false;
    }

    //attack will both decEnergy, attacked planet may death
    public boolean attack(Planet planet) {
        if (state == free) {
            setState(operated);
            double temp;
            temp = energy * .5;
            // attacked planet will death when its anergy less than half of attacker's energy
            if (planet.energy <= temp) {
                temp = planet.energy;
            }
            addToEnergy(-temp);
            planet.addToEnergy(-temp);

            if (planet.energy <= 0) {
                planet.setState(death);
            } else {
                planet.setState(free);
            }

            System.out.println("Planet 101:" + " attack" + " plant0:" + energy + " plant1:" + planet.energy);
            setState(free);
            return true;
        }
        return false;
    }

    public boolean infusion(Planet planet) {
        if (state == free && planet.state != death) {
            setState(infusion);
            this.planet = planet;
            return true;
        }
        if (state == infusion) {
            setState(free);
        }

        return false;
    }

    //donation don't need to check if any of them death
    public boolean donation(Planet planet) {
        if (state == free) {
            setState(operated);
            double temp = 0;
            temp = energy * .5f;
            addToEnergy(-temp);
            planet.addToEnergy(temp);
            planet.setState(free);

            System.out.println("Planet 134:" + " donation" + " plant0:" + energy + " plant1:" + planet.energy);
            setState(free);
            return true;
        }
        return false;
    }

    public void checkActionEnd() {
        if (energy <= 0) {
            setState(death);
            planet.setState(free);
        }
        if (planet.energy <= 0) {
            setState(free);
            planet.setState(death);
        }
    }
    private ActionListener actListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
        }
        //System.out.println(hitVector);
    };

    public float[] getVec() {
        float[] xyz = {geom.getWorldTranslation().x, geom.getWorldTranslation().y, geom.getWorldTranslation().z};
        return xyz;
    }

    public class PlanetControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            double tempEnergy = unitEnergyTrans;
            switch (state) {
                case absorb:
                    tempTime += tpf;
                    if (tempTime >= 0.2) {
                        if (planet.energy < unitEnergyTrans) {
                            tempEnergy = planet.energy;
                        }
                        addToEnergy(tempEnergy);
                        planet.addToEnergy(-tempEnergy);
                        System.out.println("Planet 174:state " + state + " absorb" + " plant0:" + energy + " plant1:" + planet.energy);
                        checkActionEnd();
                        tempTime -= 0.2;
                    }
                    break;


                case infusion:
                    tempTime += tpf;
                    if (tempTime >= 0.2) {
                        if (energy < unitEnergyTrans) {
                            tempEnergy = energy;
                        }
                        addToEnergy(-tempEnergy);
                        planet.addToEnergy(tempEnergy);
                        System.out.println("Planet 192:state " + state + " infusion" + " plant0:" + energy + " plant1:" + planet.energy);
                        checkActionEnd();
                        tempTime -= 0.2;
                    }
                    break;
                default:
                    break;

            }



        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }
    }
}
