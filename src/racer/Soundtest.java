

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.sound.openAL.SoundSystem;
import com.jmex.sound.openAL.scene.Configuration;

/**
 * @author Arman Ozcelik
 * @version $Id: Soundtest.java,v 1.7 2005/05/17 04:19:04 Mojomonkey Exp $
 */
public class Soundtest extends SimpleGame {
  private int snode;
  int footsteps;
  int background;
  Box box;
  public static void main(String[] args) {
    /*  
    SoundSystem.init(null, SoundSystem.OUTPUT_DEFAULT);
    int nd=SoundSystem.createSoundNode();
    int ft=SoundSystem.create3DSample("D:/eclipse/workspace/JMonkeyEngine/src/jmetest/data/sound/Footsteps.wav");
    SoundSystem.addSampleToNode(ft, nd);
    SoundSystem.setSampleMaxAudibleDistance(ft, 100);
    SoundSystem.setSampleMinAudibleDistance(ft, 1);
    while(true){
        SoundSystem.update(0.0f);
        SoundSystem.draw(nd);
    }
    */
      
    Soundtest app = new Soundtest();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
    
      /*
      SoundManager manager=new SoundManager("D:/eclipse/workspace/JMonkeyEngine/data/sound");
      manager.playMusic("CHAR_CRE_1.ogg");
  */
  }

  protected void simpleUpdate() {      
    SoundSystem.update(0.0f);
  }

  protected void simpleRender() {
      SoundSystem.draw(snode);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame()  {
    display.setTitle("Test Sound Graph");
    SoundSystem.init(display.getRenderer().getCamera(), SoundSystem.OUTPUT_DEFAULT);
    Vector3f max = new Vector3f(5, 5, 5);
    Vector3f min = new Vector3f( -5, -5, -5);
    box = new Box("Box", min, max);
    box.setModelBound(new BoundingSphere());
    box.updateModelBound();
    box.setLocalTranslation(new Vector3f(0, 0, -50));
    TextureState tst = display.getRenderer().createTextureState();
    tst.setEnabled(true);
    tst.setTexture(TextureManager.loadTexture(
        Soundtest.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
        Texture.FM_LINEAR));
    rootNode.setRenderState(tst);
    rootNode.attachChild(box);
    snode = SoundSystem.createSoundNode();
    try{
    
        footsteps = SoundSystem.create3DSample(Soundtest.class.getResource("/data/sound/CHAR_CRE_1.ogg"));
        background =SoundSystem.create3DSample(Soundtest.class.getResource("/data/sound/laser.wav"));
    }catch(Exception e){
        e.printStackTrace();
    }
    
    SoundSystem.setSampleMaxAudibleDistance(footsteps, 100);
    SoundSystem.setSampleMaxAudibleDistance(background, 1000);
    SoundSystem.addSampleToNode(footsteps, snode);
    SoundSystem.addSampleToNode(background, snode);
    SoundSystem.setSamplePosition(footsteps, box.getLocalTranslation().x, box.getLocalTranslation().y, box.getLocalTranslation().z);
    SoundSystem.setSampleMinAudibleDistance(footsteps, 4);
    SoundSystem.setSampleVolume(background, 0.1f);
    Configuration config=new Configuration();
    config.setDistortion(-10, 70, 6000, 5000, 7000);
    
    SoundSystem.setSampleConfig(footsteps, config);
    
    
  }
}
