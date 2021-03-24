package com.star.NexureUserPunisher.GUIs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Punishments.PunishmentList;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;

/**
 * Custom GUI base, can easily create GUIs off of it
 * @author Star
 *
 */
public abstract class CustomGUI implements InventoryHolder {

	//Reference to the list of users
	UserList userList;
	
	//Reference to the list of punishments
	PunishmentList punishmentList;

	//List of all icons on the GUI
	private Map<Integer, Icon> icons;

	//The number of columns to have the GUI display
    private final int columnCount;
    
    //The title of the GUI
    private final String title;
    
    //The user who opened the GUI
    protected User user;
    
    //Refernce to the GUI itself
    protected Inventory inventory;
    
    /**
     * Constructor to initialize variables
     * @param user
     * @param columnCount
     * @param title
     * @param main
     */
    public CustomGUI(User user, int columnCount, String title,  UserList userList, PunishmentList punishmentList) {
    	icons = new HashMap<>();
    	
    	this.user = user;
    	
    	this.userList = userList;
    	this.punishmentList = punishmentList;
    	
    	this.columnCount = columnCount;
    	this.title = title;
    }
    
    /**
     * Gets the icon at the given position
     * @param position
     * @return
     */
    public Icon getIcon(int position) {
        return icons.get(position);
    }
    
    /**
     * Sets an icon to the position
     * @param position
     * @param icon
     */
    public void setIcon(int position, Icon icon) {
        icons.put(position, icon);
        updateInventory();
    }
    
    /**
     * Creates an icon with the given material, name, and lore
     * @param material
     * @param name
     * @param lore
     * @return
     */
	protected Icon createIcon(Material material, String name, String... lore) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta itemMeta = itemStack.getItemMeta();
		
        itemMeta.setDisplayName(name);
		itemMeta.setLore(Arrays.asList(lore));
		
		itemStack.setItemMeta(itemMeta);
		
		return new Icon(itemStack);
	}
	
	/**
	 * Opens the GUI for the player
	 */
	public void openInventory() {
		inventory = getInventory();
		
		user.getBukkitPlayerInstance().openInventory(inventory);
	}
	
	/**
	 * Updates the inventory's items
	 */
	private void updateInventory() {
		if (inventory != null) {
			for (Entry<Integer, Icon> entry : icons.entrySet()) {
	        	int position = entry.getKey();
	        	Icon icon = entry.getValue();
	        	
	        	if (position < inventory.getSize()) {
	                inventory.setItem(position, icon.getItemStack());
	        	}
	        }
		}
	}

    @Override
    public Inventory getInventory() {
    	int size = columnCount * 9;
    	
        inventory = Bukkit.createInventory(this, size, title);

        for (Entry<Integer, Icon> entry : icons.entrySet()) {
        	int position = entry.getKey();
        	Icon icon = entry.getValue();
        	
        	if (position < inventory.getSize()) {
                inventory.setItem(position, icon.getItemStack());
        	}
        }
   
        return inventory;
    }
    
    protected <T> List<List<T>> splitList(List<T> list, int partitionSize) {
    	List<List<T>> partitions = new ArrayList<>();
    	
    	for (int i = 0; i < list.size(); i += partitionSize) {
    		partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
    	}
    	
    	return partitions;
    }
    
    protected abstract void createIcons();

}
