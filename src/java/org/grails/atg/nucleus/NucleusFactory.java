package org.grails.atg.nucleus;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.request.RequestContextHolder;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * FactoryBean for bringing Nucleus components into Spring.
 * 
 * Note that all Nucleus beans must be request scoped in Spring,
 * regardless of their scope in Nucleus.
 * 
 * @author noah
 *
 */
public class NucleusFactory implements FactoryBean<Object> {

	private static GrailsWebRequest getGWR() {
		return ((GrailsWebRequest)RequestContextHolder.currentRequestAttributes());
	}
	
	private String componentName;
	
	private Class<?> componentType = Object.class;
	
	public NucleusFactory() {
		this(null);
	}
	
	public NucleusFactory(String componentName) {
		super();
		this.componentName = componentName;
	}

	public NucleusFactory(String componentName, Class<?> componentType) {
		super();
		this.componentName = componentName;
		this.componentType = componentType;
	}

	public void setComponent(String componentName) {
		this.componentName = componentName;
	}
	
	public void setComponentName(String componentName) {
		setComponent(componentName);
	}
	
	public void setComponentType(Class<?> componentType) {
		this.componentType = componentType;
	}
	
	public void setType(Class<?> componentType) {
		setComponentType(componentType);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param name
	 * @param expectedClass
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	static <T> T getComponent(String name, Class<T> expectedClass) throws IOException, ServletException {
		GrailsWebRequest gwr = getGWR();
		ServletContext context = gwr.getServletContext();
		ServletRequest request = gwr.getCurrentRequest();
		ServletResponse response= gwr.getCurrentResponse();
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getDynamoRequest(context, request, response);
		
		return expectedClass.cast(dynamoRequest.resolveName(name));
	}

	@Override
	public Object getObject() throws IOException, ServletException {
		return getComponent(componentName,componentType);	
	}

	@Override
	public Class<?> getObjectType() {
		return componentType;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
