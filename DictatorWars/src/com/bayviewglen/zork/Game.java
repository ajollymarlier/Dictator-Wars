package com.bayviewglen.zork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class Game - the main class of the "Zork" game. meme Author: Michael Kolling
 * Version: 1.1 Date: March 2000
 * 
 * This class is the main class of the "Zork" application. Zork is a very
 * simple, text based adventure game. Users can walk around some scenery. That's
 * all. It should really be extended to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * routine.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 */

class Game {
	private Parser parser;
	private Room currentRoom;
	// This is a MASTER object that contains all of the rooms and is easily
	// accessible.
	// The key will be the name of the room -> no spaces (Use all caps and
	// underscore -> Great Room would have a key of GREAT_ROOM
	// In a hashmap keys are case sensitive.
	// masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the
	// Great Room (assuming you have one).
	private HashMap<String, Room> masterRoomMap;
	//initalizes the main character TODO change to modifiy for races and stuff, default stats
	Player player  = new Player(100,100,100);

	private void initRooms(String fileName) throws Exception {
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				// Read the Name
				String roomName = roomScanner.nextLine();
				room.setRoomName(roomName.split(":")[1].trim());
				// Read the Description
				String roomDescription = roomScanner.nextLine();
				room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				//Stores in string array of each than parses after
				String s1 = roomScanner.nextLine();
				String [] roomItemsString = s1.trim().split(":")[1].split(",");
				Item [] roomItems = new Item[roomItemsString.length];
				for (int i  =0;i< roomItems.length;i++){
					roomItems[i] = new Item (Integer.parseInt(roomItemsString[i].trim().split("-")[0]), roomItemsString[i].trim().split("-")[1]);
				}
				for (Item i : roomItems)
					room.addRoomItems(i);
				// Read enemies
				String[] enemies = roomScanner.nextLine().trim().split(":")[1].split(",");
				int counter = 0;
				for (String s : enemies) {
					//TODO this doesnt work right now because of the added boolean parameter in the enemy constructor
					String currentEnemyType = enemies[counter].trim().split("-")[0];
					String inRange = enemies[counter].trim().split("-")[1];
					if(currentEnemyType.equals("Grunt"))
						room.addRoomEnemy(new Grunt(1, 0, 0, "Grunt", inRange.equals("C")));
					else if(currentEnemyType.equals("MiniBoss"))
						room.addRoomEnemy(new MiniBoss(1, 0, 0, "MiniBoss",inRange.equals("C")));
					else if (currentEnemyType.equals("Boss"))
						room.addRoomEnemy(new Boss(1, 0, 0, "Boss", inRange.equals("C")));
					counter++;
				}
				
				// Read the Exits
				String roomExits = roomScanner.nextLine();
				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the
				// masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

				// Now we better set the exits.
			}

			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {
					// s = direction
					// value is the room.

					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
					roomTemp.setExit(s.trim().charAt(0), exitRoom);

				}

			}

			roomScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		try {
			initRooms("data/Rooms.dat");
			currentRoom = masterRoomMap.get("SHIP_ROOM");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new Parser();
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();

		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Zork!");
		System.out.println("Zork is a new, incredibly boring adventure game.");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.longDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command
	 * ends the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}

		String commandWord = command.getCommandWord();
		if (commandWord.equals("help"))
			printHelp();
		else if (commandWord.equals("go"))
			goRoom(command);
		else if (commandWord.equals("quit")) {
			if (command.hasSecondWord())
				System.out.println("Quit what?");
			else
				return true; // signal that we want to quit
		} else if (commandWord.equals("eat")) {
			System.out.println("Do you really think you should be eating at a time like this?");
		} else if (commandWord.equals("break")) {
			if (!command.hasSecondWord())
				System.out.println("What do you want to break");
			else
				System.out.println("The break command don't work yet");
		} else if (commandWord.equals("check")) {
			System.out.print("The items in the room are: ");
			for (int i = 0; i < currentRoom.getRoomItems().size(); i++) {
				if (i != currentRoom.getRoomItems().size() - 1)
					System.out.print(currentRoom.getRoomItems().get(i).getName() + ", ");
				else
					System.out.print(currentRoom.getRoomItems().get(i).getName());
			}
			System.out.println();

			// print out the enemies in the room
			System.out.print("The enemies in the room are: ");
			for (int i = 0; i < currentRoom.getRoomEnemies().size(); i++) {
				String message = "not ";
				if(currentRoom.getRoomEnemies().get(i).getInRange()) message = "";
				if (i != currentRoom.getRoomEnemies().size() - 1){
					System.out.print(currentRoom.getRoomEnemies().get(i) + " is " + message + "in range, ");
				}else{
					System.out.print(currentRoom.getRoomEnemies().get(i) + " is " + message + "in range.");
				}
			}
			System.out.println();
			// TODO right now is case sensitive, consider adding way to make it
			// non case sensitive
			// TODO also minor changes once an inventory is created to allow the
			// item to be removed from the room and added to the inventory
		} else if (commandWord.equals("grab")) {
			if (!command.hasSecondWord())
				System.out.println("What do you want to grab?");
			else if (currentRoom.getRoomItemNameIndex(currentRoom.getRoomItems(), command.getSecondWord()) ==-1)
				System.out.println("That item is not in the room");
			else {
				boolean works = player.pickUp(currentRoom.getRoomItems().remove(currentRoom.getRoomItemNameIndex(currentRoom.getRoomItems(), command.getSecondWord())), currentRoom);
				if(works)
					System.out.println("You obtianed: " + command.getSecondWord());
				else 
					System.out.println("You are already carrying too much");
			}
		}

		return false;
	}

	// implementations of user commands:

	/**
	 * Print out some help information. Here we print some stupid, cryptic
	 * message and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at Monash Uni, Peninsula Campus.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(direction);

		if (nextRoom == null)
			System.out.println("There is no door!");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.longDescription());
		}
	}

}