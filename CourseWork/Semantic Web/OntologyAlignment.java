import java.io.*;
import java.util.Iterator;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Aligns two ontologies created from Amazon Music and Barnes and Noble Music stores respectively.
 * 
 * (Disclaimer) Not a complete alignment, for demonstration purposes only. 
 * Aligning only five classes and five properties from each respective class
 * 
 * @author Chris
 *
 */
public class OntologyAlignment 
{

	/* Declares the namespaces for the two ontologies */
	public static final String MODEL1_NS = "http://www.semanticweb.org/chris/ontologies/2015/2/untitled-ontology-6#";
	public static final String MODEL2_NS = "http://www.semanticweb.org/chris/ontologies/2015/2/untitled-ontology-9#";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";

	/* Variables to store the different ontologies */
	protected Model model1;
	protected Model model2;
	protected Model schema;

	protected InfModel inferredModel;
	protected OntModel inferences;

	public static void main(String[] args) 
	{		
		OntologyAlignment ont = new OntologyAlignment();

		System.out.println("Read Model1");
		ont.populateModel(ont.model1, "Model1.owl");

		System.out.println("Read Model2");
		ont.populateModel(ont.model2, "Model2.owl");

		System.out.println("Populate Schema");
		ont.populateSchema();
		ont.addAlignment();
		
		System.out.println("Save Aligned Ontology");
		ont.saveSchema();
		ont.runPellet();
		System.out.println("Save Inferred Ontology");
		ont.saveInfModel();
		
		System.out.println("=====================Print Inferred Classes===================");
		ont.printInfClasses(ont.inferences);	
	}//main

	/**
	 * Reads the ontology into a model from the give file path
	 * @param model The model that the ontology will be read into 
	 * @param filePath The file path of the ontology
	 */
	private void populateModel(Model model, String filePath)
	{
		model = ModelFactory.createOntologyModel();
		model.read(filePath);
	}//populateModel

	/**
	 * Manually aligns the two ontologies
	 */
	private void addAlignment()
	{
		/*------------Class Alignment------------*/
		
		/* Declares Artist from both Ontologies to be equivalent classes */
		addTriple(MODEL1_NS+"Artist",OWL_NS+"equivalentClass",MODEL2_NS+"Artist");
		
		/* Declares Genre from both Ontologies to be equivalent classes */
		addTriple(MODEL1_NS+"Genre",OWL_NS+"equivalentClass",MODEL2_NS+"Genre");
		
		/* Declares MusicItem from both Ontologies to be equivalent classes */
		addTriple(MODEL1_NS+"MusicItem",OWL_NS+"equivalentClass",MODEL2_NS+"MusicItem");
		
		/* Declares Blues and BluesFolk to be equivalent classes */
		addTriple(MODEL1_NS+"BluesFolk",OWL_NS+"equivalentClass",MODEL2_NS+"Blues");
		
		/* Declares BargainBin and Deals to be equivalent classes */
		addTriple(MODEL1_NS+"Deals",OWL_NS+"equivalentClass",MODEL2_NS+"BargainBin");
		
		/* Declares VinylStore and VinylRecords to be equivalent classes */
		addTriple(MODEL1_NS+"VinylStore",OWL_NS+"equivalentClass",MODEL2_NS+"BargainBin");
		
		/* Declares Blues to be a sub class of Genre */
		addTriple(MODEL1_NS+"Blues",RDFS_NS+"subClassOf",MODEL2_NS+"Genre");
		
		/*------------Property Alignment------------*/
		
		/* Declares hasArtist from both Ontologies to be equivalent class */
		addTriple(MODEL1_NS+"hasArtist",OWL_NS+"equivalentProperty",MODEL2_NS+"hasArtist");
		
		/* Declares hasGenre from both Ontologies to be equivalent class */
		addTriple(MODEL1_NS+"hasGenre",OWL_NS+"equivalentProperty",MODEL2_NS+"hasGenre");
		
		/* Declares hasFeaturedProducts to be a sub property of hasCustomerFavorites */
		addTriple(MODEL2_NS+"hasFeaturedProducts",RDFS_NS+"subPropertyOf",MODEL1_NS+"hasCustomerFavorites");
		
		/* Declares hasFeaturedPrograms to be a sub property of hasCustomerFavorites */
		addTriple(MODEL2_NS+"hasFeaturedPrograms",RDFS_NS+"subPropertyOf",MODEL1_NS+"hasCustomerFavorites");
		
		/* Declares hasMoreToExplore to be a sub property of hasCustomerFavorites */
		addTriple(MODEL2_NS+"hasMoreToExplore",RDFS_NS+"subPropertyOf",MODEL1_NS+"hasCustomerFavorites");
	}//addAlignment
	
	/**
	 *  Adds the given triple to the aligned ontology model
	 * @param subject The subject of the triple
	 * @param predicate The predicate / relationship between the subject and object
	 * @param object The object of the triple
	 */
	private void addTriple(String subject, String predicate, String object)
	{
		Resource resource = schema.createResource(subject);
		Property prop = schema.createProperty(predicate);
		Resource obj = schema.createResource(object);
		schema.add(resource, prop, obj);	
	}//align

	/**
	 * Combines the two ontologies into the aligned model
	 */
	private void populateSchema()
	{
		schema = ModelFactory.createOntologyModel();
		schema.read("Model1.owl");
		schema.read("Model2.owl");
	}//populateSchema

	/**
	 * Saves the aligned model into a file
	 */
	private void saveSchema()
	{
		try
		{
			OutputStream output = new FileOutputStream("AlignedOntology.owl");
			schema.write(output, "RDF/XML");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}
	}//saveSchema
	
	/**
	 * Saves the inferred aligned model into a file
	 */
	private void saveInfModel()
	{
		try
		{
			OutputStream output = new FileOutputStream("InferredAlignedOntology.owl");
			inferences.write(output, "RDF/XML");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}
	}//saveInfModel

	/**
	 * Runs the pellet reasoner on the aligned ontology
	 */
	private void runPellet()
	{
		Reasoner reasoner = PelletReasonerFactory.theInstance().create();
		reasoner = reasoner.bindSchema(schema);
		inferredModel = (InfModel) ModelFactory.createInfModel(reasoner, schema);
		StmtIterator stmts = inferredModel.listStatements();
		inferences = (OntModel) ModelFactory.createOntologyModel().add(stmts);	
	}//runPellet
	
	/**
	 * Prints out the inferred classes of the aligned ontology
	 */
	public void printInfClasses(Model model)
	{
		ExtendedIterator<OntClass> classes = ((OntModel) model).listClasses();
		while(classes.hasNext())
		{
			OntClass thisClass = (OntClass) classes.next();
			if(thisClass.getLocalName()!=null)
			{
				System.out.println("Found class: "+thisClass.getLocalName());

				ExtendedIterator inst = thisClass.listInstances();
				while(inst.hasNext())
				{
					Individual thisInstance = (Individual) inst.next();
					System.out.println("\tFound instance: "+ thisInstance.getLocalName());
				}
			}
		}
	}//printInfClasses
}
