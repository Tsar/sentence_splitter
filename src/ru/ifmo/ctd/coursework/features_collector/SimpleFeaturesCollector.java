package ru.ifmo.ctd.coursework.features_collector;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.ctd.coursework.ml.svm.Test;

public class SimpleFeaturesCollector extends FeaturesCollector {
	
	private boolean isPunctuationMark(char c) {
		return (c == '.' || c == '?' || c == '!' || c == ',' || c == ':' ||
		        c == ';' || c == '\'' || c == '"' || c == '-');
	}
	
	private boolean isRightBracket(char c) {
		return c == '}' || c == ']' || c == ')';
	}
	
	private boolean isLeftBracket(char c) {
		return c == '{' || c == '[' || c == '(';
	}
	
	// Expecting that there is line ending between sentences and no other line endings in s
	@Override
	public Test[] extractFeatures(String s) {
		s = s.replaceAll("\\s+\\n", "\\n");
		List<Test> tests = new ArrayList<Test>();
		for (int i = 1; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == '.' || c == '?' || c == '!') {
				int y = (i + 1 == s.length() || s.charAt(i + 1) == '\n' || s.charAt(i + 1) == '\r') ? 1 : -1;
				
				final int FEATURES_COUNT = 15;
				double features[] = new double[FEATURES_COUNT];

				int cur = 0;  // Current feature number

				// 1. Space on the left
				features[cur++] = (s.charAt(i - 1) == ' ') ? 1.0 : 0.0;

				// 2. Space on the right (end of line counting as space, it was needed only to know correct answer)
				features[cur++] = (i + 1 == s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\n' || s.charAt(i + 1) == '\r') ? 1.0 : 0.0;
				
				// 3. Looks like shortening (x. xx. xxx.)
				int p1 = i - 1;
				while (p1 >= 0 && Character.isLetter(s.charAt(p1)))
					--p1;
				features[cur++] = (p1 >= 0 && (s.charAt(p1) == ' ' || s.charAt(p1) == '\n' || s.charAt(p1) == '\r' || (s.charAt(p1) == '-' && p1 - 1 >= 0 && s.charAt(p1 - 1) == '.'))) ? 6.0 - i + p1 : 0.0;

				for (int cycle2 = 0; cycle2 < 2; ++cycle2) {
					int p;
					if (cycle2 == 0) {
						p = i - 1;
						while (p >= 0 && (s.charAt(p) == ' ' || s.charAt(p) == '\n' || s.charAt(p) == '\r'))
							--p;
					} else {
						p = i + 1;
						while (p < s.length() && (s.charAt(p) == ' ' || s.charAt(p) == '\n' || s.charAt(p) == '\r'))
							++p;
					}
					
					if (p >= 0 && p < s.length()) {
						char x = s.charAt(p);
	
						features[cur++] = isPunctuationMark(x) ? 1.0 : 0.0;
						features[cur++] = Character.isDigit(x) ? 1.0 : 0.0;
						features[cur++] = Character.isUpperCase(x) ? 1.0 : 0.0;
						features[cur++] = Character.isLowerCase(x) ? 1.0 : 0.0;
						features[cur++] = isLeftBracket(x) ? 1.0 : 0.0;
						features[cur++] = isRightBracket(x) ? 1.0 : 0.0;
					}
				}

				tests.add(new Test(features, FEATURES_COUNT, y));
			}
		}
		return tests.toArray(new Test[0]);
	}
	
	public String toString() {
		return "SimpleFeaturesCollector";
	}
}
