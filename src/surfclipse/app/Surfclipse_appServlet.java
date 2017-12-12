package surfclipse.app;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Scanner;
import javax.servlet.http.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import core.Result;
import core.SearchResultProvider;

@SuppressWarnings("serial")
public class Surfclipse_appServlet extends HttpServlet {

	String searchQuery;
	String stackTrace;
	String codeContext;
	String recentPageData;
	String charset = "UTF-8";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		long curr_time = System.currentTimeMillis();
		this.searchQuery = req.getParameter("searchquery");
		this.stackTrace = req.getParameter("stacktrace");
		this.codeContext = req.getParameter("codecontext");
		this.recentPageData = req.getParameter("recentpagedata");

		// decoding of parameters
		try {
			this.searchQuery = URLDecoder.decode(this.searchQuery, charset);
			this.stackTrace = URLDecoder.decode(this.stackTrace, charset);
			this.codeContext = URLDecoder.decode(this.codeContext, charset);
			this.recentPageData = URLDecoder.decode(this.recentPageData,
					charset);
		} catch (Exception exc) {
		}

		// this.searchQuery="java.net.SocketException: Permission denied: connect";
		// this.stackTrace=get_stack_trace();
		// this.codeContext="";

		SearchResultProvider provider = new SearchResultProvider(searchQuery,
				stackTrace, codeContext, recentPageData);
		ArrayList<Result> results = provider.provide_the_final_results();
		int total_result = results.size();
		long end_time = System.currentTimeMillis();
		long duration = end_time - curr_time;
		JSONArray jsonItems = convert_to_json(results);
		resp.setContentType("application/json");
		// resp.getWriter().println("Total results found:"+total_result+" in "+duration+" milliseconds");
		resp.getWriter().print(jsonItems);
		// System.out.println("Total results found:"+total_result+" in "+duration+" milliseconds");
	}

	protected String get_stack_trace() {
		// code for getting stack trace
		String stack = new String();
		String fileName = StaticData.Base_Directory + "/exception/" + 1
				+ ".txt";
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				stack += line;
			}
			scanner.close();
		} catch (Exception exc) {
		}
		return stack;
	}

	@SuppressWarnings("unchecked")
	protected JSONArray convert_to_json(ArrayList<Result> myResults) {
		// code for converting results into JSON
		JSONArray items = new JSONArray();
		int rank = 1;
		for (Result result : myResults) {
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("rank", rank);
				jsonObj.put("title", result.title);
				jsonObj.put("description", result.description);
				jsonObj.put("resultURL", result.resultURL);
				jsonObj.put("totalscore",
						result.totalScore_content_context_popularity);
				jsonObj.put("contentscore", result.content_score);
				jsonObj.put("contextscore", result.context_score);
				jsonObj.put("popularityscore", result.popularity_score);
				jsonObj.put("confidence", result.search_result_confidence);
				// jsonObj.put("desctext",
				// URLEncoder.encode(result.representativeText,"UTF-8"));
				items.add(jsonObj);
				rank++;
			} catch (Exception exc) {
			}
		}
		return items;
	}
}
