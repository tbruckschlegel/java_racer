import org.odejava.collision.Contact;
import org.odejava.ode.Ode;

import com.jme.math.Vector3f;
import com.jme.system.JmeException;
import com.jmex.physics.DynamicPhysicsObject;
import com.jmex.physics.PhysicsObject;
import com.jmex.physics.contact.PhysicsCallBack;


public class CarPhysicsCallback implements PhysicsCallBack
{
    static private float mu, mu2, slip1, slip2, slip2Factor;
    static private PhysicsObject tw1, tw2, tw3, tw4;
    static float fDir[]=new float[3];
    static Vector3f direction = new Vector3f(0, -1, 0);
    static Vector3f tireRollDiretion = new Vector3f();


    
    
    public void onContact(PhysicsObject obj1, PhysicsObject obj2, Contact contact)
    {
        mu2 = 0.15f;//.85f;
        mu = 0.15f;//0.95f;
        slip1 = 0.025f;
        slip2Factor = 0.005f;

        slip2=0.0015f;

        contact.setMode(Ode.dContactBounce | Ode.dContactFDir1  | Ode.dContactMu2 | Ode.dContactSlip1
                | Ode.dContactSlip2 | Ode.dContactApprox1 );

        if (obj1.getName().contains("Wheel"))
        {
            ((DynamicPhysicsObject) obj1).setContact(true);
            /*
            if (obj1.getName().contains("0"))
                tw1 = obj1;
            else
                if (obj1.getName().contains("1"))
                    tw2 = obj1;
                else
                    if (obj1.getName().contains("2"))
                        tw3 = obj1;
                    else
                        if (obj1.getName().contains("3"))
                            tw4 = obj1;
            */
            
            slip2 =  ((DynamicPhysicsObject) obj1).getLinearVelocity().length() * slip2Factor;
            
            tireRollDiretion.set(((DynamicPhysicsObject) obj1).getPhysicalEntity().getQuaternion().getRotationColumn(2));
            tireRollDiretion.crossLocal(direction);

            fDir[0]=tireRollDiretion.x;
            fDir[1]=tireRollDiretion.y;
            fDir[2]=tireRollDiretion.z;
            
            contact.setFdir1(fDir);
            contact.setBounce(0.001f);
            contact.setBounceVel(1f);
            contact.setMu(mu);
            contact.setMu2(mu2);
            contact.setSlip1(slip1);
            contact.setSlip2(slip2);

        }
        else
        {
            if (obj2.getName().contains("Wheel"))
            {
                ((DynamicPhysicsObject) obj2).setContact(true);
                /*
                if (obj1.getName().contains("0"))
                    tw1 = obj2;
                else
                    if (obj1.getName().contains("1"))
                        tw2 = obj2;
                    else
                        if (obj1.getName().contains("2"))
                            tw3 = obj2;
                        else
                            if (obj1.getName().contains("3"))
                                tw4 = obj2;
                */
                slip2 = ((DynamicPhysicsObject) obj2).getLinearVelocity().length() * slip2Factor;

                tireRollDiretion.set(((DynamicPhysicsObject) obj2).getPhysicalEntity().getQuaternion().getRotationColumn(2));
                tireRollDiretion.crossLocal(direction);

                fDir[0]=tireRollDiretion.x;
                fDir[1]=tireRollDiretion.y;
                fDir[2]=tireRollDiretion.z;
                
                contact.setFdir1(fDir);
                contact.setBounce(0.001f);
                contact.setBounceVel(1f);
                contact.setMu(mu);
                contact.setMu2(mu2);
                contact.setSlip1(slip1);
                contact.setSlip2(slip2);
            }
            else
            {
                contact.setMode(Ode.dContactBounce | Ode.dContactFDir1  | Ode.dContactMu2 | Ode.dContactSlip1
                        | Ode.dContactSlip2 | Ode.dContactApprox1 );
               
                contact.setBounce(0.001f);
                contact.setBounceVel(1f);
                contact.setMu(mu);
                contact.setMu2(mu2);
                contact.setSlip1(slip1);
                contact.setSlip2(slip2);
                /*
                contact.setBounce(0.0001f);
                contact.setBounceVel(4f);
                contact.setMu(1.0f);
                contact.setMu2(1.0f);
                contact.setSlip1(0.000001f);
                contact.setSlip2(0.000001f);
                */

            }
        }        
    
    }

