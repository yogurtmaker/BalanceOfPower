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
          
          
          
      }
      
     
      
      
      @Override
    public void update(float tpf) {
      } 
      
}
