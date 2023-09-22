package bitxon.order.client.tracing.interceptor;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

@Component
@RequiredArgsConstructor
public class TracingHeadersProvider {


    private static final String CORRELATION_HEADER_NAME = "baggage";

    @Value("${management.tracing.baggage.correlation.fields}")
    private final List<String> correlationFields;

    private final io.micrometer.tracing.brave.bridge.BraveTracer braveTracerBridge;

    public Map<String, String> getTraceHeaders() {
        var tracingContext = braveTracerBridge.currentTraceContext().context();
        var traceId = tracingContext.traceId();
        var parentId = tracingContext.parentId();
        var spanId = tracingContext.spanId();

        var mdcContext = new TreeMap<String, String>(CASE_INSENSITIVE_ORDER);
        mdcContext.putAll(MDC.getCopyOfContextMap());

        var correlationFieldValue = correlationFields.stream()
            .filter(mdcContext::containsKey)
            .map(key -> String.format("%s=%s", key, mdcContext.get(key)))
            .collect(Collectors.joining(","));

        var result = new HashMap<>(braveTracerBridge.getAllBaggage());

        if (!correlationFieldValue.isBlank()) {
            result.put(CORRELATION_HEADER_NAME, correlationFieldValue);
        }
        return result;
    }
}
