/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;


public class Planet extends Node{
    final int unitEnergyTrans = 5;
    final int totalTimeTrans = 2;
    
    // free mean no action, operated mean planet being attacked or infusion, death mean no energy
    final int free=0,absorb = 1,infusion = 2,operated=3,death=4;    
    float energy=100,tempTime=0,n=0;
     int  state=0  ;
 
    Main main;
    SimpleApplication sa;
    Material mat;
    Geometry geom;
    Planet planet;

    
    public Planet(Material mat){
    this.mat =mat;
    initPlanet();
    PlanetControl pControl = new PlanetControl();
    addControl(pControl);
    }
    
    
    private void initPlanet(){
        Sphere largeSphere = new Sphere(64, 64, 2);
        largeSphere.setTextureMode(Sphere.TextureMode.Projected);    
        geom = new Geometry("Ball", largeSphere);
        geom.setMaterial(mat);
        geom.setLocalTranslation(-10f, 5f, 0f);
        attachChild(geom);
    }
      
    
    public void incEnergy(float amount){
    energy +=amount;
    }
 
    public void decEnergy(float amount){
    energy -=amount;
    }
 
    
       
    public synchronized void setState(int state){
     if(state==free||state ==death){
         tempTime =0;
         n=0;
     }
    this.state = state;
    }
    
    // final int free=0,absorb = 1,infusion = 2,operated=3; 
    public  boolean absorb(Planet planet){
        if(state==free && planet.state==free)
        {state = absorb;      
         planet.setState(operated);
           this.planet = planet;
         return true;
        }     
        return false;  
    }
    
    //attack will both decEnergy, attacked planet may death
        public  boolean attack(Planet planet){
               if (state == free && planet.state == free) {
            setState(operated);
            planet.setState(operated);
            float temp ;
            temp = energy * .5f;
            
            // attacked planet will death when its anergy less than half of attacker's energy
            if (planet.energy <= temp) {
                temp = planet.energy;
            }
            decEnergy(temp);
            planet.decEnergy(temp);
            
            if (planet.energy <= 0) {
                planet.setState(death);
            } else {
                planet.setState(free);
            }
            setState(free);
             System.out.println("Planet 101:"  + " attack" + " plant0:" + energy + " plant1:" + planet.energy);                     
              
            return true;
        }
        return false;
    } 
    
        
     public  boolean infusion(Planet planet){
       if(state==free && planet.state==free)
        {state = infusion;   
         planet.setState(operated);
        this.planet = planet;
         return true;
        }     
        return false;  
    }
     
     
     //donation don't need to check if any of them death
     public  boolean donation(Planet planet){
        if (state == free && planet.state == free) {
            setState(operated);
            planet.setState(operated);
            float temp = 0;
            temp = energy * .5f;

            decEnergy(temp);
            planet.incEnergy(temp);

            planet.setState(free);
            setState(free);
              System.out.println("Planet 134:"  + " donation" + " plant0:" + energy + " plant1:" + planet.energy);                     
          
            return true;
        }
        return false;
    }
    
    public void checkActionEnd(float time) {
        if (time >= totalTimeTrans) {
            setState(free);
            planet.setState(free);
        } else if (energy <= 0 || planet.energy <= 0) {
            if (energy <= 0) {
                setState(death);
                planet.setState(free);
            } else {
                setState(free);
                planet.setState(death);
            }


        }
    }

   
    public class PlanetControl extends AbstractControl{

        @Override
        protected void controlUpdate(float tpf) {
            switch (state) {
                case absorb:
                    n += tpf;
                    if (n >= 1) {
                       float tempEnergy = unitEnergyTrans;
                        if (planet.energy < unitEnergyTrans) {
                            tempEnergy = planet.energy;
                        }
                        incEnergy(tempEnergy);
                        planet.decEnergy(tempEnergy);
                        tempTime++;
                        n--;
                        System.out.println("Planet 174:Time " + tempTime + " absorb" + " plant0:" + energy + " plant1:" + planet.energy);                     
                        checkActionEnd(tempTime);

                    }
                    break;


                case infusion:
                    n += tpf;
                    if (n >= 1) {
                      float  tempEnergy = unitEnergyTrans;
                        if (energy < unitEnergyTrans) {
                            tempEnergy = energy;
                        }
                        decEnergy(tempEnergy);
                        planet.incEnergy(tempEnergy);
                        tempTime++;
                        n--;
                         System.out.println("Planet 192:Time " + tempTime + " infusion" + " plant0:" + energy + " plant1:" + planet.energy);                                         
                        checkActionEnd(tempTime);
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
