package com.mattepu.exception;

public class InvalidIndexException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;
	private int index = 0;

	public InvalidIndexException(int indexProvided)
	{
		index = indexProvided;
	}
	
	@Override
	public String toString() 
	{
		return "Index provided " + Integer.toString(index) + " is invalid.";
	}
}
