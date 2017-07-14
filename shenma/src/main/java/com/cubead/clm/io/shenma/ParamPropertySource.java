package com.cubead.clm.io.shenma;

import org.springframework.core.env.PropertySource;

import com.cubead.clm.IProcessor;

public class ParamPropertySource extends PropertySource<String> {
	private IProcessor<String, Object> properites;
	
    public ParamPropertySource(IProcessor<String, Object> properties) {
    	super("custom");
    	this.properites = properties;
    }

    @Override
    public String getProperty(String name) {
    	Object result = properites.process(name);
    	return result == null ? null : result.toString();
    }
}