    // particle physics
    public void defaultContact(Contact contact)
    {
        throw new JmeException("No physics for particles yet! ("+contact.toString()+")");
        
    }
    
    
    public void drawInfoStuff()
    {
        /*
        if (tw1 != null)
        {
            Vector3f[] vertex = new Vector3f[2];
            ColorRGBA[] color = new ColorRGBA[2];

            //w1
            vertex[1] = new Vector3f();
            vertex[1].x = tw1.getSpatial().getLocalTranslation().x;
            vertex[1].y = tw1.getSpatial().getLocalTranslation().y;
            vertex[1].z = tw1.getSpatial().getLocalTranslation().z;
            color[1] = new ColorRGBA(1f, 1f, 0f, 1f);

            vertex[0] = new Vector3f(vertex[1]);
            vertex[0].x += ((DynamicPhysicsObject) tw1).getLinearVelocity().x;
            vertex[0].y += ((DynamicPhysicsObject) tw1).getLinearVelocity().y;
            vertex[0].z += ((DynamicPhysicsObject) tw1).getLinearVelocity().z;
            color[0] = new ColorRGBA(1f, 1f, 0f, 1f);

            Line l = new Line("Line Group", vertex, null, color, null);
            l.setModelBound(new BoundingBox());
            l.setSolidColor(new ColorRGBA(1f, 0f, 0f, 1f));
            l.updateModelBound();
            l.setForceView(true);
            l.setLightCombineMode(LightState.OFF);
            l.setForceCull(false);
            DisplaySystem.getDisplaySystem().getRenderer().draw(l);
            //w2
            vertex[1] = new Vector3f();
            vertex[1].x = tw2.getSpatial().getLocalTranslation().x;
            vertex[1].y = tw2.getSpatial().getLocalTranslation().y;
            vertex[1].z = tw2.getSpatial().getLocalTranslation().z;
            color[1] = new ColorRGBA(1f, 1f, 0f, 1f);

            vertex[0] = new Vector3f(vertex[1]);
            vertex[0].x += ((DynamicPhysicsObject) tw2).getLinearVelocity().x;
            vertex[0].y += ((DynamicPhysicsObject) tw2).getLinearVelocity().y;
            vertex[0].z += ((DynamicPhysicsObject) tw2).getLinearVelocity().z;
            color[0] = new ColorRGBA(1f, 1f, 0f, 1f);

            l = new Line("Line Group", vertex, null, color, null);
            l.setModelBound(new BoundingBox());
            l.setSolidColor(new ColorRGBA(1f, 0f, 0f, 1f));
            l.updateModelBound();
            l.setForceView(true);
            l.setLightCombineMode(LightState.OFF);
            l.setForceCull(false);
            DisplaySystem.getDisplaySystem().getRenderer().draw(l);
            //w3
            vertex[1] = new Vector3f();
            vertex[1].x = tw3.getSpatial().getLocalTranslation().x;
            vertex[1].y = tw3.getSpatial().getLocalTranslation().y;
            vertex[1].z = tw3.getSpatial().getLocalTranslation().z;
            color[1] = new ColorRGBA(1f, 1f, 0f, 1f);

            vertex[0] = new Vector3f(vertex[1]);
            vertex[0].x += ((DynamicPhysicsObject) tw3).getLinearVelocity().x;
            vertex[0].y += ((DynamicPhysicsObject) tw3).getLinearVelocity().y;
            vertex[0].z += ((DynamicPhysicsObject) tw3).getLinearVelocity().z;
            color[0] = new ColorRGBA(1f, 1f, 0f, 1f);

            l = new Line("Line Group", vertex, null, color, null);
            l.setModelBound(new BoundingBox());
            l.setSolidColor(new ColorRGBA(1f, 0f, 0f, 1f));
            l.updateModelBound();
            l.setForceView(true);
            l.setLightCombineMode(LightState.OFF);
            l.setForceCull(false);
            DisplaySystem.getDisplaySystem().getRenderer().draw(l);
            //w4
            vertex[1] = new Vector3f();
            vertex[1].x = tw4.getSpatial().getLocalTranslation().x;
            vertex[1].y = tw4.getSpatial().getLocalTranslation().y;
            vertex[1].z = tw4.getSpatial().getLocalTranslation().z;
            color[1] = new ColorRGBA(1f, 1f, 0f, 1f);

            Vector3f tmp = new Vector3f(((DynamicPhysicsObject) tw4).getPhysicalEntity().getQuaternion()
                    .getRotationColumn(2));
            tmp = tmp.crossLocal(new Vector3f(0, -1, 0));

            //tmp=tmp.crossLocal(((DynamicPhysicsObject)tw4).getPhysicalEntity().getQuaternion().getRotationColumn(2));

            vertex[0] = new Vector3f(vertex[1]);
            vertex[0].x += ((DynamicPhysicsObject) tw4).getLinearVelocity().x;
            vertex[0].y += ((DynamicPhysicsObject) tw4).getLinearVelocity().y;
            vertex[0].z += ((DynamicPhysicsObject) tw4).getLinearVelocity().z;
            color[0] = new ColorRGBA(1f, 1f, 0f, 1f);

            l = new Line("Line Group", vertex, null, color, null);
            l.setModelBound(new BoundingBox());
            l.setSolidColor(new ColorRGBA(1f, 0f, 0f, 1f));
            l.updateModelBound();
            l.setForceView(true);
            l.setLightCombineMode(LightState.OFF);
            l.setForceCull(false);
            DisplaySystem.getDisplaySystem().getRenderer().draw(l);

            l = null;
        }
        */

    }   
    
}
