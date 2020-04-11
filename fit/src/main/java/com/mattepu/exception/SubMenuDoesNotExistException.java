package com.mattepu.exception;

public class SubMenuDoesNotExistException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() 
	{
		return "Sub menu does not exist";
	}
}
