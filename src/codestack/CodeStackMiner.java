package codestack;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utility.RegexMatcher;
import utility.StackTraceUtils;
import core.StackTrace;

public class CodeStackMiner {
	
	public String textOnlyPageContent;
	public ArrayList<String> codestack_content;
	public ArrayList<StackTrace> codestack_processed;
	ArrayList<Element> codestack_html;
	public ArrayList<String> codestack_tokenset;
	String currentExceptionName;
	String pageContent;

	public CodeStackMiner(String exceptionName, String pageContent) {
		// initialization
		this.currentExceptionName = exceptionName;
		this.pageContent = pageContent;
		textOnlyPageContent = new String();
		this.codestack_content = new ArrayList<>();
		this.codestack_processed = new ArrayList<>();
		this.codestack_html = new ArrayList<>();
		this.codestack_tokenset = new ArrayList<>();
	}

	protected void collectCodeStacks() {
		// code for collecting code stack contents
		try {
			Document document = Jsoup.parse(this.pageContent);
			// adding quotes
			Elements quotes = document.select("blockquote");
			this.codestack_html.addAll(quotes);
			// System.out.println(codestacks.size());
			// adding codes
			Elements codes = document.select("code");
			this.codestack_html.addAll(codes);
			// System.out.println(codestacks.size());
			// adding pres
			Elements pres = document.select("pre");
			for (Element pelem : pres) {
				boolean addme = true;
				for (Element celem : codes) {
					if (pelem.text().contains(celem.text())) {
						addme = false;
						break;
					}
				}
				if (addme) {
					this.codestack_html.add(pelem);
				}
			}
			// adding paras if nothing is found
			if (this.codestack_html.size() == 0) {
				Elements paras = document.select("p");
				this.codestack_html.addAll(paras);
			}
			// adding paras if nothing is found
			if (this.codestack_html.size() == 0) {
				Elements divs = document.select("div");
				this.codestack_html.addAll(divs);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected String getExceptionText(String possibleException) {
		// code for getting the exception content
		String exceptionContent = new String();
		try {
			int startIndex = possibleException
					.indexOf(this.currentExceptionName);
			int lastIndex = possibleException.lastIndexOf("at ");
			exceptionContent = possibleException.substring(startIndex,
					lastIndex);

		} catch (Exception exc) {
		}
		return exceptionContent;
	}

	protected void refineCodeStacks() {
		// code for refining the codestacks
		try {
			int stack_developed = 0;
			for (Element stack : this.codestack_html) {
				String elem = stack.text();
				//this.codestack_content.add(elem); //adding raw content
				try {
					if (elem.contains(this.currentExceptionName)) {
						boolean proceed = false;
						String extractedContent = new String();
						String exceptionText=new String();
						 exceptionText = getExceptionText(stack.text());
						if (!exceptionText.isEmpty()) {
							if (RegexMatcher.matches_stacktrace(exceptionText)) {
								proceed = true;
								extractedContent = exceptionText;
							} else
								proceed = false;
						}
						if (proceed) {
							StackTraceUtils st_utils = new StackTraceUtils(
									extractedContent);
							StackTrace strace = st_utils.analyze_stack_trace();				
							if (!strace.stackTraceTokens.isEmpty()) {
								this.codestack_processed.add(strace);
								this.codestack_tokenset
										.add(strace.stackTraceTokens);
								stack_developed++;
								// System.out.println("Tokens:"+strace.stackTraceTokens);
							}
						}else{
							this.codestack_content.add(elem);
						} 
					}else{
						this.codestack_content.add(elem);
					}
				} catch (Exception exc) {
				}
			}
		} catch (Exception exc) {

		}
	}

	protected void get_text_only_content() {
		// code for text only content
		try {
			Document doc = Jsoup.parse(this.pageContent);
			Element bodyElem = doc.select("body").first();
			String bodyHtmlStr = bodyElem.toString();
			for (Element elem : codestack_html) {
				String temp = bodyHtmlStr.replace(elem.toString(), "");
				bodyHtmlStr = temp;
			}
			Document doc2 = Jsoup.parse(bodyHtmlStr);
			String codestackFreeContent = doc2.select("body").first().text();
			this.textOnlyPageContent = codestackFreeContent;
			// System.out.println(textOnlyContent);
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
	}

	public void minePageCodeStacks() {
		// code for mining all page contents
		try {
			// performing all operations sequentially
			this.collectCodeStacks();
			this.refineCodeStacks();
			this.get_text_only_content();

		} catch (Exception exc) {

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
