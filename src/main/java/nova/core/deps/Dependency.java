package nova.core.deps;

/**
 * Created by Mitchellbrine on 2015.
 */

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author RX14-chibi (original author) & Mitchellbrine (implemented it)
 */
public interface Dependency {

	/**
	 * Gets an identifier which is specific to this type of dependency.
	 * <p>
	 * e.g. {@code maven} or {@code URL}
	 *
	 * @return the dependency type ID.
	 */
	String getDependencyTypeID();

	/**
	 * Get an identifier which is specific to this dependency, this identifier
	 * should be the same through all versions of the dependency.
	 * <p>
	 * e.g. {@code net.novaapi.core} or a hash of the URL
	 *
	 * @return the dependency ID.
	 */
	String getDependencyID();

	/**
	 * Gets an identifier that is specific to this version of the dependency.
	 * <p>
	 * e.g. {@code 1.2.3}
	 *
	 * @return the version ID.
	 */
	String getVersionID();

	/**
	 * Gets a URL to download the dependency from.
	 *
	 * @return the URL to download the dependency from.
	 */
	URL getDownloadURL() throws MalformedURLException;

}