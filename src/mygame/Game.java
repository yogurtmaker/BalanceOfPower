/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class Game  extends AbstractAppState {
    Planet[] planets;
    Main main;
    AppStateManager asm;
    
    private ActionListener actionListener = new ActionListener()  {
        public void onAction(String name, boolean keyPressed, float tpf) {
           if(name.equals("absorb")){
              planets[0].absorb( planets[1]);
           }
           else  if(name.equals("attack")){
                planets[0].attack(planets[1]);
               }
           else  if(name.equals("infusion")){
                planets[0].infusion( planets[1]);
          }
           else  if(name.equals("donation")){
                planets[0].donation(planets[1]);
                }
           } 
        
    };
    
      @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        planets = new Planet[5];
          
        for(int i=0; i<5;i++){
        planets[i] = new Planet(main.mats[i]);
        planets[i].geom.setLocalTranslation(-10+5*i, 5f, 0f);
          main.getRootNode().attachChild(planets[i]);
            System.out.println("print: "+i);
        }
   
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("absorb", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("infusion", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("donation", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addListener(actionListener, "absorb", "attack","infusion","donation");
      }
      
     
      
      
      @Override
    public void update(float tpf) {
      } 
      
}
