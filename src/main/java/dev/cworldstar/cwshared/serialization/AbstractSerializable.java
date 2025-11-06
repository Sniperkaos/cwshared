package dev.cworldstar.cwshared.serialization;

import java.util.Map;

import dev.cworldstar.cwshared.annotations.YamlSerializable;

public abstract class AbstractSerializable<Z> implements YamlSerializable {
	
	protected Class<Z> object;
	
	@SuppressWarnings("unchecked")
	public AbstractSerializable(Z object) {
		register();
		this.object = (Class<Z>) object.getClass();
	}
	
	@Override
	public abstract <T extends YamlSerializable> T deserialize(Map<String, Object> serialized);
	@Override
	public abstract Map<String, Object> serialize();
}
