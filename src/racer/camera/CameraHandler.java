/*
 * Created on 15.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package camera;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

/**
 * @author tommy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CameraHandler
{
    private static final float MAXPHI = 179*FastMath.DEG_TO_RAD;
    private static final float MINPHI = FastMath.DEG_TO_RAD;
    
    private float theta, phi;
    
    private Camera cam;
    
    private Spatial lookAtTarget;
    private Spatial followTarget;
    
    private SplineInterpolation cameraTrack = new SplineInterpolation();
    private SplineInterpolation carTrack = new SplineInterpolation();
    private SplineInterpolation interpolTrack = new SplineInterpolation();
    
    private float distance;
    
    private float goalDistance;
    
    private float zoomSpeed;
    private float movementSpeed;
    private Vector3f displacement;
    
    private float minDistance, maxDistance;
    
    //tmp variables
    private Vector3f cameraGoalPos, followTargetPos, lookAtTargetPos, sphere, dirVec, upVec, leftVec;
    private float distanceDelta, speed;
    
    ColorRGBA colorTable[] = new ColorRGBA[3];
    ColorRGBA colorTable2[] = new ColorRGBA[3];

    public CameraHandler(float theta, float phi, float distance)
    {
        
        colorTable[0] = new ColorRGBA(1, 0, 0, 1); 
        colorTable[1] = new ColorRGBA(0, 1, 0, 1); 
        colorTable[2] = new ColorRGBA(0, 0, 1, 1); 

        
        colorTable2[0] = new ColorRGBA(1, 1, 0, 1); 
        colorTable2[1] = new ColorRGBA(0, 0, 1, 1); 
        colorTable2[2] = new ColorRGBA(1, 0, 0, 1); 

        this.theta=theta;
        this.phi=phi;
        goalDistance=distance;
        this.distance=distance;
        
        cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
        
        cameraGoalPos = new Vector3f();
        followTargetPos = new Vector3f();
        lookAtTargetPos = new Vector3f();
        sphere = new Vector3f();
        dirVec = new Vector3f();
        upVec = new Vector3f();
        leftVec = new Vector3f();
        
        displacement = new Vector3f();
        
        zoomSpeed = 2.5f;//15.0f;
        movementSpeed = 5.5f;//15.0f;
        
        minDistance=2f;
        maxDistance=400f;
        
        for(int i=0; i<30; i++)
            prevPositions[i]=new Vector3f();

    }

    public void setTarget(Spatial target)
    {
        this.followTarget=target;
        this.lookAtTarget=target;
    }
    
    public void setSpeed(float sp)
    {
        movementSpeed=sp;
    }    
    
    public void setFollowTarget(Spatial target)
    {
        this.followTarget=target;
    }

    public void setLookAtTarget(Spatial target)        
    {
        this.lookAtTarget=target;
    }
    
    public void setDisplacement(float x, float y, float z)        
    {
        this.displacement.x=x;
        this.displacement.y=z;
        this.displacement.z=z;
    }

    public void setGoalDistance(float dist)
    {
        if(dist<minDistance)
            this.goalDistance=minDistance;
        else
            if(dist>maxDistance)
                this.goalDistance=maxDistance;
            else
                this.goalDistance=dist;
    }
    
    public float getGoalDistance()
    {
        return this.goalDistance;
    }
    
    public void setThetaGoal(float theta)
    {
        this.theta=theta;
    }

    public void setPhiGoal(float phi)
    {
        if(phi<MINPHI)
            this.phi=MINPHI;
        else
            if(phi>MAXPHI)
                this.phi=MAXPHI;
            else
                this.phi=phi;
    }
    
    
    public float getThetaGoal()
    {
        return theta;
    }
    
    public float getPhiGoal()
    {
        return phi;
    }
    
    
    Quaternion q1 = new Quaternion();
    Vector3f tmpDistance = new Vector3f();
    Vector3f tmpPosition = new Vector3f();
    Vector3f prevPositions[] = new Vector3f[30];
    int prevPositionsCounter=0;
    int oldNumSamples = 5;
    int numSamples = 5;

    Node lineNode = new Node("path");
    public void drawCameraPath()
    {
        
        lineNode.setLightCombineMode(LightState.OFF);
        lineNode.updateRenderState();
        lineNode.updateGeometricState(0.0f, true);

        DisplaySystem.getDisplaySystem().getRenderer().draw(lineNode);
        
    }
    
    private int counter = 0;
    private float t = 0;
    float oldSpeed=0;
    long time=0;
    
    public void update(float tpf)
    {

        
        
        speed = tpf*movementSpeed;

        if(speed>1f)
            speed=1f;        
        else
            if(speed<0)
                speed=0f;
        

        //System.out.println("speed: "+speed);
        float heading;//, attitude, bank;
        q1.set(followTarget.getLocalRotation());
        q1.normalize();
        
        // get the angles of the object to follow
        float test = q1.x*q1.y + q1.z*q1.w;
        if (test > 0.499) { // singularity at north pole
            heading = 2f * FastMath.atan2(q1.x,q1.w);
            //attitude = FastMath.PI/2f;
            //bank = 0;
            
        }
        else
        {
            if (test < -0.499) { // singularity at south pole
                heading = -2f * FastMath.atan2(q1.x,q1.w);
                //attitude = - FastMath.PI/2f;
                //bank = 0;
            }
            else
            {                
                float sqx = q1.x*q1.x;
                float sqy = q1.y*q1.y;
                float sqz = q1.z*q1.z;
                heading = FastMath.atan2(2f*q1.y*q1.w-2f*q1.x*q1.z , 1f - 2f*sqy - 2f*sqz);
                //attitude = FastMath.asin(2f*test);
                //bank = FastMath.atan2(2f*q1.x*q1.w-2f*q1.y*q1.z , 1f - 2f*sqx - 2f*sqz);          
            }
        }


                
        followTargetPos.x=followTarget.getLocalTranslation().x+displacement.x;
        followTargetPos.y=followTarget.getLocalTranslation().y+displacement.y;
        followTargetPos.z=followTarget.getLocalTranslation().z+displacement.z;
        
        
        
        distanceDelta=(goalDistance-distance) * (tpf*zoomSpeed);
        distance += distanceDelta;
       
        
        
        if(distance<minDistance)
            distance=minDistance;
        
        if(distance>maxDistance)
            distance=maxDistance;
        
        
        
        sphere.x=distance*FastMath.sin(theta+heading)*FastMath.sin(phi);
        sphere.y=distance*FastMath.cos(phi);
        sphere.z=distance*FastMath.cos(theta+heading)*FastMath.sin(phi);

        //System.out.println(" h: "+(theta+heading)*FastMath.RAD_TO_DEG);

        cameraGoalPos.x=sphere.x+followTargetPos.x;
        cameraGoalPos.y=sphere.y+followTargetPos.y;
        cameraGoalPos.z=sphere.z+followTargetPos.z;
          /*    
        if(cameraTrack.getNumPoints()>0)
        {
            if(!cameraGoalPos.equals(cameraTrack.getPoint(cameraTrack.getNumPoints()-1)))
                cameraTrack.addPoint(cameraGoalPos);
        }
        else
            cameraTrack.addPoint(cameraGoalPos);
        
        */
        /*
        if(cameraTrack.getNumPoints()>512)
        {
            cameraTrack.clear();
            lineNode=new Node("path");//.detachAllChildren();
        }
        */
        
        /*
        if(cameraTrack.getNumPoints()>1)
        {
        Vector3f[] vertex = new Vector3f[2];
        ColorRGBA[] color = new ColorRGBA[2];
        //Vector3f[] norms = new Vector3f[3];       
        //norms[0] = new Vector3f(0,0,1);
        //norms[1] = new Vector3f(0,0,1);
        //counter++;
        //if(counter>2)
          //  counter=0;
        
        vertex[0] = new Vector3f(cameraTrack.getPoint(cameraTrack.getNumPoints()-1));
        color[0] = new ColorRGBA(colorTable[counter]);

        vertex[1] = new Vector3f(cameraTrack.getPoint(cameraTrack.getNumPoints()-2));
        color[1] = new ColorRGBA(colorTable[counter]);


        Line l = new Line("Line Group", vertex, null, color, null);
        
        
    //    l.setForceView(true);
  //      l.setLightCombineMode(LightState.OFF);
//        l.setForceCull(false);
        //lineNode.attachChild(l);
        }
        

        if(carTrack.getNumPoints()>1)
        {
        Vector3f[] vertex = new Vector3f[2];
        ColorRGBA[] color = new ColorRGBA[2];
        //Vector3f[] norms = new Vector3f[3];       
        //norms[0] = new Vector3f(0,0,1);
        //norms[1] = new Vector3f(0,0,1);

        
        vertex[0] = new Vector3f(carTrack.getPoint(carTrack.getNumPoints()-1));
        color[0] = new ColorRGBA(colorTable2[counter]);

        vertex[1] = new Vector3f(carTrack.getPoint(carTrack.getNumPoints()-2));
        color[1] = new ColorRGBA(colorTable2[counter]);


        Line l = new Line("Line Group2", vertex, null, color, null);
        
        
    //    l.setForceView(true);
  //      l.setLightCombineMode(LightState.OFF);
//        l.setForceCull(false);
        lineNode.attachChild(l);
        }

        if(interpolTrack.getNumPoints()>1)
        {
        Vector3f[] vertex = new Vector3f[2];
        ColorRGBA[] color = new ColorRGBA[2];
        //Vector3f[] norms = new Vector3f[3];       
        //norms[0] = new Vector3f(0,0,1);
        //norms[1] = new Vector3f(0,0,1);

        
        vertex[0] = new Vector3f(interpolTrack.getPoint(interpolTrack.getNumPoints()-1));
        color[0] = new ColorRGBA(colorTable2[0]);

        vertex[1] = new Vector3f(interpolTrack.getPoint(interpolTrack.getNumPoints()-2));
        color[1] = new ColorRGBA(colorTable2[0]);


        Line l = new Line("Line Group3", vertex, null, color, null);
        
        
    //    l.setForceView(true);
  //      l.setLightCombineMode(LightState.OFF);
//        l.setForceCull(false);
        lineNode.attachChild(l);
        }        
        */
        counter++;
        if(counter>2)
            counter=0;        //tmpPosition.set(cameraGoalPos);
        
        //tmpDistance.set(cameraGoalPos);
        //tmpDistance.subtractLocal(cam.getLocation());
        
        //float interploateSpeed = speed;//0.5f;//0.001f+tmpDistance.length() ;//*((tmpDistance.length()!=0)?tmpDistance.length():1);//tmpDistance.length()*speed; 
