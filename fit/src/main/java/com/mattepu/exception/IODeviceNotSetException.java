package com.mattepu.exception;

public class IODeviceNotSetException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() 
	{
		return "IODevice not set for Menu to communicate with.";
	}
}
