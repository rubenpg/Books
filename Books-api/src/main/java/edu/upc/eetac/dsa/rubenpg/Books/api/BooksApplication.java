package edu.upc.eetac.dsa.rubenpg.Books.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
 
public class BooksApplication extends ResourceConfig {
	public BooksApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}