//      hermite basis function for smooth interpolation
//      Similar to Gain() above, but very cheap to call
//      value should be between 0 & 1 inclusive

        //float interploateSpeedSquared = interploateSpeed * interploateSpeed;
        
        // Nice little ease-in, ease-out k-like curve
        //interploateSpeed = tmpDistance.length() * (3 * interploateSpeedSquared - 2 * interploateSpeedSquared * interploateSpeed);

        //System.out.println(tmpDistance.length()+"/"+interploateSpeed+"/"+speed);
        // ???
        /*
        if(interploateSpeed>1f)
            interploateSpeed=1f;        
        else
            if(interploateSpeed<0)
            {
                interploateSpeed=0f;
                //cam.setLocation(new Vector3f(0,0,0));//cameraGoalPos..multLocal(0.5f));
            }
        
        System.out.println(tmpDistance.length()+"/"+interploateSpeed+"/"+speed);
//        distanceDelta=(0-tmpDistance) * (tpf*zoomSpeed);
 * */        
        
        //oldNumSamples=numSamples;
    //    numSamples = 30;//25-((int)tmpDistance.lengthSquared())/10;
        //if(numSamples<=1)
          //  numSamples=5;
        
    //    if(prevPositionsCounter>=numSamples)
      //      prevPositionsCounter=0;
        /*
        if(oldNumSamples!=numSamples)
        {
            // fill new part of array
            if(oldNumSamples<numSamples)
            {
                //1 2 3 4      // old array content + length
                //1 2 3 4 x x  // new array content + length
                //1 2 3 4 5 5  // needed content

                tmpPosition.set(prevPositions[oldNumSamples-1]);
                for(int i=oldNumSamples-1; i<numSamples; i++)
                {
                    prevPositions[i].set(tmpPosition);
                }
            }
            else
            {
                // fill new array with old values
                tmpPosition.set(prevPositions[oldNumSamples-1]);
                for(int i=0; i<numSamples-1; i++)
                {
                    //1 2 3 4 5 6 // old array content + length
                    //1 2 3 4     // new array content + length
                    //3 4 5 6     // needed content
                    
                    prevPositions[i].set(prevPositions[(oldNumSamples-numSamples)+i]);
                }
            
            }
            
        }
        */
  //      prevPositions[prevPositionsCounter].set(cam.getLocation());
