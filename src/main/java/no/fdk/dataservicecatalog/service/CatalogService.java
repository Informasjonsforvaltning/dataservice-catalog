package no.fdk.dataservicecatalog.service;

import lombok.RequiredArgsConstructor;
import no.fdk.dataservicecatalog.model.Catalog;
import no.fdk.dataservicecatalog.model.DataService;
import no.fdk.dataservicecatalog.repository.DataServiceMongoRepository;
import no.fdk.dataservicecatalog.security.SecurityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CatalogService {

    private final DataServiceMongoRepository dataServiceMongoRepository;

    public Mono<List<Catalog>> getAllPermittedCatalogs() {
        return dataServiceCountForPermittedCatalogs().mapNotNull(CatalogService::catalogsFromMap);
    }

    private Mono<Map<String, Integer>> dataServiceCountForPermittedCatalogs() {
        HashMap<String, Integer> countByCatalogId = new HashMap<>();
        return ReactiveSecurityContextHolder.getContext().flux()
                .flatMap(this::permittedDataServices)
                .doOnNext(dataService -> incCount(countByCatalogId, dataService.getOrganizationId()))
                .then()
                .thenReturn(countByCatalogId);
    }

    private Flux<DataService> permittedDataServices(SecurityContext securityContext) {
        String authorities = SecurityUtils.getAuthorities(securityContext);
        if (SecurityUtils.isSysAdmin(authorities)) {
            return dataServiceMongoRepository.findAll();
        } else {
            return dataServiceMongoRepository.findByOrganizationIdIn(SecurityUtils.authCatalogs(authorities));
        }
    }

    private static List<Catalog> catalogsFromMap(Map<String, Integer> map) {
        return map.keySet().stream()
                .map(id -> Catalog.builder().id(id).dataServiceCount(map.get(id)).build())
                .collect(Collectors.toList());
    }

    private static void incCount(HashMap<String, Integer> map, String id) {
        if (map.containsKey(id)) {
            map.put(id, map.get(id) + 1);
        } else {
            map.put(id, 1);
        }
    }

}
