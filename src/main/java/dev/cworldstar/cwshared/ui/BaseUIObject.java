package dev.cworldstar.cwshared.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import dev.cworldstar.cwshared.events.TickerTickEvent;
import dev.cworldstar.cwshared.utils.FormatUtils;
import lombok.Getter;
import lombok.Setter;

public abstract class BaseUIObject implements Listener {
	
	public static List<BaseUIObject> openUIObjects = new ArrayList<BaseUIObject>();
	
	public static enum InventorySize {

		/**
		 * Flexes inventory size based on contents of inventory.
		 */
		FLEX(-1),
		/**
		 * 1 row
		 */
		TINY(9),
		/**
		 * 2 rows
		 */
		SMALL(18),
		/**
		 * 3 rows
		 */
		MEDIUM(27),
		/**
		 * 4 rows
		 */
		MEDIUM_LARGE(36),
		/**
		 * 5 rows
		 */
		LARGE(45),
		/**
		 * 6 rows
		 */
		EXTRA_LARGE(54);

		protected final int size;
		private InventorySize(int i) {
			this.size = i;
		}
		
		/**
		 * The inventory slot size associated with this enum
		 * @return int 
		 * 
		 */
		public int toInt() {
			return this.size;
		}
		
		public static @NotNull InventorySize getSize(int size) {
			for(InventorySize inventorySize : InventorySize.values()) {
				if(inventorySize.toInt() >= size) {
					return inventorySize;
				}
			}
			return InventorySize.EXTRA_LARGE;
		}
		
		
			
	}
	
	protected boolean okay_to_close = true;
	protected boolean bypass_close = false;
	private Player owner;
	@Setter
	@Getter
	private ItemStack backgroundItem;
	private JavaPlugin plugin;
	@Setter
	@Getter
	private int[] backgroundSlots;
	private Map<Integer, ArrayList<MenuHandler<InventoryClickEvent>>> handlers = new HashMap<Integer, ArrayList<MenuHandler<InventoryClickEvent>>>();
	private Map<Integer, MenuHandler<InventoryClickEvent>> insertHandlers = new HashMap<Integer, MenuHandler<InventoryClickEvent>>();
	private ArrayList<MenuHandler<InventoryCloseEvent>> inventory_close_handlers = new ArrayList<MenuHandler<InventoryCloseEvent>>();
	private ArrayList<MenuHandler<InventoryClickEvent>> empty_click_handlers = new ArrayList<MenuHandler<InventoryClickEvent>>();
	private ArrayList<MenuHandler<InventoryClickEvent>> shift_click_handlers = new ArrayList<MenuHandler<InventoryClickEvent>>();

	private ArrayList<Integer> placeable_slots = new ArrayList<Integer>();
	private ArrayList<MenuHandler<TickerTickEvent>> tickers = new ArrayList<MenuHandler<TickerTickEvent>>();
	private Inventory inventory;
	private InventoryView view;
	
	/**
	 * 
	 * Base object for creating UI.
	 * 
	 * @author cworldstar
	 * 
	 * @param {Player} player
	 * @param {InventorySize} size
	 * 
	 */
	
	public BaseUIObject(JavaPlugin plugin, Player player, InventorySize size) {
		this(plugin, player, size, "");
	}
	
	public BaseUIObject(JavaPlugin plugin, Player player, InventorySize size, String title) {
		this.owner = player;
		this.inventory = Bukkit.createInventory(player, size.toInt(), FormatUtils.mm(title));
		this.decorate(inventory);
		if(backgroundItem != null && backgroundSlots != null) {
			for(int slot : getBackgroundSlots()) {
				addUnclickableItem(slot, backgroundItem);
			}
		}
		Bukkit.getPluginManager().registerEvents(this, plugin);
		BaseUIObject.openUIObjects.add(this);
	}
	
	public void close() {
		this.owner.closeInventory();
	}
	
	@Nonnull
	public int getFirstClearSlot() {
		for(int i=0; i<this.inventory.getSize(); i++) {
			if(this.inventory.getItem(i) == null) {
				return i;
			}
		}
		return -1;
	}
	
	public void open() {
		if(this.getOwner().getOpenInventory().equals(this.view)) {
			// prevent double inventory opening
			return;
		}
		this.view = this.owner.openInventory(inventory);
	}
	
	/**
	 * 
	 * AddMenuClickHandler. Triggers when a {@link Player} clicks the given {@link InventorySlot}.
	 * This method automatically cancels the click event.
	 * 
	 * @param {@see Integer} inventorySlot;
	 * @param {@see MenuHandler} handler;
	 */
	
	public void addMenuClickHandler(int slot, MenuHandler<InventoryClickEvent> handler) {
		// so i dont get fucked
		handlers.putIfAbsent(slot, new ArrayList<MenuHandler<InventoryClickEvent>>());
		
		ArrayList<MenuHandler<InventoryClickEvent>> handler_list = handlers.get(slot);
		handler_list.add(handler);
		
		handler_list.add(new MenuHandler<InventoryClickEvent>((InventoryClickEvent e) -> {
			e.setCancelled(true);
		}));
		
		handlers.put(slot, handler_list);
	}
	
	/**
	 * 
	 * AddGlobalMenuClickHandler. Triggers when a {@link Player} clicks any {@link InventorySlot}.
	 * This does not automatically cancel like the MenuClickHandler, so be careful, and remember to add
	 * e.setCancelled(true) when using it.
	 * 
	 * @param {@see MenuHandler} handler;
	 */
	
