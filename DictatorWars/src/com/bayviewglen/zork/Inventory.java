package com.bayviewglen.zork;

import java.util.ArrayList;
import java.util.Arrays;

public class Inventory {
	private final int NUM_OF_INVENTORIES = 5;
	public ArrayList[] allInventories;
	public ArrayList<Melee> meleeInventory;
	public ArrayList<Ranged> rangedInventory;
	public ArrayList<Key> keyInventory;
	public ArrayList<EquippableItem> equippableInventory;

	public ArrayList<Item> otherInventory;

	public Inventory() {
		// <? extends Item> allows for an ArrayList to use The Item Class as its
		// storing reference

		meleeInventory = new ArrayList<Melee>();
		rangedInventory = new ArrayList<Ranged>();
		keyInventory = new ArrayList<Key>();
		otherInventory = new ArrayList<Item>();
		allInventories = new ArrayList[NUM_OF_INVENTORIES];
		allInventories[0] = meleeInventory;
		allInventories[1] = rangedInventory;
		allInventories[2] = keyInventory;
		allInventories[3] = equippableInventory;
		allInventories[4] = otherInventory;
		

	}

	public void add(Melee item) {
		meleeInventory.add(item);

	}

	public void add(Ranged item) {
		rangedInventory.add(item);
	}

	public int findType(Item item) {
		if (item instanceof Melee) {
			return 0;
		} else if (item instanceof Ranged) {
			return 1;
		} else if (item instanceof Key) {
			return 2;
		} else if (item instanceof EquippableItem) {
			return 3;
		} else  {
			return 4;
		}
	}

	public int findType(String name) {
		if (Arrays.asList(Melee.MELEE).indexOf(name) != -1) {
			return 0;
		} else if (Arrays.asList(Ranged.RANGED).indexOf(name) != -1) {
			return 1;
		} else if (Arrays.asList(Key.KEY).indexOf(name) != -1) {
			return 2;
		} else if (Arrays.asList(EquippableItem.EQUIPPABLE).indexOf(name) != -1) {
			return 3;
		} else {
			return 4;
		}
	}

	// finds the type of the item and adds it to the corresponding array
	public void addItem(Item item) {
		allInventories[findType(item)].add(item);
	}

	public Item removeItem(String name) {
		int inventoryIndex = findType(name);
		int removingIndex = findIndexSpecific(allInventories[inventoryIndex], name);
		return (Item) allInventories[inventoryIndex].remove(removingIndex);
	}

	public Item getItem(String name) {
		int inventoryIndex = findType(name);
		int index = findIndexSpecific(allInventories[inventoryIndex], name);
		return (Item) allInventories[inventoryIndex].get(index);
	}

	public boolean isInInventory(String name) {

		for (int i = 0; i < allInventories.length; i++) {
			if (findIndexSpecific(allInventories[i], name) != -1) {
				return true;
			}
		}
		return false;
	}

	public int findIndexSpecific(ArrayList<? extends Item> current, String name) {
		for (int i = 0; i < current.size(); i++) {
			if (current.get(i).equals(name)) {
				return i;
			}
		}
		return -1;
	}

	public void displayAll() {
		if (meleeInventory.size() != 0) {
			System.out.print("Melee Weapons: ");
			displaySpecific(meleeInventory);
		}
		if (rangedInventory.size() != 0) {
			System.out.print("Ranged Weapons: ");
			displaySpecific(rangedInventory);
		}
		if (keyInventory.size() != 0) {
			System.out.print("Key Items: ");
			displaySpecific(keyInventory);
		}
		if (equippableInventory.size() != 0) {
			System.out.print("Equippable Items: ");
			displaySpecific(equippableInventory);
		}
		if (otherInventory.size() != 0) {
			System.out.print("Miscellaneous Items: ");
			displaySpecific(otherInventory);
		}

	}

	public void displaySpecific(ArrayList<? extends Item> current) {
		for (int i = 0; i < current.size(); i++) {
			if (i == current.size() - 1) {
				System.out.println(current.get(i).getName() + ".");
			} else {
				System.out.print(current.get(i).getName() + ", ");
			}
		}
	}

	// TODO not done so it is commented out
	// used to check inventory to see if something has been used or not and
	// remove it TODO IMPLEMENT WITH ITEMS
	// need to run each time item used to check if something has been used
	/*
	 * public void checkInventoryUsed() { for (int i = 0; i < inventory.size();
	 * i++) { if (inventory.get(i) instanceof ConsumableItem) if
	 * (((ConsumableItem) inventory.get(i)).getUsed()) { inventory.remove(i);
	 * i--; } } }
	 */
	
	// this is to remove used keys, convoluted a bit because different
	// inventories
	public void checkKeyInventoryUsed() {
		for (int i = 0; i < keyInventory.size(); i++) {
			if (keyInventory.get(i).getUsed()) {
				keyInventory.remove(i);
				i--;
			}
		}
	}

	//this is me being lazy, stop being lazy fool
	public Key getKey(String name) {
		return keyInventory.get(findIndexSpecific(keyInventory, name));
	}

}
