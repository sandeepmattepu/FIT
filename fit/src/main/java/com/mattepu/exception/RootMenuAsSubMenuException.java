package com.mattepu.exception;

public class RootMenuAsSubMenuException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() 
	{
		return "Root menu cannot be added as sub menu";
	}
}
