package com.mattepu.exception;

public class DuplicateRootMenuException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() 
	{
		return "Cannot have duplicate root menu in menu structure";
	}
}
