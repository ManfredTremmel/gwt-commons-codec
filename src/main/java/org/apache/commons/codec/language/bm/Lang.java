/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.gwt.core.shared.GwtIncompatible;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Language guessing utility.
 * <p>
 * This class encapsulates rules used to guess the possible languages that a word originates from. This is
 * done by reference to a whole series of rules distributed in resource files.
 * <p>
 * Instances of this class are typically managed through the static factory method instance().
 * Unless you are developing your own language guessing rules, you will not need to interact with this class directly.
 * <p>
 * This class is intended to be immutable and thread-safe.
 * <p>
 * <b>Lang resources</b>
 * <p>
 * Language guessing rules are typically loaded from resource files. These are UTF-8 encoded text files.
 * They are systematically named following the pattern:
 * <blockquote>org/apache/commons/codec/language/bm/lang.txt</blockquote>
 * The format of these resources is the following:
 * <ul>
 * <li><b>Rules:</b> whitespace separated strings.
 * There should be 3 columns to each row, and these will be interpreted as:
 * <ol>
 * <li>pattern: a regular expression.</li>
 * <li>languages: a '+'-separated list of languages.</li>
 * <li>acceptOnMatch: 'true' or 'false' indicating if a match rules in or rules out the language.</li>
 * </ol>
 * </li>
 * <li><b>End-of-line comments:</b> Any occurrence of '//' will cause all text following on that line to be
 * discarded as a comment.</li>
 * <li><b>Multi-line comments:</b> Any line starting with '/*' will start multi-line commenting mode.
 * This will skip all content until a line ending in '*' and '/' is found.</li>
 * <li><b>Blank lines:</b> All blank lines will be skipped.</li>
 * </ul>
 * <p>
 * Port of lang.php
 *
 * @since 1.6
 * @version $Id: Lang.java 1608115 2014-07-05 19:58:38Z tn $
 */
public class Lang {
    // Implementation note: This class is divided into two sections. The first part is a static factory interface that
    // exposes the LANGUAGE_RULES_RN resource as a Lang instance. The second part is the Lang instance methods that
    // encapsulate a particular language-guessing rule table and the language guessing itself.
    //
    // It may make sense in the future to expose the private constructor to allow power users to build custom language-
    // guessing rules, perhaps by marking it protected and allowing sub-classing. However, the vast majority of users
    // should be strongly encouraged to use the static factory <code>instance</code> method to get their Lang instances.

    private static final class LangRule {
        private final boolean acceptOnMatch;
        private final Set<String> languages;
        private final RegExp pattern;

        private LangRule(final RegExp pattern, final Set<String> languages, final boolean acceptOnMatch) {
            this.pattern = pattern;
            this.languages = languages;
            this.acceptOnMatch = acceptOnMatch;
        }

        public boolean matches(final String txt) {
            return this.pattern.test(txt);
        }
    }

    private static final Map<NameType, Lang> Langs = new EnumMap<NameType, Lang>(NameType.class);

    private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/%s_lang.txt";

    static {
        for (final NameType s : NameType.values()) {
            Langs.put(s, loadFromResource(s, Languages.getInstance(s)));
        }
    }

    /**
     * Gets a Lang instance for one of the supported NameTypes.
     *
     * @param nameType
     *            the NameType to look up
     * @return a Lang encapsulating the language guessing rules for that name type
     */
    public static Lang instance(final NameType nameType) {
        return Langs.get(nameType);
    }