//        prevPositionsCounter++;
        
//        cam.getLocation().addLocal(tmpDistance.multLocal(interploateSpeed));
        /*
        cam.getLocation().set(new Vector3f(cameraGoalPos));
        
        for(int i=0; i<numSamples; i++)
            cam.getLocation().addLocal(prevPositions[i]);
        
        tmpPosition.set(cameraGoalPos);
        cam.getLocation().addLocal(tmpPosition.multLocal(100*speed));
        cam.getLocation().multLocal(1.0f/((float)(1+numSamples+100*speed)));

        */
        
        //cam.getLocation().set(cameraGoalPos);
        /*
        tmpDistance.set(new Vector3f(cameraGoalPos));
        tmpDistance.subtractLocal(new Vector3f(cam.getLocation()));
        if(!Float.isInfinite(tmpDistance.length()) && !Float.isNaN(tmpDistance.length()) && tmpDistance.length()>0) 
            speed = tmpknce.length()*tpf;
        else
            speed = tpf;
        */
        //System.out.println("speed: "+speed);
          /*      
        float valueSquared = speed * speed;

        // Nice little ease-in, ease-out spline-like curve
        speed =  (3 * valueSquared - 2 * valueSquared * speed);
        
        
        */
        if(speed>1f)
            speed=1f;        
        else
            if(speed<0)
                speed=0f;

