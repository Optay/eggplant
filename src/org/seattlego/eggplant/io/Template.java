package org.seattlego.eggplant.io;

/**
 * Encapsulates a saved template. Wraps the XML save of a template as a String
 * along with a name and description. Using a String keeps things simple as
 * parsing templates only needs to be done occasionally.
 * 
 * @author Topsy
 */
public class Template {
    private String name;
    private String description;
    private String markup;
    
    public Template( String name, String description, String markup ) {
        this.name = name;
        this.description = description;
        this.markup = markup;
    }
    
    public String getName() { return name; }
    public void setName( String value ) { name = value; }
    
    public String getDescription() { return description; }
    public void setDescription( String value ) { description = value; }
    
    public String getMarkup() { return markup; }
    public void setMarkup( String value ) { markup = value; }
    
    
}
