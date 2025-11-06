package dev.cworldstar.cwshared.ui;

public abstract class SimpleMenuHandler<T> extends MenuHandler<T> {

	public SimpleMenuHandler() {
		super(null);
		consumer = this::handler;
	}
	
	public abstract void handler(T object);

}
