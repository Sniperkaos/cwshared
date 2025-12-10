package dev.cworldstar.cwshared.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;

/**
 * InventoryHistory allows for methods like {@link #goBack()}.
 * @author cworldstar
 *
 */	
public class InventoryHistory implements Iterable<BaseUIObject> {
	
	private ArrayList<BaseUIObject> history = new ArrayList<BaseUIObject>();
	
	@Getter
	private UUID owner;
	
	
	public InventoryHistory(Player owner) {
		this.owner = owner.getUniqueId();
	}

	public void addHistory(BaseUIObject object) {
		history.add(object);
	}

	public boolean isOriginal(BaseUIObject object) {
		return history.getFirst().equals(object);
	}
	
	public void goBack() {
		if(history.isEmpty()) return;
		history.getLast().open();
		if(history.size() <= 1) return;
		history.removeLast();
	}

	@Override
	public Iterator<BaseUIObject> iterator() {
		return history.iterator();
	}

	public void remove(BaseUIObject object) {
		history.remove(object);
	}

	public Collection<? extends BaseUIObject> getHistory() {
		return history;
	}
}
