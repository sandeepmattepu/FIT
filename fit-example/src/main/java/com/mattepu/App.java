package com.mattepu;

public class App 
{
    public static void main( String[] args )
    {
    	IOption backOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() {
				return OptionActionResult.GO_UP;
			}
		};
		IOption homeOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() {
				return OptionActionResult.RETURN_HOME;
			}
		};
		IOption exitOption = new IOption() {
			@Override
			public OptionActionResult optionSelected() {
				return OptionActionResult.EXIT_MENU;
			}
		};
		IOption simWork = new IOption() {
			@Override
			public OptionActionResult optionSelected() {
				System.out.println("Processing started ...");
				try 
				{
					Thread.sleep(5000);
					System.out.println("Processing finished");
				} catch (InterruptedException e) 
				{
					System.out.println("Processing failed.");;
				}
				return OptionActionResult.SUCCESS;
			}
		};
		
    	Menu mainMenu = new Menu("Home", true);
    	mainMenu.addOption("Back", backOption);
    	mainMenu.addOption("Return Home", homeOption);
    	mainMenu.addOption("Exit", exitOption);
    	mainMenu.addOption("Null option", null);
    	mainMenu.addSubMenu("Empty sub menu", null, -1);
    	mainMenu.addOption("Some process", simWork);
    	Menu wifiMenu = new Menu("Wifi");
    	mainMenu.addSubMenu(wifiMenu);
    	wifiMenu.addOption("Back", backOption);
    	wifiMenu.addOption("Return Home", homeOption);
    	wifiMenu.addOption("Exit", exitOption);
    	Menu networkMenu = new Menu("Network");
    	wifiMenu.addSubMenu(networkMenu);
    	networkMenu.addOption("Back", backOption);
    	networkMenu.addOption("Return Home", homeOption);
    	networkMenu.addOption("Exit", exitOption);
    	networkMenu.addOption("Some process", simWork, 1);
    	mainMenu.setIODevice(new InputOutputDevice());
    	mainMenu.start();
    }
}
