package com.marcoscouto.observability.config;

import brave.internal.codec.HexCodec;
import brave.internal.propagation.StringPropagationAdapter;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;

@Component
class CustomPropagator extends Propagation.Factory implements Propagation<String> {

    private final String TRACE_ID = "trace-id";
    private final String SPAN_ID = "span-id";

    @Override
    public List<String> keys() {
        return asList(TRACE_ID, SPAN_ID);
    }

    @Override
    public <R> TraceContext.Injector<R> injector(Setter<R, String> setter) {
        return (traceContext, request) -> {
            setter.put(request, TRACE_ID, traceContext.traceIdString());
            setter.put(request, SPAN_ID, traceContext.spanIdString());
        };
    }

    @Override
    public <R> TraceContext.Extractor<R> extractor(Getter<R, String> getter) {
        return request -> TraceContextOrSamplingFlags.create(TraceContext.newBuilder()
                .traceId(formatCodec(handleHeaderValue(getter.get(request, TRACE_ID))))
                .spanId(formatCodec(handleHeaderValue(getter.get(request, SPAN_ID))))
                .build());
    }

    @Override
    public <K> Propagation<K> create(KeyFactory<K> keyFactory) {
        return StringPropagationAdapter.create(this, keyFactory);
    }

    private String handleHeaderValue(String value){
        return Optional.ofNullable(value).orElse(UUID.randomUUID().toString().substring(0, 7));
    }

    private long formatCodec(String value){
        return HexCodec.lowerHexToUnsignedLong(value);
    }

}
