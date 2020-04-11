package com.mattepu.exception;

public class ActionOnlyForRootMenuException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() 
	{
		return "This action is only allowed by root menu.";
	}
}
