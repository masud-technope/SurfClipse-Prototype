package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import core.Result;

public class DownloadResultEntryContent {

	/**
	 * @param args
	 */

	ArrayList<Result> EntryList;

	public DownloadResultEntryContent(ArrayList<Result> EntryList) {
		this.EntryList = EntryList;
	}

	protected String test_download() {
		String temp = new String();
		try {
			String resultURL = "http://stackoverflow.com/questions/17003576/json-data-to-net-class";
			check_and_proceed_file_size(resultURL);
			URL url = new URL(resultURL);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			// String temp = new String();
			String line = null;
			while ((line = br.readLine()) != null) {
				temp += line + "\n";
			}
			// System.out.println(temp);
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
		return temp;
	}

	protected boolean contained_in_black_list(ArrayList<String> blackList,
			String resultURL) {
		boolean response = false;
		// code for checking black list
		for (String myurl : blackList) {
			if (resultURL.contains(myurl)) {
				response = true;
				break;
			}
		}
		return response;
	}

	protected boolean invalid_extension(String[] exts, String resultURL) {
		// code for checking invalid extension
		boolean response = false;

		try {
			URL u = new URL(resultURL);
			String fileName = u.getFile();
			// System.out.println("File name:"+fileName);
			for (String ext : exts) {
				if (!fileName.isEmpty())
					if (fileName.endsWith(ext)) {
						response = true;
						break;
					}
			}
		} catch (Exception exc) {
		}
		return response;
	}

	protected boolean check_and_proceed_file_size(String resultURL) {
		// code for checking header size
		boolean proceed = false;
		try {
			URL u = new URL(resultURL);
			String headerContent = new String();
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("HEAD");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				conn.getInputStream();
				long length = conn.getContentLengthLong();
				System.out.println(headerContent);
			}
		} catch (Exception exc) {
		}
		return proceed;
	}

