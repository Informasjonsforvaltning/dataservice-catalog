package no.fdk.dataservicecatalog.security;

import org.apache.jena.riot.Lang;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class RDFMatcher implements ServerWebExchangeMatcher {

    @Override
    public Mono<MatchResult> matches(final ServerWebExchange exchange) {
        Mono<ServerHttpRequest> request = Mono.just(exchange).map(ServerWebExchange::getRequest);

        return request.map(ServerHttpRequest::getHeaders)
                .filter(h -> acceptHeaderIsRDF(h.get(HttpHeaders.ACCEPT)))
                .flatMap($ -> MatchResult.match())
                .switchIfEmpty(MatchResult.notMatch());
    }

    private boolean acceptHeaderIsRDF(List<String> accept) {
        if (accept == null) return false;
        else if (accept.contains(Lang.TURTLE.getHeaderString())) return true;
        else if (accept.contains("text/n3")) return true;
        else if (accept.contains(Lang.RDFJSON.getHeaderString())) return true;
        else if (accept.contains(Lang.JSONLD.getHeaderString())) return true;
        else if (accept.contains(Lang.RDFXML.getHeaderString())) return true;
        else if (accept.contains(Lang.NTRIPLES.getHeaderString())) return true;
        else if (accept.contains(Lang.NQUADS.getHeaderString())) return true;
        else if (accept.contains(Lang.TRIG.getHeaderString())) return true;
        else return accept.contains(Lang.TRIX.getHeaderString());
    }
}
