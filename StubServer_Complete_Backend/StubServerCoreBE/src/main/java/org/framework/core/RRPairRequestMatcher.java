package org.framework.core;

import com.stubio.util.Argument;
import com.stubio.util.Request;

import com.jayway.jsonpath.JsonPath;
import com.sun.net.httpserver.Headers;

import org.framework.properties.Context;
import org.framework.properties.MockRequest;
import org.framework.utils.GroovyUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Evaluates an RRPair's {@code vs:Request} match criteria (RequestParameters /
 * Headers / QueryParams) against a live MockRequest - the "Operation"
 * MatchStyle counterpart to script-based matching. Used by ResponseResolver
 * to walk an operation's RRPair chain in document order.
 */
public final class RRPairRequestMatcher {

    private RRPairRequestMatcher() {
    }

    /**
     * An RRPair with no Request criteria at all matches unconditionally - the
     * same "catch-all" convention the sample XML relies on for a trailing
     * default pair.
     */
    public static boolean matches(Request spec, Context context, MockRequest request) {
        if (spec == null) {
            return true;
        }

        Map<String, String> echoes = new LinkedHashMap<>();

        if (!matchGroup(spec.getRequestParameters(),
                name -> extractBodyField(request.getRequestContent(), name), echoes)) {
            return false;
        }
        if (!matchGroup(spec.getHeaders(),
                name -> extractHeader(request.getRequestHeaders(), name), echoes)) {
            return false;
        }
        if (!matchGroup(spec.getQueryParams(),
                name -> extractQueryParam(request.getQueryString(), name), echoes)) {
            return false;
        }

        // Only capture values into context once the whole pair has matched.
        context.getDynamicProperties().putAll(echoes);
        return true;
    }

    private static boolean matchGroup(List<Argument> args, Function<String, String> extractor,
            Map<String, String> echoes) {
        if (args == null || args.isEmpty()) {
            return true;
        }
        for (Argument arg : args) {
            if (arg.getName() == null || arg.getName().isBlank()) {
                continue;
            }
            String actual = extractor.apply(arg.getName());
            if (!compare(actual, arg.getValue(), arg.getMatchType(), arg.isCaseSensitive())) {
                return false;
            }
            if ("true".equalsIgnoreCase(arg.isEchoValue())) {
                echoes.put(arg.getName(), actual);
            }
        }
        return true;
    }

    /** RequestParameters carry a bare field name; JSON body first, then XML. */
    private static String extractBodyField(String body, String name) {
        if (body == null || body.isBlank() || name == null || name.isBlank()) {
            return null;
        }
        String trimmed = body.trim();
        try {
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                Object value = JsonPath.parse(trimmed).read("$." + name);
                return value == null ? null : value.toString();
            }
            return new GroovyUtils(null).getXmlHolder(trimmed).getNodeValue("//" + name);
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractHeader(Headers headers, String name) {
        return headers == null || name == null ? null : headers.getFirst(name);
    }

    private static String extractQueryParam(String queryString, String name) {
        if (queryString == null || queryString.isBlank() || name == null) {
            return null;
        }
        for (String pair : queryString.split("&")) {
            int eq = pair.indexOf('=');
            String key = eq >= 0 ? pair.substring(0, eq) : pair;
            if (!key.equals(name)) {
                continue;
            }
            String value = eq >= 0 ? pair.substring(eq + 1) : "";
            try {
                return URLDecoder.decode(value, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return value;
            }
        }
        return null;
    }

    /**
     * matchType vocabulary is inconsistent in the sample contract (symbols in
     * one RRPair, words in another) - normalize both to the same operator set.
     */
    private static boolean compare(String actual, String expected, String matchType, String caseSensitiveAttr) {
        if (actual == null) {
            return false;
        }

        boolean caseSensitive = !"false".equalsIgnoreCase(caseSensitiveAttr);
        String a = caseSensitive ? actual : actual.toLowerCase();
        String e = expected == null ? null : (caseSensitive ? expected : expected.toLowerCase());

        switch (normalizeMatchType(matchType)) {
            case "NOT_EQUALS":
                return !Objects.equals(a, e);
            case "CONTAINS":
                return e != null && a.contains(e);
            case "GT":
            case "LT":
            case "GE":
            case "LE":
                return compareOrdered(a, e, actual, expected, normalizeMatchType(matchType));
            case "EQUALS":
            default:
                return Objects.equals(a, e);
        }
    }

    private static boolean compareOrdered(String a, String e, String actualRaw, String expectedRaw, String op) {
        Double actualNum = parseNumber(actualRaw);
        Double expectedNum = parseNumber(expectedRaw);

        int cmp = actualNum != null && expectedNum != null
                ? Double.compare(actualNum, expectedNum)
                : a.compareTo(e == null ? "" : e);

        switch (op) {
            case "GT":
                return cmp > 0;
            case "LT":
                return cmp < 0;
            case "GE":
                return cmp >= 0;
            case "LE":
                return cmp <= 0;
            default:
                return false;
        }
    }

    private static String normalizeMatchType(String matchType) {
        if (matchType == null) {
            return "EQUALS";
        }
        switch (matchType.trim().toLowerCase()) {
            case "=":
            case "==":
            case "equals":
                return "EQUALS";
            case "!=":
            case "not equals":
            case "notequals":
                return "NOT_EQUALS";
            case ">":
            case "greater than":
            case "greaterthan":
                return "GT";
            case "<":
            case "less than":
            case "lessthan":
                return "LT";
            case ">=":
            case "greater than or equals":
                return "GE";
            case "<=":
            case "less than or equals":
                return "LE";
            case "contains":
                return "CONTAINS";
            default:
                return "EQUALS";
        }
    }

    private static Double parseNumber(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
