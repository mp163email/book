package com.mp.design.mediator.sample;

public class Colleague {
	
	private Mediator mediator;
	
	public Colleague(Mediator mediator){
		this.mediator = mediator;
	}
	
	public Mediator getMediator(){
		return mediator;
	}
}
