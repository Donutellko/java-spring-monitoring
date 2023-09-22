package bitxon.order.client.tracing.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientTracingInterceptor implements RequestInterceptor {

    private final TracingHeadersProvider tracingHeadersProvider;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        tracingHeadersProvider.getTraceHeaders()
            .forEach(requestTemplate::header);
    }
}
