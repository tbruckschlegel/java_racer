package com.jmex.model.XMLparser;

import com.jme.scene.Spatial;
import com.jme.scene.Node;
import com.jme.scene.Controller;
import com.jme.scene.state.RenderState;

/**
 * Started Date: Jun 5, 2004
 * Dummy class only for use with jME's file system.
 * 
 * @author Jack Lindamood
 */
class XMLSharedNode extends Node {
    private static final long serialVersionUID = 1L;
	String myIdent;
    Object whatIReallyAm;
    XMLSharedNode(String ident){
        super();
        myIdent=ident;
    }
    public int attachChild(Spatial c){
        whatIReallyAm=c;
        return 0;
    }

    public RenderState setRenderState(RenderState r){
        whatIReallyAm=r;
        return null;
    }

    
    @Override
    public void addController(Controller c){
        whatIReallyAm=c;
    }
}