package com.mattepu;

import com.mattepu.exception.ActionOnlyForRootMenuException;
import com.mattepu.exception.IODeviceNotSetException;
import com.mattepu.exception.InvalidIndexException;
import com.mattepu.exception.RootMenuAsSubMenuException;
import com.mattepu.exception.SubMenuDoesNotExistException;

public class Menu implements IOption
{
	private String menuName = "";
	private boolean isRootMenu = false;
	private String header = null;
	private String footer = null;
	private OptionsManager optionsManager = new OptionsManager();
	private IODevice ioDevice = null;
	private NavigationPanel navigation = null;
	
	public Menu(String name, boolean isRoot)
	{
		if(name != null)
		{
			menuName = name;
		}
		isRootMenu = isRoot;
		if(isRoot)
		{
			navigation = new NavigationPanel();
			navigation.setRootMenu(name);
		}
	}
	
	public Menu(String name)
	{
		this(name, false);
	}
	
	public void setHeader(String header)
	{
		if(this.header == null)
		{
			if(header != null && !header.equals(""))
			{
				this.header = header;
			}
		}
		else 
		{
			header = header.equals("") ? null : header;
			this.header = header;
		}
	}
	
	public int addOption(String optionName, IOption option, int atIndex)
	{
		return optionsManager.addOption(optionName, option, atIndex);
	}
	
	public int addOption(String optionName, IOption option)
	{
		return optionsManager.addOption(optionName, option);
	}
	
	public int addSubMenu(String menuName, Menu subMenu, int atIndex) throws RootMenuAsSubMenuException
	{
		if(subMenu == null)
		{
			return optionsManager.addOption(menuName, subMenu, atIndex);
		}
		else if(subMenu.isRootMenu)
		{
			throw new RootMenuAsSubMenuException();
		}
		else
		{
			return optionsManager.addOption(menuName, subMenu, atIndex);
		}
	}
	
	public int addSubMenu(Menu subMenu)
	{
		return addSubMenu(subMenu, -1);
	}
	
	public int addSubMenu(Menu subMenu, int atIndex)
	{
		String subMenuName = "";
		if(subMenu != null)
		{
			subMenuName = subMenu.menuName;
		}
		return addSubMenu(subMenuName, subMenu, atIndex);
	}
	
	public Menu getSubMenu(int atIndex) throws SubMenuDoesNotExistException
	{
		IOption option = optionsManager.getOptionAt(atIndex);
		if(!(option instanceof Menu))
		{
			throw new SubMenuDoesNotExistException();
		}
		return (Menu)option;
	}
	
	public boolean removeOptionOrSubMenu(int at)
	{
		try
		{
			optionsManager.removeOption(at);
			return true;
		}
		catch(InvalidIndexException e)
		{
			return false;
		}
	}
	
	public void setFooter(String footer)
	{
		if(this.footer == null)
		{
			if(footer != null && !footer.equals(""))
			{
				this.footer = footer;
			}
		}
		else 
		{
			footer = footer.equals("") ? null : footer;
			this.footer = footer;
		}
	}
	
	public void setIODevice(IODevice ioDevice)
	{
		this.ioDevice = ioDevice;
	}
	
	public void start() throws ActionOnlyForRootMenuException, IODeviceNotSetException
	{
		if(isRootMenu)
		{
			if(ioDevice == null)
			{
				throw new IODeviceNotSetException();
			}
			optionSelected();
		}
		else
		{
			throw new ActionOnlyForRootMenuException();
		}
	}
	
	public boolean isRoot()
	{
		return isRootMenu;
	}

	@Override
	public OptionActionResult optionSelected() 
	{
		while(true)
		{
			ioDevice.clearDisplay();
			
			String toDisplay = "";
			
			toDisplay += navigation.toString();
			toDisplay += nonEmptyHeader();
			boolean hasAtleastOneOption = hasAtleastOneOption();
			if(hasAtleastOneOption)
			{
				toDisplay += "\n" + optionsManager.toString();
			}
			toDisplay += nonEmptyFooter();
			
			if(hasAtleastOneOption)
			{
				toDisplay += "\n" + "Enter your choice : ";
				ioDevice.display(toDisplay);
				String inputString = ioDevice.acceptInput();
				if(isValidInputString(inputString))
				{
					IOption option = optionsManager.getOptionAt(Integer.parseInt(inputString) - 1);
					if(option == null)
					{
						continue;
					}
					else if(option instanceof Menu)
					{
						Menu menuAsOption = (Menu)option;
						menuAsOption.setIODevice(ioDevice);
						navigation.changeToSubMenu(menuAsOption.menuName);
						menuAsOption.navigation = navigation;
					}
					OptionActionResult result = option.optionSelected();
					if(result == OptionActionResult.SUCCESS)
					{
						continue;
					}
					else if(result == OptionActionResult.EXIT_MENU)
					{
						if(isRootMenu)
						{
							ioDevice.clearDisplay();
						}
						return result;
					}
					else if(result == OptionActionResult.GO_UP)
					{
						if(isRootMenu)
						{
							continue;
						}
						navigation.goUpMenu();
						return OptionActionResult.SUCCESS;
					}
					else if(result == OptionActionResult.RETURN_HOME)
					{
						if(isRootMenu)
						{
							continue;
						}
						navigation.goToRootMenu();
						return result;
					}
				}
				else
				{
					continue;
				}
			}
			else
			{
				ioDevice.display(toDisplay);
				return OptionActionResult.SUCCESS;
			}
			
		}
	}
	
	private String nonEmptyHeader()
	{
		if(header != null && !header.equals(""))
		{
			return "\n" + header;
		}
		return "";
	}
	
	private String nonEmptyFooter()
	{
		if(footer != null && !footer.equals(""))
		{
			return "\n" + footer;
		}
		return "";
	}
	
	private boolean hasAtleastOneOption()
	{
		try
		{
			optionsManager.getOptionAt(0);
			return true;
		}
		catch (InvalidIndexException e) 
		{
			return false;
		}
	}
	
	private boolean isValidInputString(String inputString)
	{
		try
		{
			int inputValue = Integer.parseInt(inputString);
			optionsManager.getOptionAt(inputValue - 1);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
