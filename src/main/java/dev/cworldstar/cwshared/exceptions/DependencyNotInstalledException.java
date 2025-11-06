package dev.cworldstar.cwshared.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * 
 * This exception should only be triggered when a feature
 * looks for a dependency code and is unable to find it. It provides a simple
 * way to throw an exception for calling soft depend methods.
 * <p>
 * Example: <p>
 * <code>
 * 	Economy econ = VaultIntegration.getEconomy();
 * 	if(econ == null) throw new DependencyNotInstalledException("Vault is not installed!");
 * </code>
 * 
 * @author cworldstar
 *
 */
public class DependencyNotInstalledException extends RuntimeException {

	private static final long serialVersionUID = 5040858920040640171L;

	public DependencyNotInstalledException(@Nullable String reason) {
		super(reason == null ? "Generic exception, reason undefined." : reason);
	}
	
	public DependencyNotInstalledException() {
		this(null);
	}
	
}
