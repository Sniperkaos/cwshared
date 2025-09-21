package dev.cworldstar.cwshared.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cworldstar.cwshared.annotations.ErrorsIf;
import dev.cworldstar.cwshared.events.TickerTickEvent;


public abstract class PagedUIObject extends BaseUIObject {

	private List<PageLayout> layouts;
	private PageLayout currentLayout;
	private Inventory to_decorate;
	private int page = 0;
	private int lslot = 1;
	private int rslot = 2;
	
	public void addLayout(PageLayout layout) {
		layout.setInventory(this.getInventory());
		layout.setParent(this);
		layout.setPage(this.layouts.size());
		layouts.add(layout);
	}

	/**
	 * This method gets a PageLayout when given a page.
	 * @param page An {@code Integer}. The page number to query
	 * @return {@link PageLayout} the layout at this given page.
	 */
	@ErrorsIf(Reason="The given page number doesn't exist in layouts.")
	public PageLayout getLayout(int page) {
		Validate.isTrue(page <= layouts.size(), "You must provide a page number within 0 and size()-1!");
		return layouts.get(page);
	}
	
	public int getPage() {
		return this.page;
	}
	
	@Override
	public void decorate(Inventory i) {
		to_decorate = i;
	}
	
	/**
	 * 
	 * This method takes a predicate and returns the first matching result.
	 * 
	 * @param predicate {@link Predicate}<PageLayout>
	 * @return {@link PageLayout}
	 */
	@Nullable
	public PageLayout findLayout(Predicate<PageLayout> predicate) {
		for(PageLayout layout : this.layouts) {
			if(predicate.test(layout)) {
				return layout;
			}
		}
		return null;
	}
	
	public void tickLayouts(TickerTickEvent e) {
		for(PageLayout layout : layouts) {
			if(layout.isActive()) {
				layout.tick(e);
			}
		}
	}
	
	public void setup() {
		decorateLayout(to_decorate);
		Validate.isTrue(layouts.size() > 0, "PagedUIObject#decorateLayout MUST contain at least 1 PageLayout!");
		addGlobalMenuClickHandler(new MenuHandler<InventoryClickEvent>((InventoryClickEvent e)-> {
			PageLayout layout = this.layouts.get(this.page);
			if(e.isShiftClick()) {
				layout.shiftClick(e, e.getSlot());
			} else {
				layout.click(e, e.getSlot());
			}
		}));
		addTicker(new MenuHandler<TickerTickEvent>((TickerTickEvent e) -> {
			tickLayouts(e);
		}));
		decoratePageWithLayout(layouts.get(0));
	}
	
	public PagedUIObject(JavaPlugin plugin, Player player, InventorySize size) {
		super(plugin, player, size);
		layouts = new ArrayList<PageLayout>();
		setup();
	}

	public PagedUIObject(JavaPlugin plugin, Player player, InventorySize extraLarge, String string) {
		super(plugin, player, extraLarge, string);
		layouts = new ArrayList<PageLayout>();
		setup();
	}

	protected void decoratePageWithLayout(PageLayout layout) {
		if(currentLayout != null) {
			currentLayout.clear();
		}
		currentLayout = layout;
		layout.decorate(this);
	}
	
	public void firePageHandlers(InventoryClickEvent e) {
		this.layouts.get(this.page).click(e, lslot);
	}
	
	public int getPageSlotLeft() {
		return this.lslot;
	}
	
	public int getPageSlotRight() {
		return this.rslot;
	}
	
	/**
	 * 
	 * This method forcefully displays a given layout, but will error
	 * if the parent {@link PagedUIObject} is null or not this one.
	 * 
	 * @param {@link PageLayout} layout
	 */
	@ErrorsIf(Reason="Invalid page layout")
	@ErrorsIf(Reason="PageLayout has no page.")
	public void displayLayout(@Nonnull PageLayout layout) {
		
		assert layout.getPage() == -1 : "PageLayout given had an invalid page.";
		assert layout.getParent().equals(this) : "PageLayout given did not have this UIObject as parent!";

		
		int page = layout.getPage();
		this.page = page;
		
		decoratePageWithLayout(layout);
	}
	
	public void nextPage() {
		if(page+1>=layouts.size()) {
			this.page = 0;
		} else {
			this.page = this.page + 1;
		}
		
		PageLayout layout = this.layouts.get(this.page);
		// decorate new item
		decoratePageWithLayout(layout);
	}
	
	public void previousPage() {
		if(page-1<0) {
			this.page = layouts.size()-1;
		} else {
			this.page = this.page - 1;
		}
		
		PageLayout layout = this.layouts.get(page);
		// decorate new item
		decoratePageWithLayout(layout);
	}

	public abstract void decorateLayout(Inventory i);
	
}
