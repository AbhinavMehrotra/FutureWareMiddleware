package org.ubhave.anticipatorymiddleware.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer {

	public static Object fromString( String s ) throws IOException, ClassNotFoundException {
		byte [] data = s.getBytes();
		ObjectInputStream ois = new ObjectInputStream( 
				new ByteArrayInputStream(data) );
		Object o  = ois.readObject();
		ois.close();
		return o;
	}

	public static String toString( Serializable o ) throws IOException {	    	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return new String(baos.toByteArray());
	}
}
