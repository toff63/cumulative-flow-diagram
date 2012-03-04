package net.francesbagual.lean.webapp.cumulative.flow.diagram

class Feature {

    static constraints = {
		name blank: false
    }
	
	String name;
	String externalUrl;
	String description;
	Date start;
	Date end;
}
