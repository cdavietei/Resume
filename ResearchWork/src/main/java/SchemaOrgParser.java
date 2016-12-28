import java.io.*;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;

public class SchemaOrgParser
{
	public static String url = "http://schema.org/docs/full.html";
	public static String baseUrl = "http://schema.org";

	public static void main(String[] args)
	{
		Document doc = null;

		long startTime = System.currentTimeMillis();

		int tries = 0;
		try
		{
			doc = Jsoup.parse(new URL(url), 5000);
		}
		catch(IOException e)
		{
			System.out.println(e.getLocalizedMessage());
		}

		Elements thing = doc.select("#full_thing_tree");
		Elements list = thing.select("div > ul > li > ul > li");

		String base = thing.select("li.tbranch a, li.tleaf a").not("[href^=#]").first().attr("href");

		int counter = 1;
		int fullSize = list.size();

		for(Element div : list)
		{
			String title = div.select("a").first().text();
			System.out.println(title);

			OntModel model = ModelFactory.createOntologyModel();
			addToModel(model,baseUrl+base);
			addToModel(model,baseUrl+"/"+title);

			Elements subList = div.select("li.tbranch a, li.tleaf a").not("[href^=#]").not(".ext");
			Elements extList = div.select("li.tbranch a, li.tleaf a").select(".ext").not("[href^=#]");

			int size = subList.size()+extList.size();
			int current = 0;

			for(Element subDiv : subList)
			{
				String ext = subDiv.attr("href");
				System.out.println("Parsing: "+ext+" :: "+(++current)+" of "+size+" :: "+counter+" out of " + fullSize);
				addToModel(model, baseUrl+ext);
			}//for

			for(Element subDiv : extList)
			{
				String ext = subDiv.attr("href");
				System.out.println("Parsing: "+ext+" :: "+(++current)+" of "+size+" :: "+counter+" out of " + fullSize);
				addToModel(model,ext);
			}//for

			counter++;
			FileOutputStream out = null;
			try
			{
				System.out.println("\nWriting "+title+" to file.\n");
				out = new FileOutputStream(title+".rdf");
				model.write(out, "RDF/XML");
				if(out!=null)
					out.close();
			}
			catch (IOException e)
			{
				System.out.println(e.getLocalizedMessage());
			}
			model.close();
		}//for

		long endTime = System.currentTimeMillis();

		System.out.println("Time taken: "+(endTime-startTime)/60000.0+" minutes");
	}//main

	public static void addToModel(OntModel model, String base)
	{
		int tries = 0;
		while(tries<3)
		{
			try
			{
				RDFaParser rdfa = new RDFaParser(base,model);
				rdfa.setUp();
				rdfa.run();
				tries = 4;
			}
			catch(Exception e)
			{
				tries++;
				System.out.println("Exception:"+ e.getLocalizedMessage());
			}
		}//while
	}//addToModel(OntModel, String)
}//SchemaOrgParser
