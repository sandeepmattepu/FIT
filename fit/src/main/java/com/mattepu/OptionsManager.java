package com.mattepu;

import java.util.ArrayList;
import java.util.List;

import com.mattepu.exception.InvalidIndexException;

public class OptionsManager 
{	
	private List<String> optionNames;
	private List<IOption> optionActions;
	
	public OptionsManager()
	{
		optionNames = new ArrayList<String>();
		optionActions = new ArrayList<IOption>();
	}
	
	public int addOption(String optionName, IOption option, int atIndex)
	{
		int addedAtIndex = -1;
		if(optionName == null)
		{
			optionName = "";
		}
		if(atIndex < 0 || (atIndex >= optionNames.size()))
		{
			optionNames.add(optionName);
			optionActions.add(option);
			addedAtIndex = optionNames.size() - 1;
		}
		else
		{
			optionNames.add(atIndex, optionName);
			optionActions.add(atIndex, option);
			addedAtIndex = atIndex;
		}
		return addedAtIndex;
	}
	
	public int addOption(String optionName, IOption option)
	{
		return addOption(optionName, option, optionNames.size());
	}
	
	public void removeOption(int atIndex) throws InvalidIndexException
	{
		if(atIndex < 0 || (atIndex >= optionNames.size()))
		{
			throw new InvalidIndexException(atIndex);
		}
		else
		{
			optionNames.remove(atIndex);
			optionActions.remove(atIndex);
		}
	}
	
	public IOption getOptionAt(int index) throws InvalidIndexException
	{
		if(index < 0 || (index >= optionNames.size()))
		{
			throw new InvalidIndexException(index);
		}
		else
		{
			return optionActions.get(index);
		}
	}
	
	@Override
	public String toString()
	{
		String result = "";
		for(int i = 0; i < optionNames.size(); i++)
		{
			result += Integer.toString((i + 1)) + " : " + optionNames.get(i);
			if(i != (optionNames.size() - 1))
			{
				result += "\n";
			}
		}
		return result;
	}
}
