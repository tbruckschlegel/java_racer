/**
 * 
 */
package camera;

import java.util.ArrayList;

import com.jme.math.Vector3f;
import com.jme.system.JmeException;

/**
 * @author tommy
 *
 */
class SplineInterpolation
{
    private ArrayList<Vector3f> mPoints = new ArrayList<Vector3f>();
    //---------------------------------------------------------------------
    SplineInterpolation()
    {
    }

    
    void Catmull_Rom_Spline(
            final Vector3f p1,
            final Vector3f p2,
            final Vector3f p3,
            final Vector3f p4,
            float t, 
            Vector3f output )
        {
            float tSqr = t*t*0.5f;
            float tSqrSqr = t*tSqr;
            t *= 0.5f;

            assert( output != p1 );
            assert( output != p2 );
            assert( output != p3 );
            assert( output != p4 );
            //output=new Vector3f();
            output.zero();
            
            Vector3f a=new Vector3f(), b=new Vector3f(), c=new Vector3f(), d=new Vector3f();

            // matrix row 1
            a=p1.mult(-tSqrSqr, a);     // 0.5 t^3 * [ (-1*p1) + ( 3*p2) + (-3*p3) + p4 ]
            b=p2.mult(tSqrSqr*3, b );
            c=p3.mult(tSqrSqr*-3, c );
            d=p4.mult(tSqrSqr, d );

            output.addLocal( a );
            output.addLocal( b );
            output.addLocal( c );
            output.addLocal( d );

            // matrix row 2
            a=p1.mult( tSqr*2f,  a );      // 0.5 t^2 * [ ( 2*p1) + (-5*p2) + ( 4*p3) - p4 ]
            b=p2.mult( tSqr*-5f, b );
            c=p3.mult( tSqr*4f,  c );
            d=p4.mult( -tSqr,    d );

            output.addLocal( a );
            output.addLocal( b );
            output.addLocal( c );
            output.addLocal( d );

            // matrix row 3
            a=p1.mult( -t, a );           // 0.5 t * [ (-1*p1) + p3 ]
            b=p3.mult( t,  b );

            output.addLocal( a );
            output.addLocal( b );

            // matrix row 4
            output.addLocal( p2 );    // p2
        }
    
    
    
    //---------------------------------------------------------------------
    void addPoint(final Vector3f p)
    {
        Vector3f d=new Vector3f(p);

        mPoints.add(d);
        
        if(mPoints.size()>64)
        {
            mPoints.remove(0);
        }

    }
    //---------------------------------------------------------------------
    Vector3f interpolate(float t)
    {
        Vector3f output=new Vector3f();
        Catmull_Rom_Spline(new Vector3f(mPoints.get(0)), new Vector3f(mPoints.get(1)), new Vector3f(mPoints.get(2)), new Vector3f(mPoints.get(3)), t, output);
        
        System.out.println("p0: "+mPoints.get(0));
        System.out.println("p1: "+mPoints.get(1));
        System.out.println("p2: "+mPoints.get(2));
        System.out.println("p3: "+mPoints.get(3));
        System.out.println("output: "+output);
        return output;
    }
    //---------------------------------------------------------------------
    final Vector3f getPoint(int index)
    {
        if(index > mPoints.size())
            throw new JmeException("Point index is out of bounds! ("+mPoints.size()+")");

        return mPoints.get(index);
    }
    //---------------------------------------------------------------------
    int getNumPoints()
    {
        return mPoints.size();
    }
    //---------------------------------------------------------------------
    void clear()
    {
        mPoints.clear();
    }
    //---------------------------------------------------------------------
    void updatePoint(int  index, final Vector3f value)
    {
        if (index > mPoints.size())
            throw new JmeException("Point index is out of bounds!");

        mPoints.set(index, value);

    }
    //---------------------------------------------------------------------


}