	protected String download_page_content(String resultURL) {
		String temp = new String();
		try {
			// code for downloading page content
			URL u = new URL(resultURL);
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();
			connection.setConnectTimeout(2000); // setting connect time out
			connection.setReadTimeout(2000); // setting read time out
			connection.addRequestProperty("Content-Type",
					"text/html;charset=utf-8");
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = null;
				while ((line = br.readLine()) != null) {
					temp += line + "\n";
				}
			}
		} catch (Exception exc) {
		}
		return temp;
	}

	public ArrayList<Result> download_result_entry_content() {

		// code for downloading the content
		ArrayList<String> blackList = new ArrayList<>();
		blackList.add("http://kth.diva-portal.org");
		blackList.add("http://bugs.powerflasher.com");
		blackList.add("http://www.talendforge.org");
		blackList.add("http://www.youtube.com");
		blackList
				.add("http://ntnu.diva-portal.org/smash/get/diva2:350297/ATTACHMENT01");
		blackList
				.add("http://lists.alioth.debian.org/pipermail/pkg-java-commits/2009-November/011124.html");
		blackList.add("http://osdir.com");
		blackList.add("http://svn.apache.org");
		blackList.add("http://bugs.jython.org");
		blackList.add("http://bugs.jython.org");
		blackList.add("http://datavision.sourceforge.net");
		blackList.add("http://grepcode.com");
		blackList.add("http://prefuse.org");
		blackList.add("http://docs.jboss.org");
		blackList.add("http://wush.net");
		blackList.add("http://www.public.iastate.edu/~java/docs/api/");
		blackList.add("http://kickjava.com/allsrc.html");
		blackList.add("http://www.scribd.com/doc/147340763/resD");
		blackList.add("http://lists.jboss.org");
		blackList.add("http://www.enerjy.com");
		blackList
				.add("http://h50146.www5.hp.com/products/software/oe/linux/mainstream/support/matrix/rpm_reverse/x86_fc10/fedora10_x86_rrpm1_3.html");
		blackList.add("http://marc.info/?l=groovy-scm&m=123575967823381");
		blackList.add("http://pydev.org/history_pydev.html");
		blackList.add("https://confluence.atlassian.com");
		blackList
				.add("http://www.modeliosoft.com/example/javadoc_SWT/index-all.html");
		blackList.add("http://www.shopwiki.co.uk/l/system-software-pro");
		blackList.add("http://windowsitpro.com/");
		blackList.add("http://www.yellowpages.com/");
		blackList.add("http://en.usenet.nl");
		blackList.add("http://www.docstoc.com");
		blackList.add("www.zingeducation-us.com");
		blackList.add("http://www.maine.gov");
		blackList.add("http://cvalcarcel.wordpress.com/2009/07/");
		blackList.add("http://www.sevenforums.com/");
		blackList
				.add("http://technet.microsoft.com/en-us/library/bb457126.aspx");
		blackList.add("http://184.168.85.207/xampp/guestbook-pl.pl");
		blackList.add("http://www.ihi.org/pages/termsofuse.aspx");
		blackList.add("https://www.trustedchoice.com");
		blackList.add("http://office.microsoft.com");
		blackList.add("http://www.sciencedirect.com");
		blackList
				.add("http://typo3.org/documentation/document-library/core-documentation/doc_l10nguide/1.1.0/view/1/3/");
		blackList.add("http://online.wsj.com");
		blackList.add("http://www.opensourcecms.com");
		blackList
				.add("http://www.cardiff.ac.uk/humrs/staffinfo/support/disabled_support.html");
		blackList
				.add("http://msroth.wordpress.com/documentum-errors/documentum-server-6-6-errors/");
		blackList
				.add("http://download.java.net/jdk8/docs/api/compact3-summary.html");
		blackList.add("http://www.gamasutra.com");
		blackList.add("http://hbase.apache.org/book.html");
		blackList.add("http://www.scribd.com/");
		blackList
				.add("http://www.linuxtopia.org/online_books/eclipse_documentation");
		blackList.add("http://code.google.com/p/glasnost");
		blackList
				.add("http://www.oracle.com/technetwork/middleware/docs/aiasoarelnotesps5-1455925.html");
		blackList.add("http://jmvanel.free.fr/computer-notes.html");
		blackList
				.add("https://eclipse.googlesource.com/webservices/webtools.webservices/+/R1_5_maintenance_patches%5E2..R1_5_maintenance_patches/");
		blackList
				.add("http://edwin.baculsoft.com/2010/09/a-simple-java-client-server-application-using-ejb3-and-glassfish3/");
		blackList.add("http://www.cr173.com/down.asp?id=40880");
		blackList
				.add("http://www.complang.tuwien.ac.at/anton/lvas/effizienz-aufgabe12/input2");
		blackList
				.add("https://www.labkey.org/project/home/CPAS/Forum/begin.view");
		blackList
				.add("http://docs.oracle.com/javase/7/docs/api/index.html?java/lang/class-use/String.html");
		blackList
				.add("http://fs1.d-h.st/download/00069/8CY/configuration/debug.htm");
		blackList
				.add("http://www.docjar.com/jar_detail/osgi-3.5.0-v20090520.jar.html");
		blackList
				.add("http://mavenhub.com/mvn/central/org.ow2.jasmine/mapping/1.0.1");
		blackList
				.add("http://www.findjar.com/index.x?query=invalidcontentexception");
		blackList.add("pastebin.com");

		String[] extension = new String[] { "pdf", "txt", "doc", "docx", "jar",
				"ppt", "flv", "mov", "avi", "zip", "gz", "tar", "xml", "log" };

		for (Result result : this.EntryList) {
			// Result result=this.EntryList.get(i);
			if (result.resultContent.isEmpty()) {
				// System.out.println("Downloading ... " + result.resultURL);
				try {
					String resultURL = result.resultURL;
					if (!contained_in_black_list(blackList, resultURL)) {
						if (!invalid_extension(extension, resultURL)) {
							String temp = new String();
							try {
								temp = download_page_content(resultURL);
							} catch (Exception exc) {
							}
							result.resultContent = temp;
						}
					}
				} catch (Exception exc) {
					// System.err.println(exc.getMessage());
					// exc.printStackTrace();
				}
			}
		}
		return this.EntryList;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Result result = new Result();
		result.resultURL = "http://stackoverflow.com/questions/17003576/json-data-to-net-class";
		ArrayList<Result> test = new ArrayList<>();
		test.add(result);
		DownloadResultEntryContent dre = new DownloadResultEntryContent(test);
		// dre.download_result_entry_content();
		dre.test_download();
	}

}
