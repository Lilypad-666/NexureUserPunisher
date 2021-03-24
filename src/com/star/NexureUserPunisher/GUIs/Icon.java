package com.star.NexureUserPunisher.GUIs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Icon {

    private final ItemStack itemStack;
    public ItemStack getItemStack() {
    	return itemStack;
    }

    private final List<ClickAction> clickActions;
    public List<ClickAction> getClickActions() {
        return this.clickActions;
    }

    public Icon(ItemStack itemStack) {
        this.itemStack = itemStack;
        clickActions = new ArrayList<>();
    }

    public Icon addClickAction(ClickAction clickAction) {
        this.clickActions.add(clickAction);
        return this;
    }
}
 