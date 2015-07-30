package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragContainer implements Serializable {
	public static final DataFormat DATA_FORMAT = new DataFormat(AcceptanceCriteria.class.getPackage().getName() + ".dataformat");
	private static final long serialVersionUID = -1890998765646621338L;
	private final List <Pair<String, Object> > dataPairs = new ArrayList <Pair<String, Object> > ();
	
	public void addData (String key, Object value) {
		dataPairs.add(new Pair<String, Object>(key, value));		
	}
	
	@SuppressWarnings("unchecked")
    public <T> T getValue (String key) {
		
		for (Pair<String, Object> data: dataPairs) {
			
			if (data.getKey().equals(key))
				return (T) data.getValue();
				
		}
		
		return null;
	}
	
	public List <Pair<String, Object> > getData () { 
	    return dataPairs; 
	}	
}
