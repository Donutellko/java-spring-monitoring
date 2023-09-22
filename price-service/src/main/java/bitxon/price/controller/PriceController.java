package bitxon.price.controller;

import bitxon.price.api.Price;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/price")
public class PriceController {
    private static final Logger LOG = LoggerFactory.getLogger(PriceController.class);

    @GetMapping("/{identifier}")
    public Price getPrice(HttpServletRequest request,
                          @PathVariable("identifier") String identifier) {
        LOG.info("Handling: GET /price/{}", identifier);
        var headers = Collections.list(request.getHeaderNames()).stream()
            .collect(Collectors.toMap(Function.identity(),
                (header) -> Collections.list(request.getHeaders(header))));
        headers.forEach((key, value) -> {
            var headerValues = String.join(", ", value);
            System.out.println(String.format("%s = %s", key, headerValues));
        });

        var amount = switch (identifier) {
            case "ipad" -> 499;
            case "iphone" -> 899;
            case "macbook" -> 1299;
            default -> 100;
        };
        return new Price(amount);

    }
}
