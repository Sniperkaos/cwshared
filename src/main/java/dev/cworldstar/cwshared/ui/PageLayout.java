package dev.cworldstar.cwshared.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNullableByDefault;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import dev.cworldstar.cwshared.builders.ItemStackBuilder;
import dev.cworldstar.cwshared.builders.PlayerHeadBuilder;
import dev.cworldstar.cwshared.events.TickerTickEvent;

public class PageLayout {

	private Map<Integer, ArrayList<MenuHandler<InventoryClickEvent>>> shiftHandlers = new HashMap<Integer, ArrayList<MenuHandler<InventoryClickEvent>>>();
	private Map<Integer, ArrayList<MenuHandler<InventoryClickEvent>>> handlers = new HashMap<Integer, ArrayList<MenuHandler<InventoryClickEvent>>>();
	private Map<Integer, ArrayList<MenuHandler<TickerTickEvent>>> tickHandlers = new HashMap<Integer, ArrayList<MenuHandler<TickerTickEvent>>>();

	private Map<Integer, ItemStack> layout = new HashMap<Integer, ItemStack>();
	private List<String> meta = new ArrayList<String>();
	
	private PagedUIObject parent;
	private Inventory inventory;
	
	private ItemStack barrierItem = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).empty().build();
	private ItemStack closeItem = new PlayerHeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZmZDA2OWE3YTBlYjhlMTQ5YWU3NjM1M2M1MGZjNjM4MzI5ZDI2NjI2MDgyNGFiMTFjMTY4MzEzZjViMGI4In19fQ==").build();
	private ItemStack rightItem = new PlayerHeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkxYWM0MzJhYTQwZDdlN2E2ODdhYTg1MDQxZGU2MzY3MTJkNGYwMjI2MzJkZDUzNTZjODgwNTIxYWYyNzIzYSJ9fX0=").build();
	private ItemStack leftItem = new PlayerHeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EyYzEyY2IyMjkxODM4NGUwYTgxYzgyYTFlZDk5YWViZGNlOTRiMmVjMjc1NDgwMDk3MjMxOWI1NzkwMGFmYiJ9fX0=").build();
	
	
	private boolean active = false;
	
	private int rightSlot;
	private int leftSlot;
	private int closeSlot;
	private int page = -1;
	
	private List<Integer> barrier_slots = new ArrayList<Integer>();
	
	/**
	 * 
	 * @apiNote This method should only be called ONCE per PageLayout, and never
	 * called on a cloned layout with its slots set.
	 * 
	 * @param right_slot
	 * @param left_slot
	 * @param close_slot
	 */
	@ParametersAreNullableByDefault
	public void setSlots(@Nullable Integer right_slot, @Nullable Integer left_slot, @Nullable Integer close_slot) {		
		if(right_slot != null) {
			rightSlot = right_slot;
			addMenuClickHandler(rightSlot, new MenuHandler<InventoryClickEvent>((InventoryClickEvent e) -> {
				e.setCancelled(true);
				parent.nextPage();
			}));
		}
		
		if(left_slot != null) {
			leftSlot = right_slot;
			addMenuClickHandler(left_slot, new MenuHandler<InventoryClickEvent>((InventoryClickEvent e) -> {
				e.setCancelled(true);
				parent.previousPage();
			}));
		}
		
		
		if(close_slot != null) {
			closeSlot = close_slot;
			addMenuClickHandler(close_slot, new MenuHandler<InventoryClickEvent>((InventoryClickEvent e) -> {
				e.setCancelled(true);
				parent.close();
			}));
		}
	}
	
	public @Nullable Integer getRightSlot() {
		return this.rightSlot;
	}
	
	public @Nullable Integer getLeftSlot() {
		return leftSlot;
	}
	
	public @Nullable Integer getCloseSlot() {
		return closeSlot;
	}
	
	public void setInventory(Inventory i) {
		this.inventory = i;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public void addBarriers(List<Integer> barriers) {
		this.barrier_slots.addAll(barriers);
	}
	
	public void setCloseItem(ItemStack i) {
		this.closeItem = i;
	}
	
	public void setRightItem(ItemStack i) {
		this.rightItem = i;
	}
	
	public void setLeftItem(ItemStack i) {
		this.leftItem = i;
	}
	
	
	public void setBarrierItem(ItemStack i) {
		this.barrierItem = i;
	}
	
	public void addHandler(int slot, MenuHandler<InventoryClickEvent> handler) {
		handlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		
		ArrayList<MenuHandler<InventoryClickEvent>> handler_list = handlers.get(slot);
		handler_list.add(handler);
		
		handlers.put(slot, handler_list);
	}
	
	public void addTicker(int slot, MenuHandler<TickerTickEvent> handler) {
		tickHandlers.putIfAbsent(slot, new ArrayList<MenuHandler<TickerTickEvent>>());
		ArrayList<MenuHandler<TickerTickEvent>> handler_list = tickHandlers.get(slot);
		handler_list.add(handler);
		
		tickHandlers.put(slot, handler_list);
	}
	
	public void addHandlers(int slot, ArrayList<MenuHandler<InventoryClickEvent>> list) {
		handlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		handlers.get(slot).addAll(list);
	}
	
	public void addShiftHandler(int slot, MenuHandler<InventoryClickEvent> handler) {
		shiftHandlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		shiftHandlers.get(slot).add(handler);
	}
	
	public MenuHandler<?>[] getHandlers(int slot) {
		return handlers.get(slot).toArray(new MenuHandler<?>[0]);
	}
	
	public PageLayout clone() {
		PageLayout clone =  new PageLayout(this.parent);
		clone.setBarrierItem(barrierItem);
		clone.setCloseItem(closeItem);
		clone.setLeftItem(leftItem);
		clone.setRightItem(rightItem);
		clone.setSlots(rightSlot, leftSlot, closeSlot);
		clone.addBarriers(barrier_slots);
		this.handlers.forEach((Integer slot, ArrayList<MenuHandler<InventoryClickEvent>> events) -> {
			clone.addHandlers(slot, events);
		});

		return clone;
	}
	
	public void setParent(PagedUIObject parent) {
		this.parent = parent;
	}
	
	public PagedUIObject getParent() {
		return this.parent;
	}
	
	public void click(InventoryClickEvent e, int slot) {
		this.handlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		this.handlers.get(slot).forEach((MenuHandler<InventoryClickEvent> event) -> {
			event.run(e);
		});
		
		if(this.barrier_slots.contains(slot)) {
			e.setCancelled(true);
		}
	}
	
	public void shiftClick(InventoryClickEvent e, int slot) {
		this.shiftHandlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		if(e.getClickedInventory().equals(parent.getInventory())) {
			this.shiftHandlers.get(slot).forEach((MenuHandler<InventoryClickEvent> event) -> {
				event.run(e);
			});
		}
	}
	
	public void tick(TickerTickEvent e) {
		this.tickHandlers.forEach((slot, handlerList) -> {
			handlerList.forEach((ticker) -> {
				ticker.run(e);
			});
		});
	}
	
	public PageLayout() {}
	
	public PageLayout(PagedUIObject parent) {
		this.parent = parent;
	}

	/**
	 * Must be overwritten in the UI object that
	 * extends PagedUIObject. This one, however, should never
	 * be overwritten.
	 * 
	 * @param ui
	 */
	public void decorate(PagedUIObject ui) {
		
		this.inventory = ui.getInventory();
		this.active = true;
		
		layout.forEach((Integer slot, ItemStack i) -> {
			ui.setItem(slot, i);
		});
		
		this.barrier_slots.forEach((Integer slot) -> {
			ui.setItem(slot, barrierItem);
		});
		
		ui.setItem(leftSlot, leftItem);
		ui.setItem(rightSlot, rightItem);
		ui.setItem(closeSlot, closeItem);
		
	}
	
	public void clear() {
		this.active = false;
		this.inventory.clear();
		this.inventory = null;
	}

	public void addUnclickableItem(int slot, ItemStack item) {
		// stop null 
		handlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		
		// get handler list
		ArrayList<MenuHandler<InventoryClickEvent>> handler_list = handlers.get(slot);
		handler_list.add(new MenuHandler<InventoryClickEvent>((InventoryClickEvent e) -> {
			e.setCancelled(true);
		}));
		
		//add handlers
		handlers.put(slot, handler_list);
		
		// set inventory item
		layout.put(slot, item);
	}
	
	public void addItem(int slot, ItemStack item) {
		layout.put(slot, item);
	}

	public void addMenuClickHandler(int slot, MenuHandler<InventoryClickEvent> menuHandler) {
		this.addHandler(slot, menuHandler);
	}

	public void setItem(int slot, ItemStack item) {
		addItem(slot, item);
	}

	/**
	 * 
	 * @return {@link Integer} The first empty slot, or -1 if full.
	 */
	public int firstEmpty() {
		for(int i=0; i<parent.getInventory().getSize(); i++) {
			if(layout.get(i) == null && 
					!barrier_slots.contains(i) &&
					rightSlot != i &&
					leftSlot != i &&
					closeSlot != i
			) {
				return i;
			}
		}
		return -1;
	}

	public ItemStack getItem(int slot) {
		return this.layout.get(slot);
	}

	public boolean hasMeta(String meta) {
		return meta.contains(meta);
	}

	public void addMeta(String meta) {
		this.meta.add(meta);
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return this.page;
	}

	public boolean isActive() {
		return active;
	}
	
}
