package bitxon.order.client.interceptor;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class RestClientTracingInterceptor implements ClientHttpRequestInterceptor {

    private static final String CORRELATION_HEADER_NAME = "baggage";

    @Value("${management.tracing.baggage.correlation.fields}")
    private final List<String> correlationFields;

    @Value("${management.tracing.baggage.remote-fields}")
    private final List<String> remoteFields;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        var headers = request.getHeaders();
        addTraceHeaders(headers);
        return execution.execute(request, body);
    }

    void addTraceHeaders(HttpHeaders headers) {
        var mdcContext = MDC.getCopyOfContextMap();

        var remoteFieldsValues = remoteFields.stream()
            .collect(toMap(Function.identity(), mdcContext::get));
        remoteFieldsValues.forEach(headers::add);

        var correlationFieldValue = correlationFields.stream()
            .filter(mdcContext::containsKey)
            .map(key -> String.format("%s=%s", key, mdcContext.get(key)))
            .collect(Collectors.joining(","));

        if (!correlationFieldValue.isBlank()) {
            headers.add(CORRELATION_HEADER_NAME, correlationFieldValue);
        }
    }
}
