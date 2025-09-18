# About Project
This is a library containing common classes I need to use for different projects.

# How to use
This library must be shaded using maven-shade-plugin. Example:
```maven
<relocations>
	<relocation>
	    <pattern>dev.cworldstar.cwshared</pattern>
	    <shadedPattern>YOUR.PATH.HERE.cwshared</shadedPattern>
	</relocation>
</relocations>
```

## Usage/Examples

### Lang
This library contains a common implementation of different language translations. 

Example Usage:
```java
Lang lang = Lang.get();
Component messageComponent = lang.get(player, "any.lang-here");
player.sendMessage(messageComponent); // the translated component
```

### FormatUtils
This library contains a bunch of static methods to format strings
into Components.

### UI
This library contains helper classes to create simple and easy UI.

Example UI:
```java
BaseUIObject object = new BaseUIObject(plugin, player, InventorySize.MEDIUM, "Example UI") {
    @Override
    protected void decorate(Inventory ui) {
        setBackgroundSlots(new int[] {
        		0,1,2,3,4,5,6,7,8,
        		9,17,
        		18,19,20,21,22,23,24,25,26
		}); // creates an empty UI of 3 rows with surrounding background items.
    }
}
```
