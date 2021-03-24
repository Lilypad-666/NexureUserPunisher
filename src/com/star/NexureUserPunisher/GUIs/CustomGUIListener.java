package com.star.NexureUserPunisher.GUIs;

import org.bukkit.Material;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.star.NexureUserPunisher.Main;
import com.star.NexureUserPunisher.Users.User;
import com.star.NexureUserPunisher.Users.UserList;

public class CustomGUIListener implements Listener {
	
	private UserList userList;
	
	public CustomGUIListener(UserList userList) {
		this.userList = userList;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
	    //Check if the inventory is custom
	    if (event.getView().getTopInventory().getHolder() instanceof CustomGUI) {
	        //Cancel the event
	        event.setCancelled(true);

	        //Check if who clicked is a User
	        if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
		    	User senderUser = userList.getUser(event.getWhoClicked().getUniqueId());

	            //Check if the item the user clicked on is valid
	            ItemStack itemStack = event.getCurrentItem();
	            if (itemStack == null || itemStack.getType() == Material.AIR) return;

	            //Get our CustomHolder
	            CustomGUI customHolder = (CustomGUI) event.getView().getTopInventory().getHolder();

	            //Check if the clicked slot is any icon
	            Icon icon = customHolder.getIcon(event.getRawSlot());
	            
	            if (icon == null) {
	            	return;
	            }

	            //Execute all the actions
	            for (ClickAction clickAction : icon.getClickActions()) {
	                clickAction.execute(senderUser);
	            }
	        }
	    }
	}
}