//        if(cameraTrack.getNumPoints()>3)
  //      cam.setLocation(cameraTrack.interpolate(speed));
    //System.out.println("speed: "+speed);
        //tmpDistance.set(new Vector3f(cam.getLocation()));
        
        /*
        tmpknce.x=(1-speed)*tmpDistance.x + speed*cameraGoalPos.x + speed*speed*0.5f;
        tmpDistance.y=(1-speed)*tmpDistance.y + speed*cameraGoalPos.y + speed*speed*0.5f;
        tmpDistance.z=(1-speed)*tmpDistance.z + speed*cameraGoalPos.z + speed*speed*0.5f;
        */
        
        
        //tmpDistance.interpolate(new Vector3f(cameraGoalPos), 0.75f);//0.5f*(speed+oldSpeed));
        carTrack.addPoint(new Vector3f(cameraGoalPos));
        
        if(carTrack.getNumPoints()>=16)
        {
            //System.out.println("o:"+new Vector3f(cam.getLocation()));
            cam.setLocation(new Vector3f(carTrack.getPoint(carTrack.getNumPoints()-15)));// .interpolate(0.5f)));
            //interpolTrack.addPoint(new Vector3f(carTrack.interpolate(0.5f)));
            //System.out.println("cGP: "+cameraGoalPos);
            
            
            //System.out.println("n:"+new Vector3f(carTrack.getPoint(2)));
        
    //        carTrack.clear();
            
            //tmpDistance.set(new Vector3f(cam.getLocation()));
            //tmpDistance.subtractLocal(cameraGoalPos);
            
            //System.out.println("distance: "+tmpDistance.length()+" "+(System.nanoTime()-time));
            //time=System.nanoTime();
        }
        

        //cam.setLocation(new Vector3f(cameraGoalPos));
        //cameraTrack.addPoint(new Vector3f(cam.getLocation()));
        oldSpeed = speed;

        
        /*
        System.out.println("tpf: "+tpf);
        System.out.println("speed: "+speed);
        System.out.println("distance: "+tmpDistance.length());
        Vector3f d = new Vector3f(cam.getLocation());
        d.interpolate(cameraGoalPos, speed);
        System.out.println("distance after interpolation: "+d.subtractLocal(cameraGoalPos).length());
        
        tmpDistance.set(new Vector3f(cam.getLocation()));
        tmpDistance.interpolate(cameraGoalPos, speed);
        //tmpPosition.set(cam.getLocation())
        cam.setLocation(new Vector3f(tmpDistance));
        */
        //System.out.println(1+numSamples+100*speed);
        
        
        
        
        //Float tmpFloat = new Float(interploateSpeed);
      //  if(tmpDistance.length()>0.25f)
  //          cam.getLocation().interpolate(cameraGoalPos, interploateSpeed);
  //      else
        //cam.setLocation(cameraGoalPos);
                
        //cam.setLocation( new Vector3f(cam.getLocation().x, tmpPosition.y, cam.getLocation().z) );
        //cam.update();
        //tmpPosition.set(cam.getLocaticameraGoalPoson());
        //tmpPosition.
        
        
        //lookAtTarget();
    }
    
    private void lookAtTarget()
    {
        lookAtTargetPos.x=lookAtTarget.getLocalTranslation().x+displacement.x;
        lookAtTargetPos.y=lookAtTarget.getLocalTranslation().y+displacement.y;
        lookAtTargetPos.z=lookAtTarget.getLocalTranslation().z+displacement.z;    
        
        dirVec.set(lookAtTargetPos);
        
        dirVec.subtractLocal(cam.getLocation()).normalizeLocal();
        
        upVec.x=0;
        upVec.z=0;
        upVec.y=1;
        
        
        leftVec.set(upVec);       
        leftVec.crossLocal(dirVec).normalizeLocal();
        
        upVec.set(dirVec);
        upVec.crossLocal(leftVec).normalizeLocal();

        cam.setAxes(leftVec, upVec, dirVec);
    }

    public void updateOpenGL()
    {
        lookAtTarget();
    }

    
    public void setMinDistance(float min)
    {
        minDistance=min;
    }
    
    public void setMaxDistance(float max)
    {
        maxDistance=max;
    }
        
    
    
}
