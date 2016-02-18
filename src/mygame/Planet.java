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
    final int totalTimeTrans = 5;
    final int incEnergy = 1,decEnergy = 2;    
    float energy=100,temp=0;
     int  state=0;
    Main main;
    SimpleApplication sa;
    Material mat;
    Geometry geom;

    
    public Planet(Main main, SimpleApplication sa,Material mat){
    this.main = main;
    this.sa=sa;
    this.mat =mat;
    initPlanet();
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
    
    public float getEnergy(){
    return energy;
    }
       
    public void setState(int state){
     if(state==0){
         temp =0;
     }
    this.state = state;
    }
    
    public synchronized boolean absorb(Planet planet){
        if(state==0&&planet.state==0)
        {state = 1;
         planet.setState(2);
         return true;
        }     
        return false;  
    }
    
        public synchronized boolean attack(Planet planet){
        if(state==0&&planet.state==0)
        {planet.decEnergy(energy*.5f);
        energy = energy*.5f;
         return true;
        }     
        return false;  
    }
        
     public synchronized boolean infusion(Planet planet){
        if(state==0&&planet.state==0)
        {state = 2;
         planet.setState(1);
         return true;
        }     
        return false;  
    }
     
     public synchronized boolean donation(Planet planet){
        if(state==0&&planet.state==0)
        {planet.incEnergy(energy*.5f);
        energy = energy*.5f;
         return true;
        }     
        return false;  
    }
    

   
    
    
}
