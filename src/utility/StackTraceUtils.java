package utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import core.StackTrace;
import core.StackTraceElem;

public class StackTraceUtils {

	ArrayList<String> stack_packages;
	ArrayList<String> stack_methods;
	ArrayList<String> stack_fileNames;
	String stacktrace;
	String exceptionName;
	String errorMessage;
	String StackTraceTokenStr = new String();
	ArrayList<StackTraceElem> stackElements;

	public StackTraceUtils(String stacktrace) {
		this.stacktrace = format_the_stacktrace(stacktrace);
		this.stack_packages = new ArrayList<String>();
		this.stack_methods = new ArrayList<String>();
		this.stack_fileNames = new ArrayList<String>();
		this.stackElements = new ArrayList<StackTraceElem>();
	}

	protected String format_the_stacktrace(String strace) {
		// code for formatting the stack trace
		String newcontent = new String();
		try {
			String[] lines = strace.split("\\s*at\\s+");
			newcontent = lines[0].trim().replace("\n", "") + "\n";
			for (int i = 1; i < lines.length; i++) {
				lines[i] = lines[i].replace("\n", "");
				newcontent += "at " + lines[i] + "\n";
			}
		} catch (Exception exc) {
			newcontent = strace;
		}
		return newcontent.trim();
	}

	public String get_error_message() {
		// code for getting error message from stack trace
		String[] lines = this.stacktrace.split("\n");
		String temp = new String();
		for (String line : lines) {
			if (!line.startsWith("!") && !line.startsWith("at")) {
				temp = line;
				break;
			}
		}
		if (RegexMatcher.matches_exception_name(temp)) {
			// removing the file name Windows file specific
			String filepathRegex = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9\\s\\._-]+)+\\\\?";
			try {
				Pattern p = Pattern.compile(filepathRegex);
				Matcher matcher = p.matcher(temp);
				if (matcher.find()) {
					String filePath = temp.substring(matcher.start(),
							matcher.end());
					String newtemp = temp.replace(filePath, " ");
					this.errorMessage = newtemp;
				} else
					this.errorMessage = temp;

				this.errorMessage = this.errorMessage.replaceAll(":", " ");

			} catch (Exception exc) {
			}
		}
		return this.errorMessage;
	}

	public String get_error_without_exception_name() {
		// code for getting error without exception name
		String error_only_message = new String();
		String temp = get_error_message();
		try {
			String exceptionNameRegex = "^.+Exception(:)?";
			Pattern pattern = Pattern.compile(exceptionNameRegex);
			Matcher matcher = pattern.matcher(temp);
			if (matcher.find()) {
				int end = matcher.end();
				if (end < temp.length()) {
					error_only_message = temp.substring(end + 1);
				}
			}
		} catch (Exception exc) {
		}
		return error_only_message;
	}

	public String extract_exception_name() {
		// code for extracting exception name from error message
		String errorMessage = get_error_message();
		String exceptionName = new String();
		try {
			String exceptionNameRegex = "^.+Exception(:)?";
			Pattern pattern = Pattern.compile(exceptionNameRegex);
			Matcher matcher = pattern.matcher(errorMessage);
			if (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String tempStr = errorMessage.substring(start, end);
				String[] parts = tempStr.split("\\s+");
				if (parts.length > 1)
					exceptionName = parts[parts.length - 1];
				else
					exceptionName = tempStr;
			}
		} catch (Exception exc) {
		}
		return exceptionName;
	}

	protected StackTraceElem analyze_stack_trace_line(String line) {
		// code for analyzing the stack trace line
		StackTraceElem elem = new StackTraceElem();
		String traceLineTokens = new String();
		try {
			line = line.trim();
			if (!line.isEmpty()) {
				line = line.substring(2).trim(); // line without at
				int leftBraceIndex = line.indexOf("(");
				// System.out.println(line);
				String canonical_method = line.substring(0, leftBraceIndex)
						.trim();
				String[] parts = canonical_method.split("\\.");

				// package, class and method
				if (parts.length > 2) {
					int last = parts.length - 1;
					elem.methodName = parts[last];
					elem.className = parts[last - 1];
					String pack_name = new String();
					for (int i = 0; i < last - 1; i++)
						pack_name += "." + parts[i];
					pack_name = pack_name.substring(1);
					// System.out.println(pack_name);
					elem.packageName = pack_name;
					traceLineTokens = elem.packageName + " " + elem.className
							+ " " + elem.methodName;
				} else if (parts.length == 2) {
					elem.methodName = parts[1];
					elem.className = parts[0];
					traceLineTokens = elem.className + " " + elem.methodName;
				} else {
					// do nothing.
				}
				// Method call line number
				try {
					int rightBraceLoc = line.lastIndexOf(")");
					String braced_part = line.substring(leftBraceIndex + 1,
							rightBraceLoc);
					String[] parts2 = {};
					parts2 = braced_part.split(":");
					if (parts2.length == 2) {
						elem.methodCallLineNumber = Integer.parseInt(parts2[1]);
						// traceLineTokens+=" "+elem.methodCallLineNumber;
					}
				} catch (Exception exc) {
				}

				elem.traceLineTokens = traceLineTokens;
			}
		} catch (Exception exc) {
		}
		// returning the element
		return elem;
	}

	public ArrayList<StackTraceElem> get_trace_elem_for_lines() {
		// code for getting trace element for all lines
		String[] lines = this.stacktrace.split("\n");
		ArrayList<StackTraceElem> elems = new ArrayList<>();
		try {
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i].trim();
				if (line.startsWith("at")) {
					StackTraceElem elem = analyze_stack_trace_line(lines[i]);
					elems.add(elem);
				}
			}
		} catch (Exception exc) {
		}
		return elems;
	}

	public StackTrace analyze_stack_trace() {
		// code for analyzing stack trace
		StackTrace trace = new StackTrace();
		try {
			if (!RegexMatcher.matches_stacktrace(this.stacktrace))
				return trace;

			// formatting the stack trace
			this.stacktrace = format_the_stacktrace(this.stacktrace);

			trace.primaryContent = this.stacktrace;
			trace.error_message = this.get_error_without_exception_name();
			trace.exception_name = this.extract_exception_name();
			trace.complete_exception_message = this.get_error_message();
			trace.TraceElems = this.get_trace_elem_for_lines();
			String tokens = new String();

			// building token string for stack trace
			tokens = trace.exception_name + " " + trace.error_message;
			String trace_tokens = new String();
			for (int i = 0; i < trace.TraceElems.size(); i++)
				trace_tokens += " " + trace.TraceElems.get(i).traceLineTokens;

			if (trace_tokens.isEmpty()) {
				trace.stackTraceTokens = "";
			} else {
				trace.stackTraceTokens = tokens + " " + trace_tokens;
			}
		} catch (Exception exc) {
		}
		// returning trace
		return trace;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String
		// error="Primefaces exception INFO: java.lang.ArithmeticException: / by zero java.lang.ArithmeticException: / by zero";
		// String exceptionName=extract_exception_name(error);
		// if(!exceptionName.isEmpty())System.out.println(" "+exceptionName);
		String stackTrace = "java.io.FileNotFoundException:  (The system cannot find the path specified)";// load_stack_trace();
		StackTraceUtils utils = new StackTraceUtils(stackTrace);
		StackTrace trace=utils.analyze_stack_trace();
		// System.out.println(trace.stackTraceTokens);
		System.out.println(utils.extract_exception_name());

	}

}