	public void addGlobalMenuClickHandler(MenuHandler<InventoryClickEvent> handler) {
		for(int i=0; i<this.inventory.getSize(); i++) {
			handlers.putIfAbsent(i, new ArrayList<MenuHandler<InventoryClickEvent>>());
			ArrayList<MenuHandler<InventoryClickEvent>> handler_list = handlers.get(i);
			handler_list.add(handler);
			handlers.put(i, handler_list);
		}
	}
	
	public void addEmptyClickHandler(MenuHandler<InventoryClickEvent> handler) {
		this.empty_click_handlers.add(handler);
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
		inventory.setItem(slot, item);
	}
	
	public void addInsertHandler(int slot, MenuHandler<InventoryClickEvent> handler) {
		if(!placeable_slots.contains(slot)) {
			Bukkit.getLogger().log(Level.WARNING, "Argument error: Slot " + Integer.toString(slot) + " is not a valid member of PlaceableSlots.");
			return;
		}
		
		insertHandlers.putIfAbsent(slot, handler);
	}
	
	public void setItem(int slot, ItemStack item) {
		this.inventory.setItem(slot, item);
	}
	
	public Player getOwner() {
		return this.owner;
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	protected abstract void decorate(Inventory i);
	
	public void unregister(UUID identifier) {
		this.handlers.forEach((Integer slot, ArrayList<MenuHandler<InventoryClickEvent>> handlers) -> {
			handlers.forEach((MenuHandler<InventoryClickEvent> handler) -> {
				if(handler.getIdentifier().equals(identifier)) {
					handlers.remove(handler);
				}
			});
		});
	}
	
	public void addInsertableSlot(int slot) {
		placeable_slots.add(slot);
	}
	
	public void addMenuCloseHandler(MenuHandler<InventoryCloseEvent> handler) {
		inventory_close_handlers.add(handler);
	}
	
	public void addShiftClickHandler(MenuHandler<InventoryClickEvent> handler) {
		shift_click_handlers.add(handler);
	}
	
	public void disableDrop(ItemStack item) {
		
	}
	
	public void addTicker(MenuHandler<TickerTickEvent> handler) {
		this.tickers.add(handler);
	}
	
	@EventHandler
	public void onTickerTick(TickerTickEvent e) {
		this.tickers.forEach((MenuHandler<TickerTickEvent> handler) -> {
			handler.run(e);
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent e) {
		
		Inventory c_inventory = e.getClickedInventory();
		if(c_inventory == null) return;
		
		Player clicker = (Player) e.getWhoClicked();
		if(clicker.getOpenInventory().equals(this.view)) {
			if(e.getClick() == ClickType.SHIFT_LEFT && !(this.inventory.getViewers().isEmpty())) {
				e.setCancelled(true);
				shift_click_handlers.forEach((MenuHandler<InventoryClickEvent> scHandler) -> {
					scHandler.run(e);
				});
				return;
			}
		}
		
		if(c_inventory.equals(this.inventory)) {

			if(e.getCurrentItem() == null) {
				empty_click_handlers.forEach((MenuHandler<InventoryClickEvent> emptyHandler) -> {
					emptyHandler.run(e);
				});
			}
			
			ArrayList<MenuHandler<InventoryClickEvent>> handler_list = handlers.get(e.getSlot());
			if(handler_list != null) {
				handler_list.forEach((MenuHandler<InventoryClickEvent> handler) -> {
					handler.run(e);
				});
			}
			if(placeable_slots.contains(e.getSlot())) {
				if(e.getCurrentItem() == null || (e.isShiftClick() && e.getCurrentItem() != null)) {
					insertHandlers.get(e.getSlot()).run(e);
				}
				return;
			}
		}
	}
	
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().equals(this.inventory)) {
          e.setCancelled(true);
        }
    }
  
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
    	this.okay_to_close = true;
    	bypass_close = true;
		inventory_close_handlers.forEach((MenuHandler<InventoryCloseEvent> handler) -> {
			handler.run(new InventoryCloseEvent(view));
		});
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
    	this.okay_to_close = true;
    	bypass_close = true;
		inventory_close_handlers.forEach((MenuHandler<InventoryCloseEvent> handler) -> {
			handler.run(new InventoryCloseEvent(view));
		});
    }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent e) {
		Inventory c_inventory = e.getInventory();
		if(c_inventory.equals(this.inventory)) {
			inventory_close_handlers.forEach((MenuHandler<InventoryCloseEvent> handler) -> {
				handler.run(e);
			});
			if(
				this.okay_to_close || 
				bypass_close
			) {
				InventoryCloseEvent.getHandlerList().unregister(this);
				InventoryClickEvent.getHandlerList().unregister(this);
				InventoryDragEvent.getHandlerList().unregister(this);
				TickerTickEvent.getHandlerList().unregister(this);
				PlayerDeathEvent.getHandlerList().unregister(this);
				PlayerQuitEvent.getHandlerList().unregister(this);
				
				BaseUIObject.openUIObjects.remove(this);
			} else {
				new BukkitRunnable() {
					@Override
					public void run() {
						okay_to_close = true;
						BaseUIObject.this.open();
					}
				}.runTaskLater(plugin, 1L);
			}
		}
	}
	
	public void forcefullyClose() {
    	this.okay_to_close = true;
    	bypass_close = true;
		inventory_close_handlers.forEach((MenuHandler<InventoryCloseEvent> handler) -> {
			handler.run(new InventoryCloseEvent(view));
		});
	}

	public int[] getBorderSlots() {
		return new int[] {
				
		};
	}

	public void unregisterAll(int slot) {
		handlers.get(slot).forEach((MenuHandler<InventoryClickEvent> e) -> {
			this.unregister(e.getIdentifier());
		});
	}

	public boolean isOpen() {
		return this.view != null;
	}
	
}
