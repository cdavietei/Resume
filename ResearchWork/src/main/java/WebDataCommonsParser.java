import java.net.*;
import java.net.*;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * This class parses the Web Data Commons page containing information regarding
 * the Winter 2013 Microdata Corpus. Found at the following link:
 * {@link http://webdatacommons.org/structureddata/2013-11/stats/schema_org_subsets.html}
 *
 * Outputs the list of all class sets hosted on the page and its parent classes
 *
 * @author Chris Davie
 */
public class WebDataCommonsParser {

	public static void main(String[] args) {

		Document doc = null;

		try
		{
			doc = Jsoup.parse(new URL("http://webdatacommons.org/structureddata/2013-11/stats/schema_org_subsets.html"),5000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Elements rows = doc.select("tr");
		rows.remove(0);
		StringBuilder classes = new StringBuilder("{");
		StringBuilder depends = new StringBuilder("{");

		String url = "http://schema.org/";

		int i = 0;


		for(Element col : rows)
		{
			Element head = col.select("th").select("a").first();
			classes.append("\""+head.text().replace("http://schema.org/", "")+"\",");

			String row = col.select("td").get(1).text();
			//System.out.println();
			String[] files = parseDepends(row);
			String text = breadCrumb(head.text().replace("/ ", "/"));
			if(!text.equals(""))
				depends.append("{\""+text+"\",");
			String added = text;
			for(String s : files)
			{
				String a = breadCrumb(s);

				if(!a.equals("") && !added.contains(a))
				{
					added+= a+" ";
					depends.append("\""+a+"\",");
				}
			}
			depends.append("},\n");
		}

		classes.append("}");
		depends.append("}");
		System.out.println(classes.toString().replace(",}", "}"));
		System.out.println(depends.toString().replace(",}", "}"));
		System.out.println();
	}

	public static String[] parseDepends(String text)
	{
		text = text.replaceAll("[\\(][^\\(]*[\\)]", "");
		return text.split("  ");
	}

	public static String breadCrumb(String url)
	{
		//Addresses an issue where an invalid URL was displayed
		url = url.replace(".org", ".org/");

		Document doc = null;
		try
		{
			doc = Jsoup.parse(new URL(url),5000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("\n"+e.getLocalizedMessage()+" :: "+url);
			return "";
		}

		Element bread = doc.select("span.breadcrumbs").first();
		Elements links = bread.select("a");
		Element file = links.get(1);

		return file.text() +".rdf";
	}
}
