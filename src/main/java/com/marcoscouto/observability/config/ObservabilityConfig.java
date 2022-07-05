package com.marcoscouto.observability.config;

import brave.internal.codec.HexCodec;
import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class CustomPropagator extends Propagation.Factory implements Propagation<String> {

    @Override
    public List<String> keys() {
        return Arrays.asList("trace-id", "span-id", "parent-id");
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Setter<R, String> setter) {
        return (traceContext, request) -> {
            setter.put(request, "trace-id", traceContext.traceIdString());
            setter.put(request, "span-id", traceContext.spanIdString());
            setter.put(request, "parent-id", traceContext.parentIdString());
        };
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Getter<R, String> getter) {
        return request -> TraceContextOrSamplingFlags.create(TraceContext.newBuilder()
                .traceId(HexCodec.lowerHexToUnsignedLong(
                        Optional.ofNullable(getter.get(request, "trace-id")).orElse(UUID.randomUUID().toString().substring(0, 7))
                ))
                .spanId(HexCodec.lowerHexToUnsignedLong(
                        Optional.ofNullable(getter.get(request, "span-id")).orElse(UUID.randomUUID().toString().substring(0, 7))
                )).parentId(HexCodec.lowerHexToUnsignedLong(
                        Optional.ofNullable(getter.get(request, "parent-id")).orElse(UUID.randomUUID().toString().substring(0, 7))
                ))
                .build());
    }

    @Override
    public <K> Propagation<K> create(KeyFactory<K> keyFactory) {
        return StringPropagationAdapter.create(this, keyFactory);
    }

}