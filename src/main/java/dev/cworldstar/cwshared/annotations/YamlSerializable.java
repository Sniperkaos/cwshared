package dev.cworldstar.cwshared.annotations;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Using this interface will allow the given class to be yaml serializable. 
 * If you implement this, {@link YamlSerializable#register()} MUST be called,
 * otherwise it will not work.
 * @author cworldstar
 *
 */

public interface YamlSerializable extends ConfigurationSerializable {
	
	/**
	 * This method is required to be called.
	 */
	default void register() {
		ConfigurationSerialization.registerClass(this.getClass());
	}
	
	abstract Map<String, Object> serialize();
	
	abstract <T extends YamlSerializable> T deserialize(Map<String, Object> serialized);
}
