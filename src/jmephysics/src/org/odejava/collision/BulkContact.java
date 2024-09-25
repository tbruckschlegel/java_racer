/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the odejava nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava.collision;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Vector3f;

/**
 * <p>
 * This is an extended version of standard Contact class. This solution is
 * faster on all occasions, but real performance gains occur when iterating
 * over a large set of contacts (several hundreds per step). Transfers contact
 * data from ByteBuffers to local arrays in a single ByteBuffer method call.
 * Commit writes local arrays back to ByteBuffers in a single ByteBuffer method
 * call.
 * </p>
 * <p>
 * Note: when iterating collision data in a tight loop, avoid creating any
 * objects (e.g. Vector3f, Quaternion4f), this can seriously affect your
 * simulation performance.
 * </p>
 *
 * Created 13.03.2004 (dd.mm.yyyy)
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *
 * see http://odejava.dev.java.net
 *
 */
public class BulkContact extends Contact {
    // Contact count varies on each step
    int contactCount;
    // Maximum amout of contacts in any time of simulation
    public static final int ARRAY_SIZE = 1500;
    // Preallocated local arrays
    int[] intArray = new int[INTBUF_CHUNK_SIZE * ARRAY_SIZE];
    float[] floatArray = new float[FLOATBUF_CHUNK_SIZE * ARRAY_SIZE];

    /**
     * @param intBuf
     * @param floatBuf
     */
    public BulkContact(IntBuffer intBuf, FloatBuffer floatBuf) {
        super(intBuf, floatBuf);
    }

    /**
     * Bulk load ByteBuffer data to local arrays on a single call. This is
     * required before you start iterating your contact data. Call e.g.
     * bulkContact.load(collision.getContactCount());
     *
     */
    public void load(int contactCount) {
        this.contactCount = contactCount;
        // Copy ByteBuffers to local arrays
        intBuf.rewind();
        floatBuf.rewind();

        intBuf.get(intArray, 0, INTBUF_CHUNK_SIZE * contactCount);
        floatBuf.get(floatArray, 0, FLOATBUF_CHUNK_SIZE * contactCount);
    }

    /**
     * Commit local arrays back to ByteBuffers in a single call. This is
     * required before calling Collision.applyContacts(), if local arrays are
     * changed.
     */
    public void commit() {
        intBuf.rewind();
        floatBuf.rewind();

        // Copy local arrays back to ByteBuffers
        intBuf.put(intArray, 0, INTBUF_CHUNK_SIZE * contactCount);
        floatBuf.put(floatArray, 0, FLOATBUF_CHUNK_SIZE * contactCount);
    }

    /**
     * Ignore contact so it does not affect to simulation. Note: if you wish to
     * ignore certain geom <->geom collisions then use categoryBits and
     * collideBits instead, that is a lot faster.
     *
     * @param idx The index of the contact to be ignored
     */
    public void ignoreContact(int idx) {
        setGeomID1(0, idx);
        setGeomID2(0, idx);
    }

    public int getGeomID1() {
        return intArray[index * INTBUF_CHUNK_SIZE + GEOM_ID1];
    }

    public int getGeomID1(int idx) {
        return intArray[idx * INTBUF_CHUNK_SIZE + GEOM_ID1];
    }

    public void setGeomID1(int id) {
        intArray[index * INTBUF_CHUNK_SIZE + GEOM_ID1] = id;
    }

    public void setGeomID1(int id, int idx) {
        intArray[idx * INTBUF_CHUNK_SIZE + GEOM_ID1] = id;
    }

    public int getGeomID2() {
        return intArray[index * INTBUF_CHUNK_SIZE + GEOM_ID2];
    }

    public int getGeomID2(int idx) {
        return intArray[idx * INTBUF_CHUNK_SIZE + GEOM_ID2];
    }

    public void setGeomID2(int id) {
        intArray[index * INTBUF_CHUNK_SIZE + GEOM_ID2] = id;
    }

    public void setGeomID2(int id, int idx) {
        intArray[idx * INTBUF_CHUNK_SIZE + GEOM_ID2] = id;
    }

