package org.ubhave.anticipatorymiddleware.subscribe;

import java.io.Serializable;

public class ClassReferenceHelper implements Serializable
{

	private static final long serialVersionUID = -6386028344535966699L;


	protected ClassReferenceHelper()
	{
		//DO NOTHING
	}
	
	
	public String getClassCanonicalName(Object object)
	{
		return object.getClass().getCanonicalName();
	}
	
	
	public Object getClassReference(String canonical_name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Class<?> c = Class.forName(canonical_name);
		return c.newInstance();
	}
	
}