    /**
     * Loads language rules from a resource.
     * <p>
     * In normal use, you will obtain instances of Lang through the {@link #instance(NameType)} method.
     * You will only need to call this yourself if you are developing custom language mapping rules.
     *
     * @param languageRulesResourceName
     *            the fully-qualified resource name to load
     * @param languages
     *            the languages that these rules will support
     * @return a Lang encapsulating the loaded language-guessing rules.
     */
    public static Lang loadFromResource(final NameType languageRulesResourceName, final Languages languages) {
        final List<LangRule> rules = new ArrayList<LangRule>();
    	switch (languageRulesResourceName) {
    		case ASHKENAZI:
    	    	rules.add(new LangRule(RegExp.compile("zh"), new HashSet<String>(Arrays.asList("polish", "russian", "german", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("eau"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("[aoeiuäöü]h"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("^vogel"), new HashSet<String>(Arrays.asList("german,")), true));
    	    	rules.add(new LangRule(RegExp.compile("vogel$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("witz"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("tz$"), new HashSet<String>(Arrays.asList("german", "russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^tz"), new HashSet<String>(Arrays.asList("russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("güe"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("güi"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ghe"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ghi"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("vici$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("schi$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("chsch"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("tsch"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("ssch"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("sch$"), new HashSet<String>(Arrays.asList("german", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^sch"), new HashSet<String>(Arrays.asList("german", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^rz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("rz$"), new HashSet<String>(Arrays.asList("polish", "german")), true));
    	    	rules.add(new LangRule(RegExp.compile("[^aoeiuäöü]rz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("rz[^aoeiuäöü]"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cki$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ska$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cka$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ue"), new HashSet<String>(Arrays.asList("german", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ae"), new HashSet<String>(Arrays.asList("german", "russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("oe"), new HashSet<String>(Arrays.asList("german", "french", "russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("th$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("^th"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("th[^aoeiu]"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("mann"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("cz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cy"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("niew"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("stein"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("heim$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("heimer$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("ii$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("iy$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("yy$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("yi$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("yj$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ij$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gaus$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gauz$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gauz$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("goltz$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gol'tz$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("golts$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gol'ts$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^goltz"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^gol'tz"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^golts"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^gol'ts"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gendler$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gejmer$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gejm$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("geimer$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("geim$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("geymer"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("geym$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gof$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("thal"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("zweig"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("ck$"), new HashSet<String>(Arrays.asList("german", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("c$"), new HashSet<String>(Arrays.asList("polish", "romanian", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("sz"), new HashSet<String>(Arrays.asList("polish", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gue"), new HashSet<String>(Arrays.asList("spanish", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("gui"), new HashSet<String>(Arrays.asList("spanish", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("guy"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("cs$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^cs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("dzs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("zs$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^zs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^wl"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("^wr"), new HashSet<String>(Arrays.asList("polish", "english", "german")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy[aeou]"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy"), new HashSet<String>(Arrays.asList("hungarian", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ly"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ny"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ty"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("â"), new HashSet<String>(Arrays.asList("romanian", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ă"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("à"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ä"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("á"), new HashSet<String>(Arrays.asList("hungarian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ą"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ć"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ç"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ę"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("é"), new HashSet<String>(Arrays.asList("french", "hungarian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("è"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ê"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("í"), new HashSet<String>(Arrays.asList("hungarian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("î"), new HashSet<String>(Arrays.asList("romanian", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ł"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ń"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ñ"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ó"), new HashSet<String>(Arrays.asList("polish", "hungarian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ö"), new HashSet<String>(Arrays.asList("german", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("õ"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ş"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ś"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ţ"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ü"), new HashSet<String>(Arrays.asList("german", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ù"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ű"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ú"), new HashSet<String>(Arrays.asList("hungarian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ź"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ż"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ß"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("а"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ё"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("о"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("е"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("и"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("у"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ы"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("э"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ю"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("я"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("א"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ב"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ג"), new HashSet<String>(Arrays.asList("ebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ד"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ה"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ו"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ז"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ח"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ט"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("י"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("כ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ל"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("מ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("נ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ס"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ע"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("פ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("צ"), new HashSet<String>(Arrays.asList("hebrew")), true)); 
    	    	rules.add(new LangRule(RegExp.compile("ק"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ר"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ש"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ת"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("a"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("o"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("e"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("i"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("y"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "romanian")), false));
    	    	rules.add(new LangRule(RegExp.compile("u"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("v[^aoeiuäüö]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("y[^aoeiu]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("c[^aohk]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("dzi"), new HashSet<String>(Arrays.asList("german", "english", "french")), false));
    	    	rules.add(new LangRule(RegExp.compile("ou"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("aj"), new HashSet<String>(Arrays.asList("german", "english", "french")), false));
    	    	rules.add(new LangRule(RegExp.compile("ej"), new HashSet<String>(Arrays.asList("german", "english", "french")), false));
    	    	rules.add(new LangRule(RegExp.compile("oj"), new HashSet<String>(Arrays.asList("german", "english", "french")), false));
    	    	rules.add(new LangRule(RegExp.compile("uj"), new HashSet<String>(Arrays.asList("german", "english", "french")), false));
    	    	rules.add(new LangRule(RegExp.compile("k"), new HashSet<String>(Arrays.asList("romanian")), false));
    	    	rules.add(new LangRule(RegExp.compile("v"), new HashSet<String>(Arrays.asList("polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("ky"), new HashSet<String>(Arrays.asList("polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("eu"), new HashSet<String>(Arrays.asList("russian", "polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("w"), new HashSet<String>(Arrays.asList("french", "romanian", "spanish", "hungarian", "russian")), false));
    	    	rules.add(new LangRule(RegExp.compile("kie"), new HashSet<String>(Arrays.asList("french", "spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("gie"), new HashSet<String>(Arrays.asList("french", "romanian", "spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("q"), new HashSet<String>(Arrays.asList("hungarian", "polish", "russian", "romanian")), false));
    	    	rules.add(new LangRule(RegExp.compile("sch"), new HashSet<String>(Arrays.asList("hungarian", "polish", "french", "spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("^h"), new HashSet<String>(Arrays.asList("russian")), false));
    			break;
    		case GENERIC:
    	    	rules.add(new LangRule(RegExp.compile("^o’"), new HashSet<String>(Arrays.asList("english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^o'"), new HashSet<String>(Arrays.asList("english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^mc"), new HashSet<String>(Arrays.asList("english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^fitz"), new HashSet<String>(Arrays.asList("english")), true));
    	    	rules.add(new LangRule(RegExp.compile("ceau"), new HashSet<String>(Arrays.asList("french", "romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("eau"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("eau$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("eaux$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ault$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("oult$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("eux$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("eix$"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("glou$"), new HashSet<String>(Arrays.asList("greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("uu"), new HashSet<String>(Arrays.asList("dutch")), true));
    	    	rules.add(new LangRule(RegExp.compile("tx"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("witz"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("tz$"), new HashSet<String>(Arrays.asList("german", "russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^tz"), new HashSet<String>(Arrays.asList("russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("poulos$"), new HashSet<String>(Arrays.asList("greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("pulos$"), new HashSet<String>(Arrays.asList("greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("iou"), new HashSet<String>(Arrays.asList("greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("sj$"), new HashSet<String>(Arrays.asList("dutch")), true));
    	    	rules.add(new LangRule(RegExp.compile("^sj"), new HashSet<String>(Arrays.asList("dutch")), true));
    	    	rules.add(new LangRule(RegExp.compile("güe"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("güi"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ghe"), new HashSet<String>(Arrays.asList("romanian", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ghi"), new HashSet<String>(Arrays.asList("romanian", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("escu$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("esco$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("vici$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("schi$"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ii$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("iy$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("yy$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("yi$"), new HashSet<String>(Arrays.asList("russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^rz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("rz$"), new HashSet<String>(Arrays.asList("polish", "german")), true));
    	    	rules.add(new LangRule(RegExp.compile("[bcdfgklmnpstwz]rz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("rz[bcdfghklmnpstw]"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cki$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ska$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cka$"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ae"), new HashSet<String>(Arrays.asList("german", "russian", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("oe"), new HashSet<String>(Arrays.asList("german", "french", "russian", "english", "dutch")), true));
    	    	rules.add(new LangRule(RegExp.compile("th$"), new HashSet<String>(Arrays.asList("german", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("^th"), new HashSet<String>(Arrays.asList("german", "english", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("mann"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("cz"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("cy"), new HashSet<String>(Arrays.asList("polish", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("niew"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("etti$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("eti$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ati$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ato$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("[aoei]no$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("[aoei]ni$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("esi$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("oli$"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("field$"), new HashSet<String>(Arrays.asList("english")), true));
    	    	rules.add(new LangRule(RegExp.compile("stein"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("heim$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("heimer$"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("thal"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("zweig"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("[aeou]h"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("äh"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("öh"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("üh"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("[ln]h[ao]$"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("[ln]h[aou]"), new HashSet<String>(Arrays.asList("portuguese", "french", "german", "dutch", "czech", "spanish", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("chsch"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("tsch"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("sch$"), new HashSet<String>(Arrays.asList("german", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^sch"), new HashSet<String>(Arrays.asList("german", "russian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ck$"), new HashSet<String>(Arrays.asList("german", "english")), true));
    	    	rules.add(new LangRule(RegExp.compile("c$"), new HashSet<String>(Arrays.asList("polish", "romanian", "hungarian", "czech", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("sz"), new HashSet<String>(Arrays.asList("polish", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("cs$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^cs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("dzs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("zs$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^zs"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("^wl"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("^wr"), new HashSet<String>(Arrays.asList("polish", "english", "german", "dutch")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy$"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy[aeou]"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy"), new HashSet<String>(Arrays.asList("hungarian", "russian", "french", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("guy"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("gu[ei]"), new HashSet<String>(Arrays.asList("spanish", "french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("gu[ao]"), new HashSet<String>(Arrays.asList("spanish", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("gi[aou]"), new HashSet<String>(Arrays.asList("italian", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ly"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ny"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish", "spanish", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ty"), new HashSet<String>(Arrays.asList("hungarian", "russian", "polish", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ć"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ç"), new HashSet<String>(Arrays.asList("french", "spanish", "portuguese", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("č"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ď"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ğ"), new HashSet<String>(Arrays.asList("turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ł"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ń"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ñ"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ň"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ř"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ś"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ş"), new HashSet<String>(Arrays.asList("romanian", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("š"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ţ"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ť"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ź"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ż"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ß"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("ä"), new HashSet<String>(Arrays.asList("german")), true));
    	    	rules.add(new LangRule(RegExp.compile("á"), new HashSet<String>(Arrays.asList("hungarian", "spanish", "portuguese", "czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("â"), new HashSet<String>(Arrays.asList("romanian", "french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ă"), new HashSet<String>(Arrays.asList("romanian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ą"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("à"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ã"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ę"), new HashSet<String>(Arrays.asList("polish")), true));
    	    	rules.add(new LangRule(RegExp.compile("é"), new HashSet<String>(Arrays.asList("french", "hungarian", "czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("è"), new HashSet<String>(Arrays.asList("french", "spanish", "italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ê"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ě"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ê"), new HashSet<String>(Arrays.asList("french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("í"), new HashSet<String>(Arrays.asList("hungarian", "spanish", "portuguese", "czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("î"), new HashSet<String>(Arrays.asList("romanian", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ı"), new HashSet<String>(Arrays.asList("turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ó"), new HashSet<String>(Arrays.asList("polish", "hungarian", "spanish", "italian", "portuguese", "czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ö"), new HashSet<String>(Arrays.asList("german", "hungarian", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ô"), new HashSet<String>(Arrays.asList("french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("õ"), new HashSet<String>(Arrays.asList("portuguese", "hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ò"), new HashSet<String>(Arrays.asList("italian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ű"), new HashSet<String>(Arrays.asList("hungarian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ú"), new HashSet<String>(Arrays.asList("hungarian", "spanish", "portuguese", "czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("ü"), new HashSet<String>(Arrays.asList("german", "hungarian", "spanish", "portuguese", "turkish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ù"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ů"), new HashSet<String>(Arrays.asList("czech")), true));
    	    	rules.add(new LangRule(RegExp.compile("ý"), new HashSet<String>(Arrays.asList("czech", "greeklatin")), true));
    	    	rules.add(new LangRule(RegExp.compile("а"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ё"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("о"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("е"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("и"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("у"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ы"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("э"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ю"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("я"), new HashSet<String>(Arrays.asList("cyrillic")), true));
    	    	rules.add(new LangRule(RegExp.compile("α"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("ε"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("η"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("ι"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("ο"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("υ"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("ω"), new HashSet<String>(Arrays.asList("greek")), true));
    	    	rules.add(new LangRule(RegExp.compile("ا"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ب"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ت"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ث"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ج"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ح"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("خ'"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("د"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ذ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ر"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ز"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("س"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ش"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ص"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ض"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ط"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ظ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ع"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("غ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ف"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ق"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ك"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ل"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("م"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ن"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ه"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("و"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ي"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("آ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("إ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("أ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ؤ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("ئ"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("لا"), new HashSet<String>(Arrays.asList("arabic")), true));
    	    	rules.add(new LangRule(RegExp.compile("א"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ב"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ג"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ד"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ה"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ו"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ז"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ח"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ט"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("י"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("כ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ל"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("מ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("נ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ס"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ע"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("פ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("צ"), new HashSet<String>(Arrays.asList("hebrew")), true)); 
    	    	rules.add(new LangRule(RegExp.compile("ק"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ר"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ש"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ת"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("a"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic")), false));
    	    	rules.add(new LangRule(RegExp.compile("o"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic")), false));
    	    	rules.add(new LangRule(RegExp.compile("e"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic")), false));
    	    	rules.add(new LangRule(RegExp.compile("i"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic")), false));
    	    	rules.add(new LangRule(RegExp.compile("y"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic", "romanian", "dutch")), false));
    	    	rules.add(new LangRule(RegExp.compile("u"), new HashSet<String>(Arrays.asList("cyrillic", "hebrew", "greek", "arabic")), false));
    	    	rules.add(new LangRule(RegExp.compile("j"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("j[^aoeiuy]"), new HashSet<String>(Arrays.asList("french", "spanish", "portuguese", "greeklatin")), false));
    	    	rules.add(new LangRule(RegExp.compile("g"), new HashSet<String>(Arrays.asList("czech")), false));
    	    	rules.add(new LangRule(RegExp.compile("k"), new HashSet<String>(Arrays.asList("romanian", "spanish", "portuguese", "french", "italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("q"), new HashSet<String>(Arrays.asList("hungarian", "polish", "russian", "romanian", "czech", "dutch", "turkish", "greeklatin")), false));
    	    	rules.add(new LangRule(RegExp.compile("v"), new HashSet<String>(Arrays.asList("polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("w"), new HashSet<String>(Arrays.asList("french", "romanian", "spanish", "hungarian", "russian", "czech", "turkish", "greeklatin")), false));
    	    	rules.add(new LangRule(RegExp.compile("x"), new HashSet<String>(Arrays.asList("czech", "hungarian", "dutch", "turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("dj"), new HashSet<String>(Arrays.asList("spanish", "turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("v[^aoeiu]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("y[^aoeiu]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("c[^aohk]"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("dzi"), new HashSet<String>(Arrays.asList("german", "english", "french", "turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("ou"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("a[eiou]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("ö[eaiou]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("ü[eaiou]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("e[aiou]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("i[aeou]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("o[aieu]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("u[aieo]"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("aj"), new HashSet<String>(Arrays.asList("german", "english", "french", "dutch")), false));
    	    	rules.add(new LangRule(RegExp.compile("ej"), new HashSet<String>(Arrays.asList("german", "english", "french", "dutch")), false));
    	    	rules.add(new LangRule(RegExp.compile("oj"), new HashSet<String>(Arrays.asList("german", "english", "french", "dutch")), false));
    	    	rules.add(new LangRule(RegExp.compile("uj"), new HashSet<String>(Arrays.asList("german", "english", "french", "dutch")), false));
    	    	rules.add(new LangRule(RegExp.compile("eu"), new HashSet<String>(Arrays.asList("russian", "polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("ky"), new HashSet<String>(Arrays.asList("polish")), false));
    	    	rules.add(new LangRule(RegExp.compile("kie"), new HashSet<String>(Arrays.asList("french", "spanish", "greeklatin")), false));
    	    	rules.add(new LangRule(RegExp.compile("gie"), new HashSet<String>(Arrays.asList("portuguese", "romanian", "spanish", "greeklatin")), false));
    	    	rules.add(new LangRule(RegExp.compile("ch[aou]"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("ch"), new HashSet<String>(Arrays.asList("turkish")), false));
    	    	rules.add(new LangRule(RegExp.compile("son$"), new HashSet<String>(Arrays.asList("german")), false));
    	    	rules.add(new LangRule(RegExp.compile("sc[ei]"), new HashSet<String>(Arrays.asList("french")), false));
    	    	rules.add(new LangRule(RegExp.compile("sch"), new HashSet<String>(Arrays.asList("hungarian", "polish", "french", "spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("^h"), new HashSet<String>(Arrays.asList("russian")), false));
    			break;
    		case SEPHARDIC:
    	    	rules.add(new LangRule(RegExp.compile("eau"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ou"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("gni"), new HashSet<String>(Arrays.asList("italian", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("tx"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("tj"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("gy"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("guy"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("sh"), new HashSet<String>(Arrays.asList("spanish", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("lh"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("nh"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ny"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("gue"), new HashSet<String>(Arrays.asList("spanish", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("gui"), new HashSet<String>(Arrays.asList("spanish", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("gia"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gie"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("gio"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("giu"), new HashSet<String>(Arrays.asList("italian")), true));
    	    	rules.add(new LangRule(RegExp.compile("ñ"), new HashSet<String>(Arrays.asList("spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("â"), new HashSet<String>(Arrays.asList("portuguese", "french")), true));
    	    	rules.add(new LangRule(RegExp.compile("á"), new HashSet<String>(Arrays.asList("portuguese", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("à"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ã"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ê"), new HashSet<String>(Arrays.asList("french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("í"), new HashSet<String>(Arrays.asList("portuguese", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("î"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ô"), new HashSet<String>(Arrays.asList("french", "portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("õ"), new HashSet<String>(Arrays.asList("portuguese")), true));
    	    	rules.add(new LangRule(RegExp.compile("ò"), new HashSet<String>(Arrays.asList("italian", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ú"), new HashSet<String>(Arrays.asList("portuguese", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("ù"), new HashSet<String>(Arrays.asList("french")), true));
    	    	rules.add(new LangRule(RegExp.compile("ü"), new HashSet<String>(Arrays.asList("portuguese", "spanish")), true));
    	    	rules.add(new LangRule(RegExp.compile("א"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ב"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ג"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ד"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ה"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ו"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ז"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ח"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ט"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("י"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("כ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ל"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("מ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("נ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ס"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ע"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("פ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("צ"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ק"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ר"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ש"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("ת"), new HashSet<String>(Arrays.asList("hebrew")), true));
    	    	rules.add(new LangRule(RegExp.compile("a"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("o"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("e"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("i"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("y"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("u"), new HashSet<String>(Arrays.asList("hebrew")), false));
    	    	rules.add(new LangRule(RegExp.compile("kh"), new HashSet<String>(Arrays.asList("spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("gua"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("guo"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("ç"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("cha"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("cho"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("chu"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("j"), new HashSet<String>(Arrays.asList("italian")), false));
    	    	rules.add(new LangRule(RegExp.compile("dj"), new HashSet<String>(Arrays.asList("spanish")), false));
    	    	rules.add(new LangRule(RegExp.compile("sce"), new HashSet<String>(Arrays.asList("french")), false));
    	    	rules.add(new LangRule(RegExp.compile("sci"), new HashSet<String>(Arrays.asList("french")), false));
    	    	rules.add(new LangRule(RegExp.compile("ó"), new HashSet<String>(Arrays.asList("french")), false));
    	    	rules.add(new LangRule(RegExp.compile("è"), new HashSet<String>(Arrays.asList("portuguese")), false));
    			break;
    		default:
    			break;
    			
    	}
        return new Lang(rules, languages);
    }

    /**
     * Loads language rules from a resource.
     * <p>
     * In normal use, you will obtain instances of Lang through the {@link #instance(NameType)} method.
     * You will only need to call this yourself if you are developing custom language mapping rules.
     *
     * @param languageRulesResourceName
     *            the fully-qualified resource name to load
     * @param languages
     *            the languages that these rules will support
     * @return a Lang encapsulating the loaded language-guessing rules.
     */
    @GwtIncompatible("incompatible method")
    public static Lang loadFromResource(final String languageRulesResourceName, final Languages languages) {
        final List<LangRule> rules = new ArrayList<LangRule>();
        final InputStream lRulesIS = Lang.class.getClassLoader().getResourceAsStream(languageRulesResourceName);

        if (lRulesIS == null) {
            throw new IllegalStateException("Unable to resolve required resource:" + LANGUAGE_RULES_RN);
        }

        final Scanner scanner = new Scanner(lRulesIS, ResourceConstants.ENCODING);
        try {
            boolean inExtendedComment = false;
            while (scanner.hasNextLine()) {
                final String rawLine = scanner.nextLine();
                String line = rawLine;
                if (inExtendedComment) {
                    // check for closing comment marker, otherwise discard doc comment line
                    if (line.endsWith(ResourceConstants.EXT_CMT_END)) {
                        inExtendedComment = false;
                    }
                } else {
                    if (line.startsWith(ResourceConstants.EXT_CMT_START)) {
                        inExtendedComment = true;
                    } else {
                        // discard comments
                        final int cmtI = line.indexOf(ResourceConstants.CMT);
                        if (cmtI >= 0) {
                            line = line.substring(0, cmtI);
                        }

                        // trim leading-trailing whitespace
                        line = line.trim();

                        if (line.length() == 0) {
                            continue; // empty lines can be safely skipped
                        }

                        // split it up
                        final String[] parts = line.split("\\s+");

                        if (parts.length != 3) {
                            throw new IllegalArgumentException("Malformed line '" + rawLine +
                                    "' in language resource '" + languageRulesResourceName + "'");
                        }

                        final RegExp pattern = RegExp.compile(parts[0]);
                        final String[] langs = parts[1].split("\\+");
                        final boolean accept = parts[2].equals("true");

                        rules.add(new LangRule(pattern, new HashSet<String>(Arrays.asList(langs)), accept));
                    }
                }
            }
        } finally {
            scanner.close();
        }
        return new Lang(rules, languages);
    }

    private final Languages languages;
    private final List<LangRule> rules;

    private Lang(final List<LangRule> rules, final Languages languages) {
        this.rules = Collections.unmodifiableList(rules);
        this.languages = languages;
    }

    /**
     * Guesses the language of a word.
     *
     * @param text
     *            the word
     * @return the language that the word originates from or {@link Languages#ANY} if there was no unique match
     */
    public String guessLanguage(final String text) {
        final Languages.LanguageSet ls = guessLanguages(text);
        return ls.isSingleton() ? ls.getAny() : Languages.ANY;
    }

    /**
     * Guesses the languages of a word.
     *
     * @param input
     *            the word
     * @return a Set of Strings of language names that are potential matches for the input word
     */
    public Languages.LanguageSet guessLanguages(final String input) {
        final String text = input.toLowerCase(Locale.ENGLISH);

        final Set<String> langs = new HashSet<String>(this.languages.getLanguages());
        for (final LangRule rule : this.rules) {
            if (rule.matches(text)) {
                if (rule.acceptOnMatch) {
                    langs.retainAll(rule.languages);
                } else {
                    langs.removeAll(rule.languages);
                }
            }
        }

        final Languages.LanguageSet ls = Languages.LanguageSet.from(langs);
        return ls.equals(Languages.NO_LANGUAGES) ? Languages.ANY_LANGUAGE : ls;
    }
}