    public int getBodyID1() {
        return intArray[index * INTBUF_CHUNK_SIZE + BODY_ID1];
    }

    public int getBodyID1(int idx) {
        return intArray[idx * INTBUF_CHUNK_SIZE + BODY_ID1];
    }

    public void setBodyID1(int id) {
        intArray[index * INTBUF_CHUNK_SIZE + BODY_ID1] = id;
    }

    public void setBodyID1(int id, int idx) {
        intArray[idx * INTBUF_CHUNK_SIZE + BODY_ID1] = id;
    }

    public int getBodyID2() {
        return intArray[index * INTBUF_CHUNK_SIZE + BODY_ID2];
    }

    public int getBodyID2(int idx) {
        return intArray[idx * INTBUF_CHUNK_SIZE + BODY_ID2];
    }

    public void setBodyID2(int id) {
        intArray[index * INTBUF_CHUNK_SIZE + BODY_ID2] = id;
    }

    public void setBodyID2(int id, int idx) {
        intArray[idx * INTBUF_CHUNK_SIZE + BODY_ID2] = id;
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @return mode of surface contact
     */
    public int getMode() {
        return intArray[index * INTBUF_CHUNK_SIZE + MODE];
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @return mode of surface contact
     */
    public int getMode(int idx) {
        return intArray[idx * INTBUF_CHUNK_SIZE + MODE];
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @param mode
     *            of surface contact
     */
    public void setMode(int mode) {
        intArray[index * INTBUF_CHUNK_SIZE + MODE] = mode;
    }

    /**
     * Note, if mode = -1 then default surface parameter values are used. You
     * can set default surface parameters through Collision class.
     *
     * @param mode of surface contact
     */
    public void setMode(int mode, int idx) {
        intArray[idx * INTBUF_CHUNK_SIZE + MODE] = mode;
    }

    //
    // floatBuffer methods
    //

    public void getPosition(Vector3f position) {
        position.x = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION];
        position.y = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position.z = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 2];
    }

    public void getPosition(Vector3f position, int idx) {
        position.x = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION];
        position.y = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position.z = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 2];
    }

    public void getPosition(float[] position) {
        position[0] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION];
        position[1] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position[2] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 2];
    }

    public void getPosition(float[] position, int idx) {
        position[0] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION];
        position[1] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position[2] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 2];
    }

    public float[] getPosition() {
        float[] position = new float[3];
        position[0] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION];
        position[1] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position[2] = floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 2];
        return position;
    }

    public float[] getPosition(int idx) {
        float[] position = new float[3];
        position[0] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION];
        position[1] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 1];
        position[2] = floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 2];
        return position;
    }

    public void setPosition(float[] position) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION] = position[0];
        floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 1] = position[1];
        floatArray[index * FLOATBUF_CHUNK_SIZE + POSITION + 2] = position[2];
    }

    public void setPosition(float[] position, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION] = position[0];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 1] = position[1];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + POSITION + 2] = position[2];
    }

    public Vector3f getNormal(Vector3f normal) {
        normal.x = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal.y = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal.z = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
       return normal;
    }

    public void getNormal(Vector3f normal, int idx) {
        normal.x = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal.y = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal.z = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
    }

    public void getNormal(float[] normal) {
        normal[0] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal[1] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal[2] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
    }

    public void getNormal(float[] normal, int idx) {
        normal[0] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal[1] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal[2] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
    }

    public float[] getNormal() {
        float[] normal = new float[3];
        normal[0] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal[1] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal[2] = floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
        return normal;
    }

    public float[] getNormal(int idx) {
        float[] normal = new float[3];
        normal[0] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL];
        normal[1] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1];
        normal[2] = floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2];
        return normal;
    }

    public void setNormal(float[] normal) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL] = normal[0];
        floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 1] = normal[1];
        floatArray[index * FLOATBUF_CHUNK_SIZE + NORMAL + 2] = normal[2];
    }

    public void setNormal(float[] normal, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL] = normal[0];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 1] = normal[1];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + NORMAL + 2] = normal[2];
    }

    public float getDepth() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + DEPTH];
    }

    public float getDepth(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + DEPTH];
    }

    public void setDepth(float depth) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + DEPTH] = depth;
    }

    public void setDepth(float depth, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + DEPTH] = depth;
    }

    public void getFdir1(float[] fdir1) {
        fdir1[0] = floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1];
        fdir1[1] = floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1];
        fdir1[2] = floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2];
    }

    public void getFdir1(float[] fdir1, int idx) {
        fdir1[0] = floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1];
        fdir1[1] = floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 1];
        fdir1[2] = floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 2];
    }

    public void setFdir1(float[] fdir1) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1] = fdir1[0];
        floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1 + 1] = fdir1[1];
        floatArray[index * FLOATBUF_CHUNK_SIZE + FDIR1 + 2] = fdir1[2];
    }

    public void setFdir1(float[] fdir1, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1] = fdir1[0];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 1] = fdir1[1];
        floatArray[idx * FLOATBUF_CHUNK_SIZE + FDIR1 + 2] = fdir1[2];
    }

    public float getMu() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + MU];
    }

    public float getMu(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + MU];
    }

    public void setMu(float mu) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + MU] = mu;
    }

    public void setMu(float mu, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + MU] = mu;
    }

    public float getMu2() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + MU2];
    }

    public float getMu2(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + MU2];
    }

    public void setMu2(float mu2) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + MU2] = mu2;
    }

    public void setMu2(float mu2, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + MU2] = mu2;
    }

    public float getBounce() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + BOUNCE];
    }

    public float getBounce(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + BOUNCE];
    }

    public void setBounce(float bounce) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + BOUNCE] = bounce;
    }

    public void setBounce(float bounce, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + BOUNCE] = bounce;
    }

    public float getBounceVel() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL];
    }

    public float getBounceVel(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL];
    }

    public void setBounceVel(float bounceVel) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL] = bounceVel;
    }

    public void setBounceVel(float bounceVel, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + BOUNCE_VEL] = bounceVel;
    }

    public float getSoftErp() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + SOFT_ERP];
    }

    public float getSoftErp(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + SOFT_ERP];
    }

    public void setSoftErp(float softErp) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + SOFT_ERP] = softErp;
    }

    public void setSoftErp(float softErp, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + SOFT_ERP] = softErp;
    }

    public float getSoftCfm() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + SOFT_CFM];
    }

    public float getSoftCfm(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + SOFT_CFM];
    }

    public void setSoftCfm(float softCfm) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + SOFT_CFM] = softCfm;
    }

    public void setSoftCfm(float softCfm, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + SOFT_CFM] = softCfm;
    }

    public float getMotion1() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + MOTION1];
    }

    public float getMotion1(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + MOTION1];
    }

    public void setMotion1(float motion1) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + MOTION1] = motion1;
    }

    public void setMotion1(float motion1, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + MOTION1] = motion1;
    }

    public float getMotion2() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + MOTION2];
    }

    public float getMotion2(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + MOTION2];
    }

    public void setMotion2(float motion2) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + MOTION2] = motion2;
    }

    public void setMotion2(float motion2, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + MOTION2] = motion2;
    }

    public float getSlip1() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + SLIP1];
    }

    public float getSlip1(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + SLIP1];
    }

    public void setSlip1(float slip1) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + SLIP1] = slip1;
    }

    public void setSlip1(float slip1, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + SLIP1] = slip1;
    }

    public float getSlip2() {
        return floatArray[index * FLOATBUF_CHUNK_SIZE + SLIP2];
    }

    public float getSlip2(int idx) {
        return floatArray[idx * FLOATBUF_CHUNK_SIZE + SLIP2];
    }

    public void setSlip2(float slip2) {
        floatArray[index * FLOATBUF_CHUNK_SIZE + SLIP2] = slip2;
    }
    public void setSlip2(float slip2, int idx) {
        floatArray[idx * FLOATBUF_CHUNK_SIZE + SLIP2] = slip2;
    }

}
