package scoring;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import core.Result;

public class AlexaCompeteScore {

	/**
	 * @param args
	 */
	public ArrayList<Result> EntryList;

	public AlexaCompeteScore(ArrayList<Result> EntryList) {
		// code for assignment
		this.EntryList = EntryList;
	}

	public AlexaCompeteScore() {
		// default constructor
	}

	public long provide_alexa_rank_xml(String resultURL) {
		// code for returning Alexa rank
		long alexa_rank = 0;
		try {
			URL url = new URL(resultURL);
			String domain_name = url.getHost();
			String api_url = "http://data.alexa.com/data?cli=10&url="
					+ domain_name;
			URL connection = new URL(api_url);
			InputStream response = connection.openStream();
			// String responseStr = IOUtils.toString(response,"UTF-8");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(response);
			NodeList nodeList = doc.getElementsByTagName("SD");

			if (nodeList.getLength() > 0) {
				Node SD = nodeList.item(0);
				NodeList children = SD.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node tempNode = children.item(i);
					if (tempNode.getNodeName().equals("POPULARITY")) {
						Element e = (org.w3c.dom.Element) tempNode;
						String rankStr = e.getAttribute("TEXT");
						alexa_rank = Long.parseLong(rankStr);
						break;
					}
				}
			}
		} catch (Exception exc) {
		}
		return alexa_rank;
	}

	protected long provide_alexa_rank(String resultURL) {
		// code for returning Alexa rank
		long alexa_rank = 0;
		try {
			URL url = new URL(resultURL);
			String domain_name = url.getHost();
			try {
				String api_url = "http://api.camcimcumcem.com/alexa/rank/?output=json&domain="
						+ domain_name;
				URL connection = new URL(api_url);
				InputStream response = connection.openStream();
				String responseStr = IOUtils.toString(response, "UTF-8");
				JSONParser parser = new JSONParser();
				Object wholeObj = parser.parse(responseStr);
				String rankStr = ((JSONObject) wholeObj).get("rank").toString();
				alexa_rank = Long.parseLong(rankStr);
			} catch (Exception exc) {
			}
		} catch (Exception exc) {
		}
		return alexa_rank;
	}

	public long provide_compete_rank(String resultURL) {
		// code for returning compete rank
		long compete_rank = 0;
		try {
			URL url = new URL(resultURL);
			String domain_name = url.getHost();
			try {
				String api_url = "http://apps.compete.com/sites/"
						+ domain_name
						+ "/trended/Rank/?apikey=59df3d018d9904f18f1dfd4a48951d16";
				URL connection = new URL(api_url);
				InputStream response = connection.openStream();
				String responseStr = IOUtils.toString(response, "UTF-8");
				JSONParser parser = new JSONParser();
				Object wholeObj = parser.parse(responseStr);
				Object dataObj = ((JSONObject) wholeObj).get("data");
				Object trendObject = ((JSONObject) dataObj).get("trends");
				Object rankObject = ((JSONObject) trendObject).get("rank");
				JSONArray rankArray = (JSONArray) rankObject;
				int size = rankArray.size();
				JSONObject target = (JSONObject) rankArray.get(size - 1);
				String rankStr = target.get("value").toString();
				compete_rank = Long.parseLong(rankStr);
			} catch (Exception exc) {
			}
		} catch (Exception exc) {
		}
		return compete_rank;
	}

	protected Result average_the_rank(Result result) {
		// code for averaging the result
		if (result.alexaRank > 0 && result.competeRank > 0) {
			result.averageRank = (result.alexaRank + result.competeRank) / 2.0;
		} else if (result.alexaRank > 0 || result.competeRank > 0) {
			if (result.alexaRank > 0)
				result.averageRank = result.alexaRank;
			if (result.competeRank > 0)
				result.averageRank = result.competeRank;
		} else {
			// nothing to do
			result.AlexaCompeteRankScore = 0;
		}
		return result;
	}

	public ArrayList<Result> get_alexa_compete_rank_score() {
		// code for getting Site rank score
		double max_average_rank = 0;
		for (Result result : this.EntryList) {
			String resultURL = result.resultURL;
			// collecting from ALEXA
			try {
				result.alexaRank = provide_alexa_rank_xml(resultURL);
				result.averageRank = result.alexaRank;
			} catch (Exception exc) {
				result.alexaRank = -1;
			}
			if (result.averageRank > max_average_rank)
				max_average_rank = result.averageRank;
		}
		// returning intermediate results
		return this.EntryList;
	}

	public ArrayList<Result> get_alexa_compete_relative_rank_score() {
		// now provide the normalization
		double max_average_rank = 0;
		for (Result result : this.EntryList) {
			if (result.alexaRank > max_average_rank)
				max_average_rank = result.alexaRank;
		}
		if (max_average_rank > 0) {
			for (Result result2 : this.EntryList) {
				double result_rank = result2.alexaRank;
				if (result_rank > 0)// condition check
				{
					double rank_score = 1 - ((result_rank - 1) / max_average_rank);
					result2.AlexaCompeteRankScore = rank_score;
				}
			}
		}
		return this.EntryList;
	}

	protected void show_the_score(ArrayList<Result> results) {
		// code for showing the scores
		for (Result result : results) {
			System.out.println(result.alexaRank + "\t" + result.competeRank
					+ "\t" + result.AlexaCompeteRankScore + "\t"
					+ result.resultURL);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Result result = new Result();
		result.averageRank = 5794;
		result.resultURL = "http://www.eclipse.org";
		ArrayList<Result> temp = new ArrayList<>();
		temp.add(result);
		AlexaCompeteScore acscore = new AlexaCompeteScore(temp);
		temp = acscore.get_alexa_compete_rank_score();
		temp = acscore.get_alexa_compete_relative_rank_score();
	}

}
