import java.io.*;
import java.net.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;

/**
 * This class serves to parse individual classes of the Schema.org Ontology into an ontological model
 * Uses JSoup API to parse the RDFa Markup and JENA API to create the ontology
 *
 * @author Chris Davie
 * @version 1.0
 *
 */
public class RDFaParser
{

	protected String SCHEMA = "http://schema.org/";
	protected static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

	private OntModel model;
	private OntClass subject;
	private OntProperty prop;
	private Resource object;

	private Document doc;
	private Elements list, subClass;
	private Element element;

	private String className;

	/**
	 * Constructor that creates an new ontological model for the class given by url
	 * @param url The URL of the schema.org class to be parsed
	 */
	public RDFaParser(String url)
	{
		model = ModelFactory.createOntologyModel();

		try
		{
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}

		list = doc.select("[typeOf],[property]");
		subClass = doc.select("th.supertype-name");
	}//RDFaParser(String)

	/**
	 * Constructor that adds the class given by URL to an existing model
	 * @param url The URL of the schema.org class to be parsed
	 * @param model The existing model to be added to
	 */
	public RDFaParser(String url, OntModel model)
	{
		this.model = model;
		try
		{
			doc = Jsoup.connect(url).get();
		}
		catch (IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}

		list = doc.select("[typeOf],[property]");
		subClass = doc.select("th.supertype-name");
	}//RDFaParser(String, OntModel)

	/**
	 * Sets up the variables needed to create the triples for the class
	 * @throws Exception used to save
	 */
	public void setUp()
	{
		element = list.select("[typeOf=rdfs:Class]").first();
		if(element == null)
			element = list.select("[typeOf=rdfs:Property]").first();
		subject = model.createClass(element.attr("resource"));
		className =  element.attr("resource").replace(SCHEMA, "");
		list.remove(element);

		element = list.select("[property=rdfs:comment]").first();
		subject.addComment(model.createLiteral(element.text()));
		list.remove(element);

		element = list.select("[property=rdfs:label]").first();

		if(element.text().equals(className))
		{
			subject.addLabel(model.createLiteral(element.text()));
			list.remove(element);
		}
		else
			subject.addLabel(model.createLiteral(className));

		list.remove(element);

		setSuperClasses();

	}//setUp()

	/**
	 *
	 * @throws Exception
	 */
	public void run() throws Exception
	{
		for(Element div : list)
		{
			prop = model.createObjectProperty(div.attr("resource"));
			prop.addDomain(subject);

			Elements subList = div.select("[property]");
			for(Element subDiv : subList)
				setProperty(subDiv.attr("property"),subDiv.text());

			if(prop!=null && object!=null)
				subject.addEquivalentClass(model.createSomeValuesFromRestriction(null, prop, object));
		}//for div

	}//run()

	/**
	 *
	 * @param fileName
	 */
	public void writeModel(String fileName)
	{
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(fileName);
			model.write(out, "RDF/XML");

			if(out!=null)
				out.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}
	}//writeModel(String)

	/**
	 *
	 * @param property
	 * @param value
	 */
	public void setProperty(String property, String value)
	{
		property = property.replace("rdfs:", "");

		switch(property)
		{
			case "label":
				prop.addLabel(model.createLiteral(value));
				//System.out.println("Adding label: "+value+" to property: "+prop.getURI());
				break;
			case "rangeIncludes":
				object = model.createResource(SCHEMA+value);
				prop.addRange(object);
				//System.out.println("Adding range: "+value+" to property: "+prop.getURI());
				break;
			case "comment":
				prop.addComment(model.createLiteral(value));
				//System.out.println("Adding comment: "+value+" to property: "+prop.getURI());
				break;
			default:
				break;
		}
	}//setProperty(String, String)

	/**
	 *
	 */
	public void setSuperClasses()
	{
		Element del = subClass.first();

		if(del.text().replace("Properties from ", "").equals(className))
		{
			list.remove(del);
			list = doc.select("tbody.supertype").first().select("[typeOf]");
			subClass.remove(subClass.first());
		}
		else
			list.clear();

		if(subClass.size()>2 && subClass.last().text().equals("Properties from Thing"))
			subClass.remove(subClass.size()-1);

		for(int i = 0; i<subClass.size();i++)
			for(int j = i+1; j<subClass.size(); j++)
			{
				OntClass cI = model.getOntClass(SCHEMA+subClass.get(i).text().replace("Properties from ", ""));
				OntClass cJ = model.getOntClass(SCHEMA+subClass.get(j).text().replace("Properties from ", ""));
				if(cI == null)
					subject.addSuperClass(model.createResource(SCHEMA+subClass.get(i).text().replace("Properties from ", "")));

				else if(cJ == null)
					subject.addSuperClass(model.createResource(SCHEMA+subClass.get(j).text().replace("Properties from ", "")));

				else if(cI.hasSuperClass(cJ) && !cI.equals(cJ))
				{
					subClass.remove(j);
					j = subClass.size();
				}
				else if(cI.hasSubClass(cJ) && !cI.equals(cJ))
				{
					subClass.remove(i);
					j = subClass.size();
				}
			}//for j

		for(Element div : subClass)
			subject.addSuperClass(model.createResource(SCHEMA+div.text().replace("Properties from ", "")));
	}//setSuperClasses()
}//RDFaParser
