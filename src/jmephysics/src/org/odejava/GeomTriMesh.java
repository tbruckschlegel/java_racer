/*
 * Open Dynamics Engine for Java (odejava) Copyright (c) 2004, Jani Laakso, All
 * rights reserved. Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. Neither the name of the odejava nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.odejava;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import org.odejava.ode.Ode;
import org.odejava.ode.SWIGTYPE_p_dxTriMeshData;
import org.odejava.ode.SWIGTYPE_p_float;
import org.odejava.ode.SWIGTYPE_p_int;

import com.jme.scene.TriMesh;

/**
 * A triangle mesh (TriMesh) represents an arbitrary collection of triangles.
 * The triangle mesh collision system has the following features:
 * <p>
 * Any triangle soup can be represented i.e. the triangles are not required to
 * have any particular strip, fan or grid structure. Triangle meshes can
 * interact with spheres, boxes and rays. It works well for relatively large
 * triangles. It uses temporal coherence to speed up collision tests. When a
 * geom has its collision checked with a trimesh once, data is stored inside the
 * trimesh. This data can be cleared with the dGeomTriMeshClearTCCache function.
 * In the future it will be possible to disable this functionality.
 * <p>
 * Note: give index in such way that triangles are build clockwise (z is up).
 * Created 16.12.2003 (dd.mm.yyyy)
 *
 * <br> see http://odejava.dev.java.net
 *
 * @author Jani Laakso E-mail: jani.laakso@itmill.com
 *
 */
public class GeomTriMesh extends PlaceableGeom {

	/**
	 * Create and ode representation of the geometry from jme's own trimesh
	 * class.
	 * Regards current world scale.
     *
	 * @param name
	 *            An optional name to associate with this geometry
    * @param mesh graphical representation
	 */
	public GeomTriMesh(String name, TriMesh mesh) {
		super(name);

        IntBuffer indices = mesh.getIndexAsBuffer();
        indices.rewind();

        FloatBuffer vertices = mesh.getVerticeAsFloatBuffer();
        vertices.rewind();

        FloatBuffer normals = mesh.getNormalAsFloatBuffer();
        normals.rewind();

        // Create ODE vertices
        int verticesLength = mesh.getVertQuantity()*3;
        SWIGTYPE_p_float tmpVertices = Odejava.createSwigFloatArray( verticesLength );
        vertices.rewind();
        float[] scale = { mesh.getWorldScale().x, mesh.getWorldScale().y, mesh.getWorldScale().z };
        for (int i = 0; i < verticesLength; i++)
        {
            Ode.floatArray_setitem(tmpVertices, i, vertices.get()*scale[i%3] );
        }

        // Create ODE indices
        int indicesLength = mesh.getTriangleQuantity() * 3;
        SWIGTYPE_p_int tmpIndices = Odejava.createSwigArray(indices, indicesLength );

		// Create ODE TriMeshData
		SWIGTYPE_p_dxTriMeshData data = Ode.dGeomTriMeshDataCreate();

		// Build ODE TriMesh
		// Ode.dGeomTri

        SWIGTYPE_p_float tmpNormals = Odejava.createSwigArray(normals, verticesLength);
        Ode.dGeomTriMeshDataBuildSingle1(data, tmpVertices, 12,
                verticesLength/3, tmpIndices, indicesLength, 12, tmpNormals);

        spaceId = Ode.getPARENTSPACEID_ZERO();

		// Create ODE TriMesh
		geomId = Ode.dCreateTriMesh(spaceId, data, null, null, null);

		retrieveNativeAddr();
	}
}
