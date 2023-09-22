package bitxon.order.client.tracing.interceptor;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class TracingHeadersProvider {

    private static final String CORRELATION_HEADER_NAME = "baggage";

    @Value("${management.tracing.baggage.correlation.fields}")
    private final List<String> correlationFields;

    @Value("${management.tracing.baggage.remote-fields}")
    private final List<String> remoteFields;


    public Map<String, String> getTraceHeaders() {
        var mdcContext = MDC.getCopyOfContextMap();

        var remoteFieldsValues = remoteFields.stream()
            .collect(toMap(Function.identity(), mdcContext::get));

        var correlationFieldValue = correlationFields.stream()
            .filter(mdcContext::containsKey)
            .map(key -> String.format("%s=%s", key, mdcContext.get(key)))
            .collect(Collectors.joining(","));

        var result = new HashMap<>(remoteFieldsValues);
        if (!correlationFieldValue.isBlank()) {
            result.put(CORRELATION_HEADER_NAME, correlationFieldValue);
        }
        return result;
    }
}
