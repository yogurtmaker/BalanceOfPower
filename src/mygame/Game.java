/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class Game  extends AbstractAppState {
    Planet[] planets;
    Main main;
    AppStateManager asm;
    
    
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
   
          
      }
      
     
      
      
      @Override
    public void update(float tpf) {
      } 
      
}
